package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

@Slf4j
public class ByteUtils {
	private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

	public static String byteToHexPair(byte b) {
		String hexPair = "";
		hexPair += hexCode[(b >> 4) & 0xF];
		hexPair += hexCode[(b & 0xF)];
		return hexPair;
	}


	public static String byteArrayToHexString(byte[] data) {
		StringBuilder s = new StringBuilder(data.length * 2);
		for (byte b : data) {
			s.append(hexCode[(b >> 4) & 0xF]);
			s.append(hexCode[(b & 0xF)]);
		}
		return s.toString();
	}

	public static byte[] xor(byte[] byteArray1, byte[] byteArray2){
		if(byteArray1.length != byteArray2.length) {
			log.error("Byte arrays of unequal length.");
			return null;
		}
		byte[] xoredArray = new byte[byteArray1.length];
		for(int i=0 ;i < byteArray1.length; i++){
			xoredArray[i] = (byte) (byteArray1[i] ^ byteArray2[i]); 
		}
		return xoredArray;
	}

	public static byte[] getRandomBytes(int len){
		byte[] randomBytes = new byte[len];
		new SecureRandom().nextBytes(randomBytes);
		return randomBytes;
	}
	
	public static StringBuilder byteToBinaryString(byte b) {
		return StringUtils.padLeft(Integer.toBinaryString(b & 0xFF), '0', 8);
	}

}
