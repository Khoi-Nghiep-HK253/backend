package com.hcmut.divvy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables Spring Data JPA Auditing.
 * Required for {@code @CreatedDate} and {@code @LastModifiedDate}
 * annotations in {@link com.hcmut.divvy.common.audit.BaseEntity} to work.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // No additional beans needed — @EnableJpaAuditing does the work.
}
