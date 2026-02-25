package dev.nathanyan.fastbuy.shared.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity(name = "address")
@Table(name = "addresses")
public class AddressEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private CustomerEntity customer;

  @NotBlank
  @Column(nullable = false)
  private String street;

  @PositiveOrZero
  @Column(nullable = false)
  private int number;

  @Column()
  private String complement;

  @NotBlank
  @Column(nullable = false)
  private String city;

  @NotBlank
  @Column(nullable = false)
  private String state;

  @NotBlank
  @Column(nullable = false)
  private String zipCode;

  @NotBlank
  @Column(nullable = false)
  private String country;

  @NotNull
  @Column(nullable = false)
  private Boolean isDefault;

  @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private Instant createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
  }
}
