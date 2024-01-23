package com.example.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing //이거 안하면 @CreatedDate, @LastModifiedDate 안먹음
public class JpaAuditingConfiguration {
}
