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
}
