package org.example.QuanLyMuaVu.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.QuanLyMuaVu.DTO.Common.PageResponse;
import org.example.QuanLyMuaVu.DTO.Request.FarmCreateRequest;
import org.example.QuanLyMuaVu.DTO.Request.FarmUpdateRequest;
import org.example.QuanLyMuaVu.DTO.Response.FarmDetailResponse;
import org.example.QuanLyMuaVu.DTO.Response.FarmResponse;
import org.example.QuanLyMuaVu.Entity.Farm;
import org.example.QuanLyMuaVu.Entity.User;
import org.example.QuanLyMuaVu.Exception.AppException;
import org.example.QuanLyMuaVu.Exception.ErrorCode;
import org.example.QuanLyMuaVu.Mapper.FarmMapper;
import org.example.QuanLyMuaVu.Repository.FarmRepository;
import org.example.QuanLyMuaVu.Repository.ProvinceRepository;
import org.example.QuanLyMuaVu.Repository.UserRepository;
import org.example.QuanLyMuaVu.Repository.WardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing farms with soft delete, restore, and audit logging
 * capabilities.
 * 
 * Key Features:
 * - Soft delete (deactivate) for farms, preserving data for potential
 * restoration
 * - Hard delete (permanent) for complete removal (service layer only, not
 * exposed via API)
 * - Restore functionality with foreign key integrity validation
 * - Comprehensive audit logging for all critical operations
 * - Unique constraint check only on ACTIVE farms to allow name reuse after soft
 * delete
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class FarmService {

    FarmRepository farmRepository;
    UserRepository userRepository;
    ProvinceRepository provinceRepository;
    WardRepository wardRepository;
    FarmMapper farmMapper;
    FarmAccessService farmAccessService;
    AuditLogService auditLogService;

    public PageResponse<FarmResponse> getMyFarms(String keyword, Boolean active, int page, int size) {
        User currentUser = farmAccessService.getCurrentUser();
        List<Farm> farms = farmRepository.findAllByOwner(currentUser);

        String searchKeyword = keyword != null ? keyword.trim().toLowerCase() : "";
        boolean filterActive = active != null;

        List<FarmResponse> items = farms.stream()
                .filter(farm -> !filterActive || active.equals(farm.getActive()))
                .filter(farm -> searchKeyword.isEmpty()
                        || (farm.getName() != null && farm.getName().toLowerCase().contains(searchKeyword)))
                .sorted((f1, f2) -> Integer.compare(
                        f2.getId() != null ? f2.getId() : 0,
                        f1.getId() != null ? f1.getId() : 0))
                .map(farmMapper::toResponse)
                .toList();

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, items.size());
        List<FarmResponse> pageItems = fromIndex >= items.size() ? List.of() : items.subList(fromIndex, toIndex);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<FarmResponse> pageData = new PageImpl<>(pageItems, pageable, items.size());

        return PageResponse.of(pageData, pageItems);
    }

    public FarmDetailResponse create(FarmCreateRequest request) {
        User currentUser = farmAccessService.getCurrentUser();

        // CRITICAL FIX: Only check duplicate names among ACTIVE farms
        // This allows users to create a new farm with the same name after soft-deleting
        // the old one
        if (farmRepository.existsByOwnerAndNameIgnoreCaseAndActiveTrue(currentUser, request.getName())) {
            throw new AppException(ErrorCode.FARM_NAME_EXISTS);
        }

        // Validate Province and Ward (strict validation for create)
        validateProvinceAndWardForCreate(request.getProvinceId(), request.getWardId());

        Farm farm = farmMapper.toEntity(request);
        farm.setOwner(currentUser);
        farm.setActive(true);

        Farm saved = farmRepository.save(farm);

        // Log farm creation for audit trail
        auditLogService.logFarmOperation(saved, "CREATE", currentUser.getUsername(), null, null);

        return farmMapper.toDetailResponse(saved);
    }

    public FarmDetailResponse getMyFarm(Integer id) {
        User currentUser = farmAccessService.getCurrentUser();
        Farm farm = farmRepository.findByIdAndOwner(id, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.FARM_NOT_FOUND));
        return farmMapper.toDetailResponse(farm);
    }

    public FarmDetailResponse update(Integer id, FarmUpdateRequest request) {
        User currentUser = farmAccessService.getCurrentUser();
        Farm farm = farmRepository.findByIdAndOwner(id, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.FARM_NOT_FOUND));

        // CRITICAL FIX: Only check name uniqueness among ACTIVE farms if name is being
        // updated
        if (request.getName() != null
                && !farm.getName().equalsIgnoreCase(request.getName())
                && farmRepository.existsByOwnerAndNameIgnoreCaseAndActiveTrue(currentUser, request.getName())) {
            throw new AppException(ErrorCode.FARM_NAME_EXISTS);
        }

        // Validate Province and Ward if being updated (flexible validation)
        validateProvinceAndWardForUpdate(request.getProvinceId(), request.getWardId());

        farmMapper.updateEntity(farm, request);
        Farm saved = farmRepository.save(farm);

        // Log farm update
        auditLogService.logFarmOperation(saved, "UPDATE", currentUser.getUsername(), null, null);

        return farmMapper.toDetailResponse(saved);
    }

    /**
     * Soft delete (deactivate) a farm.
     * Sets active = false but preserves all data for potential restoration.
     * 
     * IMPORTANT: This operation does NOT cascade to child records (plots, seasons).
     * - Plots and Seasons associated with this farm will remain UNCHANGED.
     * - They will still reference this farm, but the farm will be inactive.
     * - Frontend should hide/filter data from inactive farms as needed.
     * 
     * Design Rationale:
     * - Preserves data integrity and historical records
     * - Allows farm restoration without data loss
     * - Prevents accidental mass deactivation of child records
     * 
     * Future Enhancement: If business requires cascade deactivation,
     * implement a separate "deactivate farm and all children" operation.
     * 
     * @param id        Farm ID
     * @param reason    Optional reason for deactivation (for audit trail)
     * @param ipAddress IP address of the request (for security audit)
     */
    public void deactivate(Integer id, String reason, String ipAddress) {
        User currentUser = farmAccessService.getCurrentUser();
        Farm farm = farmRepository.findByIdAndOwner(id, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.FARM_NOT_FOUND));

        // Check if already inactive
        if (Boolean.FALSE.equals(farm.getActive())) {
            throw new AppException(ErrorCode.FARM_ALREADY_INACTIVE);
        }

        // Log BEFORE deactivation for audit trail
        auditLogService.logFarmOperation(farm, "SOFT_DELETE", currentUser.getUsername(), reason, ipAddress);

        farm.setActive(false);
        farmRepository.save(farm);
    }

    /**
     * Restore a previously deactivated farm.
     * Validates foreign key integrity before restoration.
     * 
     * @param id        Farm ID
     * @param ipAddress IP address of the request
     * @return FarmDetailResponse with warning flags for orphaned data
     */
    public FarmDetailResponse restore(Integer id, String ipAddress) {
        User currentUser = farmAccessService.getCurrentUser();
        Farm farm = farmRepository.findByIdAndOwner(id, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.FARM_NOT_FOUND));

        // Check if already active
        if (Boolean.TRUE.equals(farm.getActive())) {
            throw new AppException(ErrorCode.FARM_ALREADY_ACTIVE);
        }

        // Validate foreign key integrity before restore
        validateForeignKeyIntegrity(farm, currentUser);

        // Log BEFORE restoration
        auditLogService.logFarmOperation(farm, "RESTORE", currentUser.getUsername(),
                "Farm restored from inactive state", ipAddress);

        farm.setActive(true);
        Farm saved = farmRepository.save(farm);

        // Build response with warning flags
        FarmDetailResponse response = farmMapper.toDetailResponse(saved);

        // Check for orphaned plots/seasons to warn user
        boolean hasPlots = farmRepository.hasPlots(id);
        boolean hasSeasons = farmRepository.hasSeasons(id);

        response.setHasOrphanedPlots(hasPlots);
        response.setHasOrphanedSeasons(hasSeasons);

        return response;
    }

    /**
     * Permanently delete a farm from the database (HARD DELETE).
     * 
     * WARNING: This is a destructive operation that cannot be undone.
     * Only allowed if farm has NO child records (plots, seasons).
     * 
     * This method is implemented but NOT exposed via API endpoint.
     * Requires ADMIN role authorization (to be implemented).
     * 
     * @param id        Farm ID
     * @param reason    Mandatory reason for permanent deletion
     * @param ipAddress IP address of the request
     */
    public void permanentDelete(Integer id, String reason, String ipAddress) {
        User currentUser = farmAccessService.getCurrentUser();
        Farm farm = farmRepository.findByIdAndOwner(id, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.FARM_NOT_FOUND));

        // Validate no child records exist
        validateNoChildRecords(farm);

        // CRITICAL: Log BEFORE deletion with full snapshot using MANDATORY transaction
        // If audit fails, the transaction will rollback and the delete will not occur
        auditLogService.logFarmOperationCritical(farm, "HARD_DELETE", currentUser.getUsername(), reason, ipAddress);

        // Permanent deletion - only happens if audit succeeds
        farmRepository.delete(farm);
    }

    /**
     * Validate that farm has no child records (plots or seasons).
     * Throws exception if child records exist.
     */
    private void validateNoChildRecords(Farm farm) {
        // Use optimized repository queries instead of loading all data into memory
        boolean hasPlots = farmRepository.hasPlots(farm.getId());
        boolean hasSeasons = farmRepository.hasSeasons(farm.getId());

        if (hasPlots || hasSeasons) {
            throw new AppException(ErrorCode.FARM_HAS_CHILD_RECORDS);
        }
    }

    /**
     * Validate foreign key integrity before restoring a farm.
     * Ensures Province, Ward, and Owner still exist and are valid.
     */
    private void validateForeignKeyIntegrity(Farm farm, User owner) {
        // Check Province exists
        if (farm.getProvince() != null && !provinceRepository.existsById(farm.getProvince().getId())) {
            throw new AppException(ErrorCode.FARM_CANNOT_RESTORE);
        }

        // Check Ward exists
        if (farm.getWard() != null && !wardRepository.existsById(farm.getWard().getId())) {
            throw new AppException(ErrorCode.FARM_CANNOT_RESTORE);
        }

        // Check Owner account is still active
        User farmOwner = userRepository.findById(owner.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (farmOwner.getStatus() != org.example.QuanLyMuaVu.Enums.UserStatus.ACTIVE) {
            throw new AppException(ErrorCode.FARM_OWNER_INACTIVE);
        }
    }

    /**
     * Strict validation for creating a farm - both Province and Ward are required.
     */
    private void validateProvinceAndWardForCreate(Integer provinceId, Integer wardId) {
        if (provinceId == null) {
            throw new AppException(ErrorCode.PROVINCE_REQUIRED);
        }
        if (wardId == null) {
            throw new AppException(ErrorCode.WARD_REQUIRED);
        }

        // Validate Province exists
        if (!provinceRepository.existsById(provinceId)) {
            throw new AppException(ErrorCode.PROVINCE_NOT_FOUND);
        }

        // Validate Ward exists
        var ward = wardRepository.findById(wardId)
                .orElseThrow(() -> new AppException(ErrorCode.WARD_NOT_FOUND));

        // Validate that Ward belongs to Province
        if (!ward.getProvince().getId().equals(provinceId)) {
            throw new AppException(ErrorCode.WARD_NOT_IN_PROVINCE);
        }
    }

    /**
     * Flexible validation for updating a farm - Province and Ward are optional.
     * If provided, they must exist and Ward must belong to Province.
     */
    private void validateProvinceAndWardForUpdate(Integer provinceId, Integer wardId) {
        if (provinceId != null) {
            if (!provinceRepository.existsById(provinceId)) {
                throw new AppException(ErrorCode.PROVINCE_NOT_FOUND);
            }
        }

        if (wardId != null) {
            var ward = wardRepository.findById(wardId)
                    .orElseThrow(() -> new AppException(ErrorCode.WARD_NOT_FOUND));

            // If both province and ward are provided, validate that ward belongs to
            // province
            if (provinceId != null && !ward.getProvince().getId().equals(provinceId)) {
                throw new AppException(ErrorCode.WARD_NOT_IN_PROVINCE);
            }
        }
    }
}
