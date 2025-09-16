package com.phc.phc_ai_code_platform.constant;

/**
 * App常量
 *
 * @author CodeBuddy
 */
public interface AppConstant {

    /**
     * 默认每页最大数量
     */
    int DEFAULT_MAX_PAGE_SIZE = 20;

    /**
     * 管理员可查看的每页最大数量
     */
    int ADMIN_MAX_PAGE_SIZE = 100;

    /**
     * 精选应用的优先级
     */
    Integer GOOD_APP_PRIORITY = 99;

    /**
     * 默认应用优先级
     */
    Integer DEFAULT_APP_PRIORITY = 0;

    /**
     * 应用生成目录
     */
    String CODE_OUTPUT_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 应用部署目录
     */
    String CODE_DEPLOY_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_deploy";

    /**
     * 应用部署域名
     */
    String CODE_DEPLOY_HOST = "http://localhost";

}