package org.delcom.app.repositories;

import org.delcom.app.entities.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CashFlowRepository extends JpaRepository<CashFlow, UUID> {
    // Mencari data berdasarkan User ID
    List<CashFlow> findByUserId(UUID userId);
}