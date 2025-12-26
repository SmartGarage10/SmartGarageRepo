package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "visit_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_item_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "visit_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    private Visit visit;

    // Can be either ServiceItem OR Pack (one will be null)
    @ManyToOne
    @JoinColumn(name = "service_id")
    @JsonIgnoreProperties({"visits", "packs", "visitItems"})
    @ToString.Exclude
    private ServiceItem serviceItem;

    @ManyToOne
    @JoinColumn(name = "pack_id")
    @JsonIgnoreProperties({"services", "visits", "visitItems"})
    @ToString.Exclude
    private Pack pack;

    @Column(name = "price", nullable = false)
    private Double price; // Actual charged price

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "item_name", length = 100)
    private String itemName; // For historical records

    @Column(name = "item_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    public enum ItemType {
        SERVICE,
        PACK
    }

    // Constructor for service
    public VisitItem(ServiceItem serviceItem, Double price, Integer quantity) {
        this.serviceItem = serviceItem;
        this.pack = null;
        this.price = price != null ? price : (serviceItem != null ? serviceItem.getPrice() : 0.0);
        this.quantity = quantity != null ? quantity : 1;
        this.itemName = serviceItem != null ? serviceItem.getServiceName() : null;
        this.itemType = ItemType.SERVICE;
    }

    // Constructor for pack
    public VisitItem(Pack pack, Double price, Integer quantity) {
        this.pack = pack;
        this.serviceItem = null;
        this.price = price != null ? price : (pack != null ? pack.getAmount() : 0.0);
        this.quantity = quantity != null ? quantity : 1;
        this.itemName = pack != null ? pack.getPackName() : null;
        this.itemType = ItemType.PACK;
    }

    // Get subtotal for this item
    @Transient
    public Double getSubtotal() {
        return price * quantity;
    }

    // Get display name
    @Transient
    public String getDisplayName() {
        if (itemName != null) return itemName;
        if (itemType == ItemType.PACK && pack != null) {
            return pack.getPackName();
        } else if (itemType == ItemType.SERVICE && serviceItem != null) {
            return serviceItem.getServiceName();
        }
        return "Unknown Item";
    }
}