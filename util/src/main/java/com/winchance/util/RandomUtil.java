package com.winchance.util;

import java.security.SecureRandom;
import java.util.Random;

@SuppressWarnings("unuse")
public class RandomUtil {
    private static final String letterBase = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
    private static final int letterLength = letterBase.length();

    private static final String numBase = "0123456789";
    private static final int numLength = numBase.length();

    private static final ThreadLocal<Random> randomThreadLocal = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            return new SecureRandom();
        }
    };

    public static String getRandomString(int length) {
        Random random = randomThreadLocal.get();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(letterLength);
            sb.append(letterBase.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomNum(int length) {
        Random random = randomThreadLocal.get();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(numLength);
            sb.append(numBase.charAt(number));
        }
        return sb.toString();
    }

    public static boolean machRandom(int percent) {
        if (percent < 0 || percent > 100)
            return false;

        Random random = randomThreadLocal.get();
        if ((random.nextInt(100) + 1) <= percent)
            return true;
        return false;
    }
}
