package dev.nathanyan.fastbuy.entity;

import dev.nathanyan.fastbuy.entity.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "order")
@Table(name = "orders")
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @OneToMany(mappedBy = "order", fetch =  FetchType.LAZY, cascade = CascadeType.ALL)
  private List<OrderItemEntity> items = new ArrayList<>();

  @Positive
  @Column(nullable = false)
  private BigDecimal totalAmount;

  @NotBlank
  @Column(nullable = false)
  private String stripePaymentIntentId;

  @NotNull
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

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    OrderEntity that = (OrderEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "OrderEntity{" +
        "id='" + id + '\'' +
        ", totalAmount=" + totalAmount +
        ", stripePaymentIntentId='" + stripePaymentIntentId + '\'' +
        ", orderStatus=" + orderStatus +
        '}';
  }
}
