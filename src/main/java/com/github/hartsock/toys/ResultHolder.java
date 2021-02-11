package com.github.hartsock.toys;

import java.util.function.Function;

/**
 * A generic result holder that can handle trapping Exceptions for use in streams.
 * <p/>
 * For example, if you were writing a retry-loop or a batch operation, you might want to track
 * a data structure of these. Each result or error can be examined by control logic after the
 * individual execution succeeds or fails.
 * <p/>
 * @param <T> the result to hold
 * @param <E> the exception raised
 */
public class ResultHolder<T,E> {
    final T t;
    final E e;

    private ResultHolder(final T t, final E e) {
        this.t = t;
        this.e = e;
    }

    /**
     * Get the result or null.
     * @return may return null.
     */
    public T get() {
        return t;
    }

    /**
     * Build a ResultHolder without any exceptions.
     *
     * @param t the result to hold
     * @param <T> the type of the result
     * @param <E> the type of the exception (optional in this context)
     * @return an instance of ResultHolder
     */
    public static <T, E> ResultHolder<T, E> ofResult(T t) {
        return new ResultHolder<T, E>(t, null);
    }

    /**
     * Build a ResultHolder with out any result and only an exception.
     *
     * @param e the exception to hold
     * @param <T> the type of the missing result
     * @param <E> the type of the exception
     * @return an instance of ResultHolder
     */
    public static <T, E> ResultHolder<T, E> ofException(E e) {
        return new ResultHolder(null, e);
    }

    /**
     * Build a ResultHolder with BOTH a result and an exception. This
     * might include partial results or results that had problems.
     *
     * @param t the result
     * @param e the exception
     * @param <T> the type of the result
     * @param <E> the type of the exception
     * @return an instance of ResultHolder
     */
    public static <T, E> ResultHolder<T, E> of(T t, E e) {
        return new ResultHolder(t, e);
    }

    /**
     * If there is a result, we return the value held. If there isn't
     * one, the function supplied should convert the exception into an
     * appropriate value of some kind.
     *
     * @param f a function that takes the exception and returns an appropriate result value
     * @return an instance of the result type appropriate for this kind of error
     */
    public T orElse(final Function<E, T> f) {
        if (t != null) {
            return t;
        }
        return f.apply(e);
    }

    /**
     * Returns true if there was an error. There may or may not be a result.
     * @return true if there is a held exception
     */
    public boolean hasError() {
        return e != null;
    }

    /**
     * The exception held.
     * @return the exception held
     */
    public E getE() {
        return e;
    }
}
