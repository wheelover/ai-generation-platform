package com.phc.phc_ai_code_platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.phc.phc_ai_code_platform.mapper")
public class PhcAiCodePlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhcAiCodePlatformApplication.class, args);
		System.out.println();
	}

}



