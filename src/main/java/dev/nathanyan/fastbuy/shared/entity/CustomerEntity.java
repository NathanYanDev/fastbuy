package dev.nathanyan.fastbuy.shared.entity;

import dev.nathanyan.fastbuy.shared.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "CUSTOMER")
public class CustomerEntity implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String document;

  @Column(nullable = false)
  private String phone;

  @Column(nullable = false)
  private LocalDate birthDate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserRole role;

  @OneToMany(mappedBy = "customer", fetch =  FetchType.LAZY, cascade = CascadeType.PERSIST)
  private List<OrderEntity> orders = new ArrayList<>();

  @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AddressEntity> addresses = new ArrayList<>();

  @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RefreshTokenEntity> tokens = new ArrayList<>();

  @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
  private CartEntity cart;

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
  @NullMarked
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if(this.role == UserRole.ADMIN){
      return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
    }
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  @NullMarked
  public String getUsername() {
    return getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return UserDetails.super.isEnabled();
  }
}
