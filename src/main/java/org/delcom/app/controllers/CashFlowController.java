package org.delcom.app.controllers;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller // 1. Berubah dari RestController menjadi Controller
@RequestMapping("/cashflows")
public class CashFlowController {

    private final CashFlowService cashFlowService;
    private final AuthContext authContext;

    @Autowired
    public CashFlowController(CashFlowService cashFlowService, AuthContext authContext) {
        this.cashFlowService = cashFlowService;
        this.authContext = authContext;
    }

    // --- MENAMPILKAN TABEL DATA ---
    @GetMapping
    public String listCashFlows(Model model) {
        // Cek Login (Kalau belum login, lempar ke halaman login)
        if (!authContext.isAuthenticated())
            return "redirect:/login";

        User user = authContext.getAuthUser();

        // Ambil data dari database
        List<CashFlow> flows = cashFlowService.getAllCashFlows(user.getId(), null);

        // Masukkan data ke "Model" supaya bisa dibaca file HTML
        model.addAttribute("listCashFlow", flows);

        return "cashflow-list"; // Ini akan membuka file cashflow-list.html
    }

    // --- MENAMPILKAN FORM TAMBAH ---
    @GetMapping("/add")
    public String showAddForm(Model model) {
        if (!authContext.isAuthenticated())
            return "redirect:/login";

        // Kirim object kosong untuk diisi user
        model.addAttribute("cashFlow", new CashFlow());
        return "cashflow-form"; // Ini akan membuka file cashflow-form.html
    }

    // --- MENAMPILKAN FORM EDIT ---
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        if (!authContext.isAuthenticated())
            return "redirect:/login";

        User user = authContext.getAuthUser();

        // Cari data berdasarkan ID (Logic manual filter dari list karena service
        // getById mungkin belum ada)
        List<CashFlow> flows = cashFlowService.getAllCashFlows(user.getId(), null);
        CashFlow flow = flows.stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);

        if (flow != null) {
            model.addAttribute("cashFlow", flow);
            return "cashflow-form"; // Pakai form yang sama dengan tambah
        }
        return "redirect:/cashflows";
    }

    // --- PROSES SIMPAN (TAMBAH / UPDATE) ---
    @PostMapping("/save")
    public String saveCashFlow(@ModelAttribute CashFlow flow) {
        if (!authContext.isAuthenticated())
            return "redirect:/login";

        User user = authContext.getAuthUser();

        if (flow.getId() == null) {
            // Kalau ID kosong, berarti DATA BARU (Create)
            cashFlowService.createCashFlow(
                    user.getId(), flow.getType(), flow.getSource(),
                    flow.getLabel(), flow.getAmount().intValue(), flow.getDescription());
        } else {
            // Kalau ID ada, berarti UPDATE (Edit)
            cashFlowService.updateCashFlow(
                    flow.getId(), user.getId(), flow.getType(), flow.getSource(),
                    flow.getLabel(), flow.getAmount().intValue(), flow.getDescription());
        }
        return "redirect:/cashflows"; // Balik ke halaman list
    }

    // --- PROSES HAPUS ---
    @GetMapping("/delete/{id}")
    public String deleteCashFlow(@PathVariable UUID id) {
        if (!authContext.isAuthenticated())
            return "redirect:/login";

        User user = authContext.getAuthUser();
        cashFlowService.deleteCashFlow(id, user.getId());

        return "redirect:/cashflows";
    }
}