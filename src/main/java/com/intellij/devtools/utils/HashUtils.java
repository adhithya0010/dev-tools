package com.intellij.devtools.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HashUtils {

  public static String generateHash(String algo, String input) {
    try {
      MessageDigest msgDst = MessageDigest.getInstance(algo);
      byte[] msgArr = msgDst.digest(input.getBytes(StandardCharsets.UTF_8));
      return bytesToHex(msgArr);
    } catch (NoSuchAlgorithmException e) {
      return "Algorithm not found";
    }
  }

  private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder();
    for (int i = 0; i < hash.length; i++) {
      // Undoing sign extension and converting to hexadecimal
      String hex = Integer.toHexString(0xFF & hash[i]);
      // Why do we need to pad with a zero here?
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
