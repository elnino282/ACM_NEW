package org.example.QuanLyMuaVu.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.QuanLyMuaVu.DTO.Common.ApiResponse;
import org.example.QuanLyMuaVu.DTO.Common.PageResponse;
import org.example.QuanLyMuaVu.DTO.Request.FarmCreateRequest;
import org.example.QuanLyMuaVu.DTO.Request.FarmUpdateRequest;
import org.example.QuanLyMuaVu.DTO.Response.FarmDetailResponse;
import org.example.QuanLyMuaVu.DTO.Response.FarmResponse;
import org.example.QuanLyMuaVu.Service.FarmService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * REST endpoints for managing farms of the current farmer.
 * Farms are the root aggregate for plots, seasons, stock, harvests and
 * incidents
 * across business flows [2], [3], [4], [5] and [10].
 */

@RestController
@RequestMapping("/api/v1/farms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('FARMER')")
public class FarmController {

        FarmService farmService;
        org.example.QuanLyMuaVu.Config.FarmSecurityProperties farmSecurityProperties;

        @Operation(summary = "List farms", description = "Get list of farms for the current authenticated farmer")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Success"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
        })
        @GetMapping
        public ApiResponse<PageResponse<FarmResponse>> listFarms(
                        @Parameter(description = "Keyword to search by farm name") @RequestParam(value = "keyword", required = false) String keyword,
                        @Parameter(description = "Filter by active status") @RequestParam(value = "active", required = false) Boolean active,
                        @Parameter(description = "Page index (0-based)") @RequestParam(value = "page", defaultValue = "0") int page,
                        @Parameter(description = "Page size") @RequestParam(value = "size", defaultValue = "20") int size) {
                return ApiResponse.success(farmService.getMyFarms(keyword, active, page, size));
        }

        @Operation(summary = "Create farm", description = "Create a new farm for the current farmer")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Created"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict")
        })
        @PostMapping
        public ApiResponse<FarmDetailResponse> createFarm(@Valid @RequestBody FarmCreateRequest request) {
                return ApiResponse.success(farmService.create(request));
        }

        @Operation(summary = "Get farm detail", description = "Get farm detail for the current farmer")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Success"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found")
        })
        @GetMapping("/{id}")
        public ApiResponse<FarmDetailResponse> getFarm(@PathVariable Integer id) {
                return ApiResponse.success(farmService.getMyFarm(id));
        }

        @Operation(summary = "Update farm", description = "Update farm information")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Success"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict")
        })
        @PutMapping("/{id}")
        public ApiResponse<FarmDetailResponse> updateFarm(
                        @PathVariable Integer id,
                        @Valid @RequestBody FarmUpdateRequest request) {
                return ApiResponse.success(farmService.update(id, request));
        }

        @Operation(summary = "Soft delete/deactivate farm", description = "Deactivates a farm by setting active = false. The farm data is preserved and can be restored later. "
                        +
                        "IMPORTANT: This operation does NOT cascade to plots or seasons. Child records remain unchanged but will reference an inactive farm. "
                        +
                        "This operation is logged in the audit trail.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Farm deactivated successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request - Farm is already inactive"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found")
        })
        @DeleteMapping("/{id}")
        public ApiResponse<Void> deactivateFarm(
                        @PathVariable Integer id,
                        @Parameter(description = "Optional reason for deactivation (for audit trail)") @RequestParam(value = "reason", required = false) String reason,
                        jakarta.servlet.http.HttpServletRequest request) {
                String ipAddress = getClientIpAddress(request);
                farmService.deactivate(id, reason, ipAddress);
                return ApiResponse.success(null);
        }

        @Operation(summary = "Restore deactivated farm", description = "Restores a previously deactivated farm by setting active = true. "
                        +
                        "Validates that Province, Ward, and Owner still exist before restoration. " +
                        "Response includes warning flags if the farm has orphaned plots or seasons.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Farm restored successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request - Farm is already active or foreign key constraints violated"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found")
        })
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "No request body needed for restore operation")
        @org.springframework.web.bind.annotation.PatchMapping("/{id}/restore")
        public ApiResponse<FarmDetailResponse> restoreFarm(
                        @PathVariable Integer id,
                        jakarta.servlet.http.HttpServletRequest request) {
                String ipAddress = getClientIpAddress(request);
                return ApiResponse.success(farmService.restore(id, ipAddress));
        }

        /**
         * Extract client IP address from HTTP request with security hardening.
         * 
         * Security Strategy:
         * 1. Only trust X-Forwarded-For header if trustProxyHeaders is enabled
         * 2. Only trust X-Forwarded-For if request comes from a trusted proxy IP
         * 3. Otherwise, use direct remoteAddr (prevents client spoofing)
         * 
         * Configuration (application.yaml):
         * 
         * <pre>
         * farm:
         *   security:
         *     trust-proxy-headers: true  # Enable only in production behind proxy
         *     trusted-proxy-ips:         # Optional: restrict by proxy IP
         *       - "10.0.0.1"
         *       - "172.16.0.0/12"
         * </pre>
         * 
         * Why this matters:
         * - Without proper validation, malicious clients can spoof X-Forwarded-For
         * - This would bypass IP-based audit trails and security logging
         * - Production deployments behind AWS ELB, nginx, etc. need this enabled
         * - Development/testing should keep it disabled to prevent spoofing
         */
        private String getClientIpAddress(jakarta.servlet.http.HttpServletRequest request) {
                String remoteAddr = request.getRemoteAddr();

                // Only trust X-Forwarded-For if configured to do so
                if (!farmSecurityProperties.isTrustProxyHeaders()) {
                        return remoteAddr;
                }

                // Check if request is from a trusted proxy
                if (!farmSecurityProperties.isTrustedProxy(remoteAddr)) {
                        // Request is not from trusted proxy, don't trust X-Forwarded-For
                        return remoteAddr;
                }

                // Request is from trusted proxy, we can trust X-Forwarded-For
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                        // X-Forwarded-For can contain multiple IPs (client, proxy1, proxy2, ...)
                        // Get the first IP which is the original client
                        return xForwardedFor.split(",")[0].trim();
                }

                // No X-Forwarded-For header, use remote address
                return remoteAddr;
        }
}
