package com.phc.phc_ai_code_platform;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.phc.phc_ai_code_platform.mapper")
@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class}, scanBasePackages = {"com.phc.phc_ai_code_platform", "langgraph4j"})
public class PhcAiCodePlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhcAiCodePlatformApplication.class, args);
		System.out.println();
	}

}