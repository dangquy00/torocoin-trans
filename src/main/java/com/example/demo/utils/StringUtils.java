package com.example.demo.utils;

public class StringUtils extends org.springframework.util.StringUtils {
    public static boolean isNullOrEmpty(String string) {
        if (string == null || string.trim().length() == 0) return true;
        else return false;
    }


    public static StringBuilder padRight(String data, char padChar, int len) {
        StringBuilder sb = new StringBuilder();
        sb.append(data);
        if (sb.length() < len) while (sb.length() != len) sb.append(padChar);
        return sb;
    }

    public static StringBuilder tabPadRight(String data, int len) {
        StringBuilder sb = new StringBuilder();
        sb.append(data);
        int strLen = sb.length();
        if (strLen < len) while (strLen < len) {
            if(strLen % 4 == 0) sb.append('\t');
            strLen ++;
        }
        return sb;
    }


    public static StringBuilder padLeft(String data, char padChar, int len) {
        StringBuilder sb = new StringBuilder();
        if (data.length() >= len) {
            sb.append(data);
            return sb;
        } else while (sb.length() != (len - data.length())) sb.append(padChar);
        sb.append(data);
        return sb;
    }

    public static String hexToASCII(String hexString) {
        return new String(HexUtils.hexStringToByteArray(hexString));
    }

    public static void main(String[] args) {
        System.out.println(padLeft("ABCD", '0', 20));
        System.out.println(padRight("ABCD", '0', 20).reverse());
    }
}
