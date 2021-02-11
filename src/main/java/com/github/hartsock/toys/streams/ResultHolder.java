package com.github.hartsock.toys.streams;

import java.util.function.Function;

public class ResultHolder<T,E> {
    final T t;
    final E e;

    private ResultHolder(final T t, final E e) {
        this.t = t;
        this.e = e;
    }

    public T get() {
        return t;
    }

    public static <T, E> ResultHolder<T, E> ofResult(T t) {
        return new ResultHolder<T, E>(t, null);
    }

    public static <T, E> ResultHolder<T, E> ofException(E e) {
        return new ResultHolder(null, e);
    }

    public static <T, E> ResultHolder<T, E> of(T t, E e) {
        return new ResultHolder(t, e);
    }

    public T orElse(final Function<E, T> f) {
        if (t != null) {
            return t;
        }
        return f.apply(e);
    }

    public boolean hasError() {
        return e != null;
    }

    public E getE() {
        return e;
    }
}
