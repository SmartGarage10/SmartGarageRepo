package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "visit_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"visit", "serviceItem", "pack"})
@ToString(exclude = {"visit", "serviceItem", "pack"})
public class VisitItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "visit_id", nullable = false)
    @JsonBackReference
    private Visit visit;

    // Can be either ServiceItem OR Pack (one will be null)
    @ManyToOne
    @JoinColumn(name = "service_id")
    @JsonIgnoreProperties({"visits", "packs", "visitItems"})
    private ServiceItem serviceItem;

    @ManyToOne
    @JoinColumn(name = "pack_id")
    @JsonIgnoreProperties({"services", "visits", "visitItems"})
    private Pack pack;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Actual charged price

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "item_name", length = 100)
    private String itemName; // Historical record

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private ItemType itemType;

    public enum ItemType {
        SERVICE,
        PACK
    }

    // Constructor for service
    public VisitItem(ServiceItem serviceItem, BigDecimal price, Integer quantity) {
        this.serviceItem = serviceItem;
        this.pack = null;
        this.price = price != null ? price : (serviceItem != null ? serviceItem.getPrice() : BigDecimal.ZERO);
        this.quantity = quantity != null ? quantity : 1;
        this.itemName = serviceItem != null ? serviceItem.getServiceName() : null;
        this.itemType = ItemType.SERVICE;
    }

    // Constructor for pack
    public VisitItem(Pack pack, BigDecimal price, Integer quantity) {
        this.pack = pack;
        this.serviceItem = null;
        this.price = price != null ? price : (pack != null && pack.getAmount() != null ? pack.getAmount() : BigDecimal.ZERO);
        this.quantity = quantity != null ? quantity : 1;
        this.itemName = pack != null ? pack.getPackName() : null;
        this.itemType = ItemType.PACK;
    }

    // Subtotal for this item
    @Transient
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // Display name
    @Transient
    public String getDisplayName() {
        if (itemName != null) return itemName;
        if (itemType == ItemType.PACK && pack != null) return pack.getPackName();
        if (itemType == ItemType.SERVICE && serviceItem != null) return serviceItem.getServiceName();
        return "Unknown Item";
    }
}
