package org.delcom.app.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "cashflows")
public class CashFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId; 

    private String type;        
    private String source;      
    private String label;       
    private Double amount;      
    private String description; 

    // --- 1. CONSTRUCTOR KOSONG (Wajib untuk Database) ---
    public CashFlow() {
    }

    // --- 2. CONSTRUCTOR CUSTOM (Untuk bikin data baru di Service) ---
    public CashFlow(UUID userId, String type, String source, String label, Double amount, String description) {
        this.userId = userId;
        this.type = type;
        this.source = source;
        this.label = label;
        this.amount = amount;
        this.description = description;
    }

    // --- 3. GETTER DAN SETTER MANUAL (Pengganti @Data) ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}