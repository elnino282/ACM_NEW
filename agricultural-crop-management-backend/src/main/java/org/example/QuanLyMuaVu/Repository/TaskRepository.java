package org.example.QuanLyMuaVu.Repository;

import org.example.QuanLyMuaVu.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByTitleContainingIgnoreCase(String title);

    List<Task> findAllBySeason_Id(Integer seasonId);

    boolean existsBySeason_Id(Integer seasonId);
}
