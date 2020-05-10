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
	
	private static Map<Long,String> API_THROTTING_MAP = new HashMap<Long,String>();
	
	static {
		// < Number of Requests per Minute , URL pattern >
		API_THROTTING_MAP.put((long) 10, "/employees/bucket4j");
	}
	
	/**
	 * Adds the interceptors.
	 *
	 * @param registry the registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		API_THROTTING_MAP.entrySet().stream().forEach(i -> {
			Bandwidth limit = Bandwidth.simple(i.getKey(), Duration.ofMinutes(1));
			Bucket bucket = Bucket4j.builder().addLimit(limit).build();
			registry.addInterceptor(new RateLimitInterceptor(bucket, 1)).addPathPatterns(i.getValue());
		});
		
	}

}
