package dev.nathanyan.fastbuy.customer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.nathanyan.fastbuy.customer.dto.CustomerResponse;
import dev.nathanyan.fastbuy.customer.dto.UpdateCustomerRequest;
import dev.nathanyan.fastbuy.customer.dto.UpdatePasswordRequest;
import dev.nathanyan.fastbuy.shared.dto.address.AddressRequest;
import dev.nathanyan.fastbuy.shared.entity.AddressEntity;
import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import dev.nathanyan.fastbuy.shared.entity.enums.UserRole;
import dev.nathanyan.fastbuy.shared.exception.InvalidPasswordException;
import dev.nathanyan.fastbuy.shared.exception.ResourceNotFoundException;
import dev.nathanyan.fastbuy.shared.exception.UserAlreadyExistsException;
import dev.nathanyan.fastbuy.shared.repository.CustomerRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
  private final String email = "test@email.com";
  private final String addressId = "address-1";
  @InjectMocks private CustomerService customerService;
  @Mock private CustomerRepository customerRepository;
  @Mock private PasswordEncoder passwordEncoder;
  private CustomerEntity customer;
  private AddressEntity address;
  private AddressRequest addressRequest;

  @BeforeEach
  void setUp() {
    address =
        AddressEntity.builder()
            .id(addressId)
            .street("Rua das Flores")
            .number(100)
            .neighborhood("Centro")
            .city("Jundiaí")
            .state("SP")
            .zipCode("13200-000")
            .country("Brasil")
            .isDefault(true)
            .build();

    customer =
        CustomerEntity.builder()
            .id("customer-1")
            .email(email)
            .name("John Doe")
            .phone("(11) 99943-2843")
            .document("465.354.489-80")
            .birthDate(LocalDate.of(1998, 1, 1))
            .role(UserRole.CUSTOMER)
            .addresses(new ArrayList<>(List.of(address)))
            .build();

    addressRequest =
        new AddressRequest(
            "Rua das Flores", 100, null, "Centro", "Jundiaí", "SP", "13200-000", "Brasil", true);
  }

  @Test
  @DisplayName("Should get customer successfully")
  void shouldGetCustomerSuccessfully() {
    when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

    CustomerResponse response = customerService.getCustomer(email);

    assertNotNull(response);
    assertEquals(email, response.email());
    assertEquals("John Doe", response.name());
  }

  @Test
  @DisplayName("Should throw exception when customer not found on getCustomer")
  void shouldThrowExceptionWhenCustomerNotFoundOnGetCustomer() {
    when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> customerService.getCustomer(email));
  }

  @Test
  @DisplayName("Should update customer successfully")
  void shouldUpdateCustomerSuccessfully() {
    UpdateCustomerRequest request =
        new UpdateCustomerRequest("John Doe Doe", "(11) 99943-2843", LocalDate.of(1998, 1, 1));

    when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
    when(customerRepository.save(any())).thenReturn(customer);

    CustomerResponse response = customerService.updateCustomer(email, request);

    assertNotNull(response);
    verify(customerRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Should throw exception when customer not found on updateCustomer")
  void shouldThrowExceptionWhenCustomerNotFoundOnUpdateCustomer() {
    UpdateCustomerRequest request =
        new UpdateCustomerRequest("John Doe Doe", "(11) 99943-2843", LocalDate.of(1998, 1, 1));

    when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class, () -> customerService.updateCustomer(email, request));

    verify(customerRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should update email successfully")
  void shouldUpdateEmailSuccessfully() {
    String newEmail = "newemail@email.com";

    when(customerRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
    when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
    when(customerRepository.save(any())).thenReturn(customer);

    CustomerResponse response = customerService.updateEmail(email, newEmail);

    assertNotNull(response);
    verify(customerRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Should throw exception when email already in use")
  void shouldThrowExceptionWhenEmailAlreadyInUse() {
    String newEmail = "existing@email.com";

    when(customerRepository.findByEmail(newEmail)).thenReturn(Optional.of(customer));

    assertThrows(
        UserAlreadyExistsException.class, () -> customerService.updateEmail(email, newEmail));

    verify(customerRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception when customer not found on updateEmail")
  void shouldThrowExceptionWhenCustomerNotFoundOnUpdateEmail() {
    String newEmail = "newemail@email.com";

    when(customerRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
    when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class, () -> customerService.updateEmail(email, newEmail));
  }

  @Test
  @DisplayName("Should update password successfully")
  void shouldUpdatePasswordSuccessfully() {
    UpdatePasswordRequest request =
        new UpdatePasswordRequest("safetypassword123", "newpassword456");

    when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
    when(passwordEncoder.matches("safetypassword123", customer.getPassword())).thenReturn(true);
    when(passwordEncoder.matches("newpassword456", customer.getPassword())).thenReturn(false);
    when(passwordEncoder.encode("newpassword456")).thenReturn("encodedNewPassword");
    when(customerRepository.save(any())).thenReturn(customer);

    CustomerResponse response = customerService.updatePassword(email, request);

    assertNotNull(response);
    verify(passwordEncoder, times(1)).encode("newpassword456");
    verify(customerRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Should throw exception when current password is wrong")
  void shouldThrowExceptionWhenCurrentPasswordIsWrong() {
    UpdatePasswordRequest request = new UpdatePasswordRequest("wrongpassword", "newpassword456");

    when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
    when(passwordEncoder.matches("wrongpassword", customer.getPassword())).thenReturn(false);

    assertThrows(
        InvalidPasswordException.class, () -> customerService.updatePassword(email, request));

    verify(customerRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception when customer not found on updatePassword")
  void shouldThrowExceptionWhenCustomerNotFoundOnUpdatePassword() {
    UpdatePasswordRequest request =
        new UpdatePasswordRequest("safetypassword123", "newpassword456");

    when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class, () -> customerService.updatePassword(email, request));
  }

  @Test
  @DisplayName("Should add address successfully")
  void shouldAddAddressSuccessfully() {
    customer.setAddresses(new ArrayList<>());

    when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
    when(customerRepository.save(any())).thenReturn(customer);

    customerService.addAddress(email, addressRequest);

    ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);
    verify(customerRepository).save(captor.capture());
    assertEquals(1, captor.getValue().getAddresses().size());
  }

  @Test
  @DisplayName("Should throw exception when customer not found on addAddress")
  void shouldThrowExceptionWhenCustomerNotFoundOnAddAddress() {
    when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class, () -> customerService.addAddress(email, addressRequest));

    verify(customerRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should update address successfully")
  void shouldUpdateAddressSuccessfully() {
    when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
    when(customerRepository.save(any())).thenReturn(customer);

    CustomerResponse response = customerService.updateAddress(email, addressId, addressRequest);

    assertNotNull(response);
    verify(customerRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Should throw exception when address not found on updateAddress")
  void shouldThrowExceptionWhenAddressNotFoundOnUpdateAddress() {
    when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

    assertThrows(
        ResourceNotFoundException.class,
        () -> customerService.updateAddress(email, "invalid-id", addressRequest));

    verify(customerRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception when customer not found on updateAddress")
  void shouldThrowExceptionWhenCustomerNotFoundOnUpdateAddress() {
    when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class,
        () -> customerService.updateAddress(email, addressId, addressRequest));
  }

  @Test
  @DisplayName("Should delete address successfully")
  void shouldDeleteAddressSuccessfully() {
    when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

    customerService.deleteAddress(email, addressId);

    ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);
    verify(customerRepository).save(captor.capture());
    assertTrue(captor.getValue().getAddresses().isEmpty());
  }

  @Test
  @DisplayName("Should throw exception when address not found on deleteAddress")
  void shouldThrowExceptionWhenAddressNotFoundOnDeleteAddress() {
    when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

    assertThrows(
        ResourceNotFoundException.class, () -> customerService.deleteAddress(email, "invalid-id"));

    verify(customerRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception when customer not found on deleteAddress")
  void shouldThrowExceptionWhenCustomerNotFoundOnDeleteAddress() {
    when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class, () -> customerService.deleteAddress(email, addressId));
  }

  @Test
  @DisplayName("Should set default address successfully")
  void shouldSetDefaultAddressSuccessfully() {
    address.setIsDefault(false);

    when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
    when(customerRepository.save(any())).thenReturn(customer);

    customerService.setDefaultAddress(email, addressId);

    assertTrue(address.getIsDefault());
    verify(customerRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Should unset previous default address when setting new default")
  void shouldUnsetPreviousDefaultAddress() {
    AddressEntity previousDefault = AddressEntity.builder().id("address-2").isDefault(true).build();

    address.setIsDefault(false);
    customer.setAddresses(new ArrayList<>(List.of(address, previousDefault)));

    when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
    when(customerRepository.save(any())).thenReturn(customer);

    customerService.setDefaultAddress(email, addressId);

    assertTrue(address.getIsDefault());
    assertFalse(previousDefault.getIsDefault());
  }

  @Test
  @DisplayName("Should throw exception when address not found on setDefaultAddress")
  void shouldThrowExceptionWhenAddressNotFoundOnSetDefaultAddress() {
    when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

    assertThrows(
        ResourceNotFoundException.class,
        () -> customerService.setDefaultAddress(email, "invalid-id"));

    verify(customerRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception when customer not found on setDefaultAddress")
  void shouldThrowExceptionWhenCustomerNotFoundOnSetDefaultAddress() {
    when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class, () -> customerService.setDefaultAddress(email, addressId));
  }
}
