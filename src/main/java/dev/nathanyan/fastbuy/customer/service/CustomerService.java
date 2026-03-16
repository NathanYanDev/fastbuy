package dev.nathanyan.fastbuy.customer.service;

import dev.nathanyan.fastbuy.customer.dto.CustomerResponse;
import dev.nathanyan.fastbuy.customer.dto.UpdateCustomerRequest;
import dev.nathanyan.fastbuy.customer.dto.UpdatePasswordRequest;
import dev.nathanyan.fastbuy.shared.dto.address.AddressRequest;
import dev.nathanyan.fastbuy.shared.dto.address.AddressResponse;
import dev.nathanyan.fastbuy.shared.entity.AddressEntity;
import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import dev.nathanyan.fastbuy.shared.exception.InvalidPasswordException;
import dev.nathanyan.fastbuy.shared.exception.ResourceNotFoundException;
import dev.nathanyan.fastbuy.shared.exception.UserAlreadyExistsException;
import dev.nathanyan.fastbuy.shared.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;

  public CustomerResponse getCustomer(String email) {
    CustomerEntity customer = findByEmailOrThrow(email);
    return CustomerResponse.from(customer);
  }

  @Transactional
  public CustomerResponse updateCustomer(String email, UpdateCustomerRequest request) {
    CustomerEntity customer = findByEmailOrThrow(email);

    customer.setName(request.name());
    customer.setPhone(request.phone());
    customer.setBirthDate(request.birthDate());

    return CustomerResponse.from(customerRepository.save(customer));
  }

  @Transactional
  public CustomerResponse updatePassword(String email, UpdatePasswordRequest request) {
    CustomerEntity customer = findByEmailOrThrow(email);

    if (!passwordEncoder.matches(request.currentPassword(), customer.getPassword())) {
      throw new InvalidPasswordException("Invalid password");
    }

    customer.setPassword(passwordEncoder.encode(request.newPassword()));
    return CustomerResponse.from(customerRepository.save(customer));
  }

  @Transactional
  public CustomerResponse updateEmail(String email, String newEmail) {
    if (customerRepository.findByEmail(newEmail).isPresent()) {
      throw new UserAlreadyExistsException("Email already in use: " + newEmail);
    }

    CustomerEntity customer = findByEmailOrThrow(email);
    customer.setEmail(newEmail);
    return CustomerResponse.from(customerRepository.save(customer));
  }

  @Transactional
  public AddressResponse addAddress(String email, AddressRequest request) {
    CustomerEntity customer = findByEmailOrThrow(email);

    AddressEntity address = AddressRequest.toEntity(request, customer);
    customer.addAddress(address);
    customerRepository.save(customer);

    return AddressResponse.from(address);
  }

  @Transactional
  public CustomerResponse updateAddress(String email, String addressId, AddressRequest request) {
    CustomerEntity customer = findByEmailOrThrow(email);

    AddressEntity address = findAddressByIdOrThrow(addressId, customer);

    AddressRequest.updateEntity(address, request);

    return CustomerResponse.from(customerRepository.save(customer));
  }

  @Transactional
  public void deleteAddress(String email, String addressId) {
    CustomerEntity customer = findByEmailOrThrow(email);

    AddressEntity address = findAddressByIdOrThrow(addressId, customer);

    customer.removeAddress(address);

    customerRepository.save(customer);
  }

  @Transactional
  public CustomerResponse setDefaultAddress(String email, String addressId) {
    CustomerEntity customer = findByEmailOrThrow(email);

    AddressEntity address = findAddressByIdOrThrow(addressId, customer);
    customer.getAddresses().stream()
        .filter(AddressEntity::getIsDefault)
        .findFirst()
        .ifPresent(pastDefaultAddress -> pastDefaultAddress.setIsDefault(false));

    address.setIsDefault(true);

    return CustomerResponse.from(customerRepository.save(customer));
  }

  private CustomerEntity findByEmailOrThrow(String email) {
    return customerRepository
        .findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  private AddressEntity findAddressByIdOrThrow(String addressId, CustomerEntity customer) {
    return customer.getAddresses().stream()
        .filter(a -> a.getId().equals(addressId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + addressId));
  }
}
