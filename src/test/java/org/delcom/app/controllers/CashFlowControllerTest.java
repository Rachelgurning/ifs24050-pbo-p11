package org.delcom.app.controllers;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CashFlowControllerTest {

    @Mock
    private CashFlowService cashFlowService;

    @Mock
    private AuthContext authContext;

    @Mock
    private Model model;

    @InjectMocks
    private CashFlowController cashFlowController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail("test@email.com");
    }

    // --- 1. TEST LIST ---
    @Test
    void testListCashFlows_NotLoggedIn() {
        when(authContext.isAuthenticated()).thenReturn(false);
        String view = cashFlowController.listCashFlows(model);
        assertEquals("redirect:/login", view);
    }

    @Test
    void testListCashFlows_LoggedIn() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(cashFlowService.getAllCashFlows(any(), any())).thenReturn(new ArrayList<>());

        String view = cashFlowController.listCashFlows(model);
        assertEquals("cashflow-list", view);
        verify(model).addAttribute(eq("listCashFlow"), any());
    }

    // --- 2. TEST ADD FORM ---
    @Test
    void testShowAddForm_NotLoggedIn() {
        when(authContext.isAuthenticated()).thenReturn(false);
        String view = cashFlowController.showAddForm(model);
        assertEquals("redirect:/login", view);
    }

    @Test
    void testShowAddForm_LoggedIn() {
        when(authContext.isAuthenticated()).thenReturn(true);
        String view = cashFlowController.showAddForm(model);
        assertEquals("cashflow-form", view);
        verify(model).addAttribute(eq("cashFlow"), any(CashFlow.class));
    }

    // --- 3. TEST EDIT FORM ---
    @Test
    void testShowEditForm_NotLoggedIn() {
        when(authContext.isAuthenticated()).thenReturn(false);
        String view = cashFlowController.showEditForm(UUID.randomUUID(), model);
        assertEquals("redirect:/login", view);
    }

    @Test
    void testShowEditForm_Found() {
        UUID id = UUID.randomUUID();
        CashFlow flow = new CashFlow();
        flow.setId(id);

        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(cashFlowService.getAllCashFlows(any(), any())).thenReturn(Arrays.asList(flow));

        String view = cashFlowController.showEditForm(id, model);
        
        assertEquals("cashflow-form", view);
        verify(model).addAttribute(eq("cashFlow"), eq(flow));
    }

    @Test
    void testShowEditForm_NotFound() {
        UUID id = UUID.randomUUID();
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(cashFlowService.getAllCashFlows(any(), any())).thenReturn(Collections.emptyList());

        String view = cashFlowController.showEditForm(id, model);
        
        assertEquals("redirect:/cashflows", view);
    }

    // --- 4. TEST SAVE (CREATE & UPDATE) ---
    @Test
    void testSave_NotLoggedIn() {
        when(authContext.isAuthenticated()).thenReturn(false);
        String view = cashFlowController.saveCashFlow(new CashFlow());
        assertEquals("redirect:/login", view);
    }

    @Test
    void testSave_Create_New() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);

        CashFlow newFlow = new CashFlow();
        newFlow.setId(null);
        newFlow.setAmount(10000.0); // PENTING: ISI NILAI AMOUNT
        
        String view = cashFlowController.saveCashFlow(newFlow);
        
        assertEquals("redirect:/cashflows", view);
        verify(cashFlowService).createCashFlow(any(), any(), any(), any(), any(), any());
    }

    @Test
    void testSave_Update_Existing() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);

        CashFlow existingFlow = new CashFlow();
        existingFlow.setId(UUID.randomUUID());
        existingFlow.setAmount(20000.0); // PENTING: ISI NILAI AMOUNT

        String view = cashFlowController.saveCashFlow(existingFlow);
        
        assertEquals("redirect:/cashflows", view);
        verify(cashFlowService).updateCashFlow(any(), any(), any(), any(), any(), any(), any());
    }

    // --- 5. TEST DELETE ---
    @Test
    void testDelete_NotLoggedIn() {
        when(authContext.isAuthenticated()).thenReturn(false);
        String view = cashFlowController.deleteCashFlow(UUID.randomUUID());
        assertEquals("redirect:/login", view);
    }

    @Test
    void testDelete_LoggedIn() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);

        String view = cashFlowController.deleteCashFlow(UUID.randomUUID());
        
        assertEquals("redirect:/cashflows", view);
        verify(cashFlowService).deleteCashFlow(any(), any());
    }
} 
// <-- Pastikan kurung kurawal penutup ini ada!