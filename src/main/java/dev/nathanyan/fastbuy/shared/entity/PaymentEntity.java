package dev.nathanyan.fastbuy.shared.entity;

import dev.nathanyan.fastbuy.shared.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "PAYMENT")
public class PaymentEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderEntity order;

  @Column(nullable = false)
  private String stripePaymentIntentId;

  @Column(nullable = false)
  private String stripeChargeId;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP")
  private Instant paidAt;

  @PrePersist
  private void prePersist() {
    this.paidAt = Instant.now();
  }
}
