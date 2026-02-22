package dev.nathanyan.fastbuy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "carts")
public class CartEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CartItemEntity> items = new ArrayList<>();

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
    CartEntity that = (CartEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "CartEntity{" +
        "id='" + id + '\'' +
        '}';
  }
}
