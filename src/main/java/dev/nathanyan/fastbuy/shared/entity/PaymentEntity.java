package dev.nathanyan.fastbuy.shared.entity;

import dev.nathanyan.fastbuy.shared.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity(name = "payment")
@Table(name = "payments")
public class PaymentEntity implements Serializable {
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
}
