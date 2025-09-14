package com.phc.phc_ai_code_platform.core.save;

import cn.hutool.core.util.StrUtil;
import com.phc.phc_ai_code_platform.ai.model.CodeGenTypeEnum;
import com.phc.phc_ai_code_platform.ai.model.HtmlCodeResult;
import com.phc.phc_ai_code_platform.exception.BusinessException;
import com.phc.phc_ai_code_platform.exception.ErrorCode;

/**
 * HTML代码文件保存器
 *
 * @author yupi
 */
public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        // 保存 HTML 文件
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        // HTML 代码不能为空
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码内容不能为空");
        }
    }
}

