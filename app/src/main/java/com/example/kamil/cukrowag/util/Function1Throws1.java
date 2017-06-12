package com.example.kamil.cukrowag.util;

/**
 * Created by kamil on 11.06.17.
 */
public interface Function1Throws1<S, T, E extends Throwable> {
    public S apply(T a) throws E;
}