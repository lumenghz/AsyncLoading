package com.winchance.util;

import java.security.MessageDigest;
import java.util.regex.Pattern;

public class StringUtil {
    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final ThreadLocal<Pattern> mobilePatternThreadLocal = new ThreadLocal<Pattern>() {
        @Override
        protected Pattern initialValue() {
            return Pattern.compile("^1\\d{10}$");
        }
    };

    public static String getMD5(String source) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(source.getBytes());
            byte tmp[] = messageDigest.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toUpperCase();
        } catch (Exception ignore) {
        }
        return null;
    }

    public static boolean isEmpty(String value) {
        int strLen;
        if ((null == value) || (0 == (strLen = value.length())))
            return true;

        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(value.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    public static String byte2hex(byte[] buffer) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buffer) {
            String stmp = Integer.toHexString(b & 0xFF);
            if (stmp.length() == 1)
                sb.append("0").append(stmp);
            else
                sb.append(stmp);
        }

        return sb.toString().toUpperCase();
    }

    public static byte[] hex2byte(String hex) {
        if (0 != hex.length() % 2)
            throw new IllegalArgumentException();

        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];

        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap    = "" + arr[i++] + arr[i];
            int    byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }

        return b;
    }

    public static boolean isValidMobile(String mobile) {
        if (isEmpty(mobile))
            return false;

        if (mobile.equals("13800138000") || mobile.equals("13000000000"))
            return false;

        return mobilePatternThreadLocal.get().matcher(mobile).matches();
    }
}
