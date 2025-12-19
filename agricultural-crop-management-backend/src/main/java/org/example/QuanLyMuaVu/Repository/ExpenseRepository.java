package org.example.QuanLyMuaVu.Repository;

import org.example.QuanLyMuaVu.Entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    List<Expense> findByItemNameContainingIgnoreCase(String itemName);

    List<Expense> findAllBySeason_Id(Integer seasonId);

    List<Expense> findAllBySeason_IdAndExpenseDateBetween(Integer seasonId, LocalDate from, LocalDate to);

    boolean existsBySeason_Id(Integer seasonId);
}
