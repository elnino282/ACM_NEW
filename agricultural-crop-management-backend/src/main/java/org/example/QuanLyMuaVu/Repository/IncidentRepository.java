package org.example.QuanLyMuaVu.Repository;

import org.example.QuanLyMuaVu.Entity.Incident;
import org.example.QuanLyMuaVu.Entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Integer> {

    List<Incident> findAllBySeason(Season season);
}

