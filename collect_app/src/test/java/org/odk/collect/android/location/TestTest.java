package org.odk.collect.android.location;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

@RunWith(JUnit4.class)
public class TestTest {

    @Test
    public void test() {
        Maybe.just("Maybe 1.")
                .doOnComplete(() -> System.out.println("Maybe complete 1."))
                .flatMapCompletable(this::print)
                .andThen(print("After flat map 1."))
                .subscribe(() -> System.out.println("Subscribe complete 1."));

        Maybe.<String>empty()
                .doOnComplete(() -> System.out.println("Maybe complete 2."))
                .flatMapCompletable(this::print)
                .andThen(print("After flat map 2."))
                .subscribe(() -> System.out.println("Subscribe complete 2."));
    }

    private Completable print(String n) {
        return Completable.defer(() -> {
            System.out.println(n);
            return Completable.complete();
        });
    }
}
