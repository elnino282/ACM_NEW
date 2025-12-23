package org.example.QuanLyMuaVu.Repository;

import org.example.QuanLyMuaVu.Entity.Farm;
import org.example.QuanLyMuaVu.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmRepository extends JpaRepository<Farm, Integer> {

    List<Farm> findAllByOwner(User owner);

    Optional<Farm> findByIdAndOwner(Integer id, User owner);

    boolean existsByOwnerAndNameIgnoreCase(User owner, String name);

    /**
     * Check if an active farm with the same name exists for the owner.
     * Used to prevent duplicate farm names for ACTIVE farms only.
     * This allows users to create a new farm with the same name after soft-deleting
     * the old one.
     */
    boolean existsByOwnerAndNameIgnoreCaseAndActiveTrue(User owner, String name);

    /**
     * Check if a farm has any plots.
     * More efficient than loading all plots into memory.
     */
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(p) > 0 FROM Plot p WHERE p.farm.id = :farmId")
    boolean hasPlots(@org.springframework.data.repository.query.Param("farmId") Integer farmId);

    /**
     * Check if a farm has any seasons (through plots).
     * More efficient than loading all plots and seasons into memory.
     */
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(s) > 0 FROM Season s WHERE s.plot.farm.id = :farmId")
    boolean hasSeasons(@org.springframework.data.repository.query.Param("farmId") Integer farmId);
}
