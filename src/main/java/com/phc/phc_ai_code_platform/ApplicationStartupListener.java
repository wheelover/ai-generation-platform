package com.phc.phc_ai_code_platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("API接口测试文档地址: http://localhost:8123/api/doc.html");
    }
}
