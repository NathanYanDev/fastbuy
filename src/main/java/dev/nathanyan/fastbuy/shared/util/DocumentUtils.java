package dev.nathanyan.fastbuy.shared.util;

public class DocumentUtils {
  public static String maskDocument(String document) {
    String sanitized = document.replaceAll("[.\\-]", "");
    return "***." + sanitized.substring(3, 6) + "." + sanitized.substring(6, 9) + "-**";
  }
}
