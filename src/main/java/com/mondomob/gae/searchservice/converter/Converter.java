package com.mondomob.gae.searchservice.converter;

@FunctionalInterface
interface Converter<F, T> {
    T convert(F from);
}
