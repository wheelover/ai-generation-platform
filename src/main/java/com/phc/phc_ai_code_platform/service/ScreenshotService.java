package com.phc.phc_ai_code_platform.service;

import com.mybatisflex.core.service.IService;
import org.openqa.selenium.devtools.v135.page.model.Screenshot;

public interface ScreenshotService {

    String generateAndUploadScreenshot(String webUrl);


}
