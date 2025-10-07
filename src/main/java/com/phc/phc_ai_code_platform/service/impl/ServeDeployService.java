package com.phc.phc_ai_code_platform.service.impl;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ServeDeployService {

    private static final String CODE_BASE_DIR = "./tmp";
    private static final int SERVE_PORT = 3000;
    private static Process serveProcess;

    /**
     * 启动 Serve 服务
     */
    private static final String NODEJS_PATH = "D:\\Program Files\\nodejs\\"; // 根据实际安装路径调整
    private static final String NPX_CMD = NODEJS_PATH + "npm.cmd"; // npx 是 npm 的一部分，可通过 npm.cmd 调用

    public void startServeService() {
        try {
            if (serveProcess == null || !serveProcess.isAlive()) {
                ProcessBuilder pb = new ProcessBuilder(
                        NPX_CMD, "serve", CODE_BASE_DIR, "-p", String.valueOf(SERVE_PORT)
                );
                pb.redirectErrorStream(true);
                serveProcess = pb.start();
                System.out.println("Serve service started on port " + SERVE_PORT);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to start serve service", e);
        }
    }


    /**
     * 关闭 Serve 服务
     */
    public void stopServeService() {
        if (serveProcess != null && serveProcess.isAlive()) {
            serveProcess.destroy();
            try {
                serveProcess.waitFor(5, TimeUnit.SECONDS);
                System.out.println("Serve service stopped");
            } catch (InterruptedException e) {
                serveProcess.destroyForcibly();
                System.out.println("Serve service force stopped");
            }
        }
    }
}
