package dev.nathanyan.fastbuy.entity;

import dev.nathanyan.fastbuy.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "payments")
public class PaymentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderEntity order;

  @NotBlank
  @Column(nullable = false)
  private String stripePaymentIntentId;

  @NotBlank
  @Column(nullable = false)
  private String stripeChargeId;

  @Positive
  @Column(nullable = false)
  private BigDecimal amount;

  @NotBlank
  @Column(nullable = false)
  private String currency;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus status;

  @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP")
  private Instant paidAt;

  @PrePersist
  private void prePersist() {
    this.paidAt = Instant.now();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    PaymentEntity that = (PaymentEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "PaymentEntity{" +
        "id='" + id + '\'' +
        ", stripePaymentIntentId='" + stripePaymentIntentId + '\'' +
        ", stripeChargeId='" + stripeChargeId + '\'' +
        ", amount=" + amount +
        ", currency='" + currency + '\'' +
        '}';
  }
}
