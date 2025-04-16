package cn.nap.download.common.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

public class CommonUtil {
    public static void nonNullElseThrow(Object obj, String errMsg) {
        nonNullElseThrow(obj, new IllegalArgumentException(errMsg));
    }

    public static void nonNullElseThrow(Object obj, RuntimeException exception) {
        if (isEmpty(obj)) {
            throw exception;
        }
    }

    public static <T> T nonNullOrDefault(T t, T def) {
        if (isEmpty(t)) {
            return def;
        }
        return isEmpty(t) ? def : t;
    }

    public static <T> void nonNullOrElse(Object obj, Supplier<T> supplier) {
        if (isEmpty(obj)) {
            supplier.get();
        }
    }

    public static boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        }

        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        } else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        } else if (obj instanceof Iterable) {
            return ((Iterable) obj).iterator().hasNext();
        } else if (obj instanceof Iterator) {
            return ((Iterator) obj).hasNext();
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }

        return false;
    }

    public static String leftPad(String str, int length, char padChar) {
        return leftPad(new StringBuilder(str), length, padChar);
    }

    public static String leftPad(StringBuilder str, int length, char padChar) {
        int strLen = str.length();
        for (int i = strLen; i < length; i++) {
            str.insert(0, padChar);
        }
        return str.toString();
    }
}
