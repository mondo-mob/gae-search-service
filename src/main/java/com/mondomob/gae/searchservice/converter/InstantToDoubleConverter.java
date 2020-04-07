package com.mondomob.gae.searchservice.converter;

import java.time.Instant;

/**
 * Convert {@link Instant} values to {@link Double} values to use as
 * search index values.
 * <p>
 * Number field for search index is a double precision floating point value
 * between -2,147,483,647 and 2,147,483,647. Storing long value up to second
 * precision would only allow range between 1901 and 2038.
 * This converter takes the epoch seconds and divides it by 1,000,000 to keep
 * the value well within the allowed range and still maintain ability for
 * numeric comparison during search queries (e.g. greater than/less than).
 * e.g.
 * 2040-01-01T12:13:14.123Z = 2209032794123 epoch milliseconds = 2209032.794123 indexed value
 */
public class InstantToDoubleConverter implements Converter<Instant, Double> {
	@Override
	public Double convert(Instant source) {
		return Long.valueOf(source.toEpochMilli()).doubleValue() / 1000000;
	}
}
