/**
 * 
 */
package com.example.rest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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
	
	private static final long NUMBER_OF_REQUESTS=10;
	
	/**
	 * Adds the interceptors.
	 *
	 * @param registry the registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		Bandwidth limit = Bandwidth.simple(NUMBER_OF_REQUESTS, Duration.ofMinutes(1));
		Bucket bucket = Bucket4j.builder().addLimit(limit).build();
		registry.addInterceptor(new RateLimitInterceptor(bucket, 1)).addPathPatterns("/employees/bucket4j");
		registry.addInterceptor(new RateLimitInterceptor(bucket, 1)).addPathPatterns("/employees/bucket4j_v2");
	}

}
