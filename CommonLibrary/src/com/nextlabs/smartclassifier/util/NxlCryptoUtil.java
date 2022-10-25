package com.nextlabs.smartclassifier.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.UUID;

import com.bluejungle.framework.crypt.IDecryptor;
import com.bluejungle.framework.crypt.IEncryptor;
import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.nextlabs.smartclassifier.constant.SCConstant;

public class NxlCryptoUtil {

  public static final IEncryptor encryptor = new ReversibleEncryptor();
  public static final IDecryptor decryptor = new ReversibleEncryptor();

  public static final String encrypt(String originalValue) {
    return encrypt(originalValue, false);
  }

  public static final String encrypt(String originalValue, boolean wrap) {
    if (originalValue == null) return originalValue;

    if (wrap) {
      return SCConstant.ENCRYPTED_PREFIX
          + encryptor.encrypt(originalValue)
          + SCConstant.ENCRYPTED_SUFFIX;
    } else {
      return encryptor.encrypt(originalValue);
    }
  }

  public static final String decrypt(String encryptedValue) {
    if (encryptedValue == null) return encryptedValue;

    if (encryptedValue.startsWith(SCConstant.ENCRYPTED_PREFIX)
        && encryptedValue.endsWith(SCConstant.ENCRYPTED_SUFFIX)) {
      return decryptor.decrypt(
          encryptedValue.substring(
              SCConstant.ENCRYPTED_PREFIX.length(),
              encryptedValue.length() - SCConstant.ENCRYPTED_SUFFIX.length()));
    } else {
      return decryptor.decrypt(encryptedValue);
    }
  }

  public static String hash(String algorithm, String rawPassword) {
    StringBuilder hexString = new StringBuilder();

    try {
      MessageDigest md = MessageDigest.getInstance(algorithm);
      md.update(rawPassword.getBytes());

      byte[] byteData = md.digest();
      for (int i = 0; i < byteData.length; i++) {
        hexString.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
      }
    } catch (NoSuchAlgorithmException err) {
      err.printStackTrace();
    }

    return hexString.toString();
  }

  public static String hash(String algorithm, String rawPassword, byte[] salt) {
    StringBuilder hexString = new StringBuilder();

    try {
      MessageDigest md = MessageDigest.getInstance(algorithm);
      md.update(salt);

      byte[] byteData = md.digest(rawPassword.getBytes());
      for (int i = 0; i < byteData.length; i++) {
        hexString.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
      }
    } catch (NoSuchAlgorithmException err) {
      err.printStackTrace();
    }

    return hexString.toString();
  }

  public static byte[] generateSalt(int size) {
    // Do not cache instance of SecureRandom, let system choose the best algorithm
    SecureRandom secureRandom = new SecureRandom();

    byte[] salt = new byte[size];
    secureRandom.nextBytes(salt);

    return salt;
  }

  public static byte[] generateSalt(String algorithm, int size) throws NoSuchAlgorithmException {
    SecureRandom secureRandom = SecureRandom.getInstance(algorithm);

    byte[] salt = new byte[size];
    secureRandom.nextBytes(salt);

    return salt;
  }

  public static byte[] generateSalt(String algorithm, String provider, int size)
      throws NoSuchAlgorithmException, NoSuchProviderException {
    SecureRandom secureRandom = SecureRandom.getInstance(algorithm, provider);

    byte[] salt = new byte[size];
    secureRandom.nextBytes(salt);

    return salt;
  }

  public static void main(String[] args) {
    //		String originalValue = "qapf1\\Administrator";
    //		String originalValue = "P@ssw0rd";
    String originalValue = UUID.randomUUID().toString();
    String encryptedValue = NxlCryptoUtil.encrypt(originalValue, true);
    System.out.println("Encrypted: " + encryptedValue);
    System.out.println("Decrypted: " + NxlCryptoUtil.decrypt(encryptedValue));
  }
}
