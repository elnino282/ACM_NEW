package org.example.QuanLyMuaVu.Repository;

import org.example.QuanLyMuaVu.Entity.StockLocation;
import org.example.QuanLyMuaVu.Entity.StockMovement;
import org.example.QuanLyMuaVu.Entity.SupplyLot;
import org.example.QuanLyMuaVu.Entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Integer> {

    @Query("""
            select coalesce(sum(
                case when m.movementType = org.example.QuanLyMuaVu.Enums.StockMovementType.IN then m.quantity
                     when m.movementType = org.example.QuanLyMuaVu.Enums.StockMovementType.OUT then -m.quantity
                     else 0 end
            ), 0)
            from StockMovement m
            where m.supplyLot = :lot
              and m.warehouse = :warehouse
              and (:location is null or m.location = :location)
            """)
    BigDecimal calculateOnHandQuantity(
            @Param("lot") SupplyLot lot,
            @Param("warehouse") Warehouse warehouse,
            @Param("location") StockLocation location
    );
}

