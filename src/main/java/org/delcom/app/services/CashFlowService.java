package org.delcom.app.services;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CashFlowService {

    @Autowired
    private CashFlowRepository cashFlowRepository;

    // Ambil semua data milik user tertentu
    public List<CashFlow> getAllCashFlows(UUID userId, String keyword) {
        return cashFlowRepository.findByUserId(userId);
    }

    // Tambah Data Baru
    public CashFlow createCashFlow(UUID userId, String type, String source, String label, Integer amount, String description) {
        CashFlow flow = new CashFlow(
            userId, 
            type, 
            source, 
            label, 
            Double.valueOf(amount), // Konversi int ke double
            description
        );
        return cashFlowRepository.save(flow);
    }

    // Update Data
    public CashFlow updateCashFlow(UUID id, UUID userId, String type, String source, String label, Integer amount, String description) {
        CashFlow flow = cashFlowRepository.findById(id).orElse(null);
        
        // Pastikan data ada dan milik user yang benar
        if (flow != null && flow.getUserId().equals(userId)) {
            flow.setType(type);
            flow.setSource(source);
            flow.setLabel(label);
            flow.setAmount(Double.valueOf(amount));
            flow.setDescription(description);
            return cashFlowRepository.save(flow);
        }
        return null;
    }

    // Hapus Data
    public boolean deleteCashFlow(UUID id, UUID userId) {
        CashFlow flow = cashFlowRepository.findById(id).orElse(null);
        if (flow != null && flow.getUserId().equals(userId)) {
            cashFlowRepository.delete(flow);
            return true;
        }
        return false;
    }
}