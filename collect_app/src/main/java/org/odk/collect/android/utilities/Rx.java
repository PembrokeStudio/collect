package org.odk.collect.android.utilities;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * @author James Knight
 */

public class Rx {
    public static <T> T id(T t) { return t; }

    public static <L, R> L takeLeft(L l, R r) {
        return l;
    }

    public static <L, R> R takeRight(L l, R r) {
        return r;
    }

    public static <L, R> Object consume(L l, R r) {
        return new Object();
    }

    public static boolean isTrue(boolean b) {
        return b;
    }

    public static boolean isFalse(boolean b) {
        return !b;
    }

    public static boolean not(boolean b) {
        return !b;
    }

    public static boolean and(boolean a, boolean b) {
        return a && b;
    }

    public static boolean or(boolean a, boolean b) {
        return a || b;
    }

    public static boolean xor(boolean a, boolean b) {
        return a ^ b;
    }

    public static boolean nand(boolean a, boolean b) {
        return not(and(a, b));
    }

    public static boolean nor(boolean a, boolean b) {
        return not(or(a, b));
    }

    public static void noop(Object... vars) {}
}
