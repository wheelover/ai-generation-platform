package com.phc.phc_ai_code_platform.ai.model;

import jdk.jfr.Description;
import lombok.Data;

@Data
@Description("生成的HTML代码文件的结果")
public class HtmlCodeResult {

    @Description("HTML代码")
    private String htmlCode;

    @Description("生成代码描述")
    private String description;
}
