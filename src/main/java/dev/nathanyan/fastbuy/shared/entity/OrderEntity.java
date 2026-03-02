package dev.nathanyan.fastbuy.shared.entity;

import dev.nathanyan.fastbuy.shared.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "ORDER")
public class OrderEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private CustomerEntity customer;

  @OneToMany(mappedBy = "order", fetch =  FetchType.LAZY, cascade = CascadeType.ALL)
  private List<OrderItemEntity> items = new ArrayList<>();

  @Column(nullable = false)
  private BigDecimal totalAmount;

  @Column(nullable = false)
  private String stripePaymentIntentId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus orderStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shipping_address_id", nullable = false)
  private AddressEntity shippingAddress;

  @OneToOne(mappedBy = "order")
  private PaymentEntity payment;

  @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private Instant createdAt;

  @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private Instant updatedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = Instant.now();
  }
}
