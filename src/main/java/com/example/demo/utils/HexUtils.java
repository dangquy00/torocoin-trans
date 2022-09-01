package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

@Slf4j
public class HexUtils {

	public static int hexToByte(char ch) {
		if ('0' <= ch && ch <= '9') return ch - '0';
		if ('A' <= ch && ch <= 'F') return ch - 'A' + 10;
		if ('a' <= ch && ch <= 'f') return ch - 'a' + 10;
		return -1;
	}

	public static byte hexPairToByte(String hexString) {
		int h = hexToByte(hexString.charAt(0));
		int l = hexToByte(hexString.charAt(1));
		if (h == -1 || l == -1) throw new IllegalArgumentException("contains illegal character for hexBinary: " + hexString);
		byte byteValue = (byte) (h * 16 + l);
		return byteValue;
	}

	public static int hexPairToInt(String hexString) {
		int h = hexToByte(hexString.charAt(0));
		int l = hexToByte(hexString.charAt(1));
		if (h == -1 || l == -1) throw new IllegalArgumentException("contains illegal character for hexBinary: " + hexString);
		int intValue =  (h * 16 + l);
		return intValue;
	}


	public static byte[] hexStringToByteArray(String hexString) {
		if (hexString.length() % 2 != 0) hexString = "0" + hexString;
		byte[] byteArray = new byte[hexString.length() / 2];
		for (int i = 0; i < hexString.length(); i += 2) {
			byteArray[i / 2] = hexPairToByte(hexString.substring(i, i + 2));
		}
		return byteArray;
	}

	public static int[] hexStringToIntArray(String hexString){
		if (hexString.length() % 2 != 0) hexString = "0" + hexString;
		int[] intArray = new int[hexString.length() / 2];
		for (int i = 0; i < hexString.length(); i += 2) {
			intArray[i / 2] = hexPairToInt(hexString.substring(i, i + 2));
		}
		return intArray;
	}

	public static String xor(String hexString1, String hexString2){
		if(hexString1.length() != hexString2.length()){
			log.error("Hex strings of unequal length");
			return null;
		}
		byte[] byteArray1 = hexStringToByteArray(hexString1);
		byte[] byteArray2 = hexStringToByteArray(hexString2);
		String xoredHexString = ByteUtils.byteArrayToHexString(ByteUtils.xor(byteArray1, byteArray2));
		return xoredHexString.substring(xoredHexString.length()-hexString1.length());
	}

	public static String getRandomHexString(int len){
		byte[] randomBytes = new byte[len/2+1];
		new SecureRandom().nextBytes(randomBytes);
		return ByteUtils.byteArrayToHexString(randomBytes).substring(0, len);
	}


	public static void main(String[] args) {
		//System.out.println(getRandomHexString(10));
		System.out.println(hexPairToInt("81"));
		System.out.println(xor("04444", "44567"));
	}
}
