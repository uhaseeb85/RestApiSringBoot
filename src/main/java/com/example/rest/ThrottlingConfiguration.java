/**
 * 
 */
package com.example.rest;

import java.time.Duration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.throttling.filter.RateLimitInterceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;

/**
 * The Class ThrottlingConfiguration.
 *
 * @author Haseeb
 */
@Configuration
public class ThrottlingConfiguration implements WebMvcConfigurer {
	
	/** The Constant NUMBER_OF_REQUESTS. */
	private static final long NUMBER_OF_REQUESTS_PER_MINUTE=10;
	
	/**
	 * Adds the interceptors.
	 *
	 * @param registry the registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new RateLimitInterceptor(createBucket(NUMBER_OF_REQUESTS_PER_MINUTE), 1)).addPathPatterns("/employees/bucket4j");
		registry.addInterceptor(new RateLimitInterceptor(createBucket(NUMBER_OF_REQUESTS_PER_MINUTE), 1)).addPathPatterns("/employees/bucket4j_v2");
	}

	/**
	 * Creates the bucket.
	 *
	 * @param numberOfRequests the number of requests
	 * @return the bucket
	 */
	private Bucket createBucket(long numberOfRequests) {
		Bandwidth limit = Bandwidth.simple(NUMBER_OF_REQUESTS_PER_MINUTE, Duration.ofMinutes(1));
		Bucket bucket = Bucket4j.builder().addLimit(limit).build();
		return bucket;
	}

}
