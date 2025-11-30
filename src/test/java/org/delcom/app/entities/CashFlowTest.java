package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class CashFlowTest {

    @Test
    void testCashFlowEntity() {
        // 1. Test Constructor Custom
        UUID userId = UUID.randomUUID();
        String type = "Pemasukan";
        String source = "Dompet";
        String label = "Gaji";
        Double amount = 5000000.0;
        String description = "Gaji Bulanan";

        CashFlow cashFlow = new CashFlow(userId, type, source, label, amount, description);

        // 2. Test Getters (Pastikan datanya masuk benar)
        assertNotNull(cashFlow);
        assertEquals(userId, cashFlow.getUserId());
        assertEquals(type, cashFlow.getType());
        assertEquals(source, cashFlow.getSource());
        assertEquals(label, cashFlow.getLabel());
        assertEquals(amount, cashFlow.getAmount());
        assertEquals(description, cashFlow.getDescription());

        // 3. Test Setters (Coba ubah data, lalu cek lagi)
        String newSource = "Bank";
        cashFlow.setSource(newSource);
        assertEquals(newSource, cashFlow.getSource());

        cashFlow.setType("Pengeluaran");
        assertEquals("Pengeluaran", cashFlow.getType());

        cashFlow.setLabel("Jajan");
        assertEquals("Jajan", cashFlow.getLabel());

        cashFlow.setAmount(10000.0);
        assertEquals(10000.0, cashFlow.getAmount());

        cashFlow.setDescription("Beli Bakso");
        assertEquals("Beli Bakso", cashFlow.getDescription());

        UUID newId = UUID.randomUUID();
        cashFlow.setId(newId);
        assertEquals(newId, cashFlow.getId());
        
        UUID newUserId = UUID.randomUUID();
        cashFlow.setUserId(newUserId);
        assertEquals(newUserId, cashFlow.getUserId());
    }
    
    @Test
    void testEmptyConstructor() {
        // 4. Test Constructor Kosong
        CashFlow cashFlow = new CashFlow();
        assertNotNull(cashFlow);
    }
}