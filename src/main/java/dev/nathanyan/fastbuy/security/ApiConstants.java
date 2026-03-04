package dev.nathanyan.fastbuy.security;

public final class ApiConstants {

  private ApiConstants() {}

  public static final String API_PREFIX = "/api/v1";

  public static final String AUTH_PREFIX = API_PREFIX + "/auth";
  public static final String PRODUCT_PREFIX = API_PREFIX + "/products";
  public static final String ORDER_PREFIX = API_PREFIX + "/orders";
  public static final String CART_PREFIX = API_PREFIX + "/cart";
  public static final String PAYMENT_PREFIX = API_PREFIX + "/payments";
  public static final String CUSTOMER_PREFIX = API_PREFIX + "/customers";
}
