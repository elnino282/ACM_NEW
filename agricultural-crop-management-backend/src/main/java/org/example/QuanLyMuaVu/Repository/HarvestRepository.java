package org.example.QuanLyMuaVu.Repository;

import org.example.QuanLyMuaVu.Entity.Harvest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HarvestRepository extends JpaRepository<Harvest, Integer> {

    List<Harvest> findByHarvestDateBetween(LocalDate start, LocalDate end);

    List<Harvest> findAllBySeason_Id(Integer seasonId);

    boolean existsBySeason_Id(Integer seasonId);
}
