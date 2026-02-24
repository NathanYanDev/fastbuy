package dev.nathanyan.fastbuy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "address")
@Table(name = "addresses")
public class AddressEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

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

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    AddressEntity that = (AddressEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "AddressEntity{" +
        "id='" + id + '\'' +
        ", street='" + street + '\'' +
        ", complement='" + complement + '\'' +
        ", number=" + number +
        ", city='" + city + '\'' +
        ", state='" + state + '\'' +
        ", zipCode='" + zipCode + '\'' +
        ", country='" + country + '\'' +
        ", isDefault=" + isDefault +
        '}';
  }
}
