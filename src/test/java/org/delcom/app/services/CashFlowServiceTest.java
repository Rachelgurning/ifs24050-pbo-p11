package org.delcom.app.services;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CashFlowServiceTest {

    @Mock
    private CashFlowRepository cashFlowRepository;

    @InjectMocks
    private CashFlowService cashFlowService;

    // --- 1. TEST GET ALL ---
    @Test
    void testGetAllCashFlows() {
        UUID userId = UUID.randomUUID();
        List<CashFlow> dummyList = new ArrayList<>();
        dummyList.add(new CashFlow());

        when(cashFlowRepository.findByUserId(userId)).thenReturn(dummyList);

        List<CashFlow> result = cashFlowService.getAllCashFlows(userId, null);
        
        assertEquals(1, result.size());
        verify(cashFlowRepository).findByUserId(userId);
    }

    // --- 2. TEST CREATE ---
    @Test
    void testCreateCashFlow() {
        UUID userId = UUID.randomUUID();
        CashFlow dummyFlow = new CashFlow();
        dummyFlow.setAmount(10000.0);

        when(cashFlowRepository.save(any(CashFlow.class))).thenReturn(dummyFlow);

        CashFlow result = cashFlowService.createCashFlow(userId, "Masuk", "Dompet", "Gaji", 10000, "Ket");
        
        assertNotNull(result);
        assertEquals(10000.0, result.getAmount());
    }

    // --- 3. TEST UPDATE: SUKSES (Data Ada & User Cocok) ---
    @Test
    void testUpdateCashFlow_Success() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        CashFlow existingFlow = new CashFlow();
        existingFlow.setId(id);
        existingFlow.setUserId(userId); // User ID Cocok

        when(cashFlowRepository.findById(id)).thenReturn(Optional.of(existingFlow));
        when(cashFlowRepository.save(any(CashFlow.class))).thenReturn(existingFlow);

        CashFlow result = cashFlowService.updateCashFlow(id, userId, "Keluar", "Bank", "Jajan", 5000, "Baru");

        assertNotNull(result);
    }

    // --- 4. TEST UPDATE: GAGAL (Data Tidak Ada / NULL) ---
    // Ini untuk menghijaukan diamond kuning "flow != null"
    @Test
    void testUpdateCashFlow_Fail_NotFound() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(cashFlowRepository.findById(id)).thenReturn(Optional.empty()); // Data Kosong

        CashFlow result = cashFlowService.updateCashFlow(id, userId, "Tipe", "Src", "Lbl", 100, "Desc");
        
        assertNull(result); // Harusnya null
    }

    // --- 5. TEST UPDATE: GAGAL (User Beda) ---
    // Ini untuk menghijaukan diamond kuning "flow.getUserId().equals(userId)"
    @Test
    void testUpdateCashFlow_Fail_WrongUser() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID(); // User Lain

        CashFlow existingFlow = new CashFlow();
        existingFlow.setId(id);
        existingFlow.setUserId(otherUserId); // Punya orang lain

        when(cashFlowRepository.findById(id)).thenReturn(Optional.of(existingFlow));

        CashFlow result = cashFlowService.updateCashFlow(id, userId, "Tipe", "Src", "Lbl", 100, "Desc");
        
        assertNull(result); // Harusnya null karena akses ditolak
    }

    // --- 6. TEST DELETE: SUKSES ---
    @Test
    void testDeleteCashFlow_Success() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CashFlow flow = new CashFlow();
        flow.setId(id);
        flow.setUserId(userId); // User Cocok

        when(cashFlowRepository.findById(id)).thenReturn(Optional.of(flow));

        boolean isDeleted = cashFlowService.deleteCashFlow(id, userId);
        
        assertTrue(isDeleted);
        verify(cashFlowRepository).delete(flow);
    }

    // --- 7. TEST DELETE: GAGAL (Data Tidak Ada) ---
    @Test
    void testDeleteCashFlow_Fail_NotFound() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(cashFlowRepository.findById(id)).thenReturn(Optional.empty()); // Kosong

        boolean isDeleted = cashFlowService.deleteCashFlow(id, userId);
        
        assertFalse(isDeleted);
        verify(cashFlowRepository, never()).delete(any());
    }

    // --- 8. TEST DELETE: GAGAL (User Beda) ---
    @Test
    void testDeleteCashFlow_Fail_WrongUser() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID(); // Saya
        UUID otherUserId = UUID.randomUUID(); // Orang lain

        CashFlow flow = new CashFlow();
        flow.setId(id);
        flow.setUserId(otherUserId); // Punya orang lain

        when(cashFlowRepository.findById(id)).thenReturn(Optional.of(flow));

        boolean isDeleted = cashFlowService.deleteCashFlow(id, userId);
        
        assertFalse(isDeleted); // Gagal hapus
        verify(cashFlowRepository, never()).delete(any());
    }
}