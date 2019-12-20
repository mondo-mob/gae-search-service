package com.threewks.gae.searchservice.converter;

import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class InstantToDoubleConverterTest {

    private InstantToDoubleConverter converter;

    @Before
    public void before() {
        converter = new InstantToDoubleConverter();
    }

    @Test
    public void convert_willConvertToDouble() {
        assertThat(converter.convert(Instant.parse("2007-12-03T10:15:30.123Z")), is(1196676.930123));
        assertThat(converter.convert(Instant.parse("+070021-01-17T19:16:39.999Z")), is(2147483646.999999));
        assertThat(converter.convert(Instant.parse("-066082-12-15T04:43:20.001Z")), is(-2147483646.999999));
    }
}
