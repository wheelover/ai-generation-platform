package com.phc.phc_ai_code_platform.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.phc.phc_ai_code_platform.ai.model.HtmlCodeResult;
import com.phc.phc_ai_code_platform.ai.model.MultiFileCodeResult;

/**
 * 代码解析器 提供静态方法解析不同类型的代码内容
 *
 * @author yupi
 */
public class CodeParser {

    private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern CSS_CODE_PATTERN = Pattern.compile("```css\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript)\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);

    /**
     * 解析 HTML 单文件代码
     */
    public static HtmlCodeResult parseHtmlCode(String codeContent) {
        HtmlCodeResult result = new HtmlCodeResult();
        // 提取 HTML 代码
        String htmlCode = extractHtmlCode(codeContent);
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlCode(htmlCode.trim());
        } else {
            // 如果没有找到代码块，将整个内容作为HTML
            result.setHtmlCode(codeContent.trim());
        }
        return result;
    }

    /**
     * 解析多文件代码（HTML + CSS + JS）
     */
    public static MultiFileCodeResult parseMultiFileCode(String codeContent) {
        MultiFileCodeResult result = new MultiFileCodeResult();
        // 提取各类代码
        String htmlCode = extractCodeByPattern(codeContent, HTML_CODE_PATTERN);
        String cssCode = extractCodeByPattern(codeContent, CSS_CODE_PATTERN);
        String jsCode = extractCodeByPattern(codeContent, JS_CODE_PATTERN);

        // 设置HTML代码
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlCode(htmlCode.trim());
        } else {
            // 尝试从"html 格式"标记后提取HTML
            htmlCode = extractHtmlFromMarkedContent(codeContent);
            if (htmlCode != null && !htmlCode.trim().isEmpty()) {
                result.setHtmlCode(htmlCode.trim());
            }
        }

        // 设置CSS代码
        if (cssCode != null && !cssCode.trim().isEmpty()) {
            result.setCssCode(cssCode.trim());
        } else {
            // 尝试从"css 格式"标记后提取CSS
            cssCode = extractCssFromMarkedContent(codeContent);
            if (cssCode != null && !cssCode.trim().isEmpty()) {
                result.setCssCode(cssCode.trim());
            }
        }

        // 设置JS代码
        if (jsCode != null && !jsCode.trim().isEmpty()) {
            result.setJsCode(jsCode.trim());
        } else {
            // 尝试从"```js"标记后提取JS
            jsCode = extractJsFromMarkedContent(codeContent);
            if (jsCode != null && !jsCode.trim().isEmpty()) {
                result.setJsCode(jsCode.trim());
            }
        }

        return result;
    }

    /**
     * 从标记内容中提取JS代码 处理"```js"标记后的内容
     */
    private static String extractJsFromMarkedContent(String content) {
        int jsStartIndex = content.indexOf("```js");
        if (jsStartIndex != -1) {
            int start = jsStartIndex + "```js".length();
            int endIndex = content.indexOf("```", start);
            String jsCode;
            if (endIndex != -1) {
                jsCode = content.substring(start, endIndex);
            } else {
                jsCode = content.substring(start);
            }
            // 移除"文件创建完成！"
            if (jsCode.contains("文件创建完成！")) {
                jsCode = jsCode.substring(0, jsCode.indexOf("文件创建完成！"));
            }
            return jsCode;
        }
        return null;
    }

    /**
     * 从标记内容中提取HTML代码 处理"html 格式"标记后的内容
     */
    private static String extractHtmlFromMarkedContent(String content) {
        int htmlStartIndex = content.indexOf("html 格式");
        if (htmlStartIndex != -1) {
            int start = htmlStartIndex + "html 格式".length();
            int cssStartIndex = content.indexOf("css 格式", start);
            if (cssStartIndex != -1) {
                return content.substring(start, cssStartIndex);
            } else {
                return content.substring(start);
            }
        }
        return null;
    }

    /**
     * 从标记内容中提取CSS代码 处理"css 格式"标记后的内容
     */
    private static String extractCssFromMarkedContent(String content) {
        int cssStartIndex = content.indexOf("css 格式");
        if (cssStartIndex != -1) {
            int start = cssStartIndex + "css 格式".length();
            int jsStartIndex = content.indexOf("```js", start);
            String cssCode;
            if (jsStartIndex != -1) {
                cssCode = content.substring(start, jsStartIndex);
            } else {
                cssCode = content.substring(start);
            }
            // 移除尾部的```
            cssCode = cssCode.replaceAll("\\s*```\\s*$", "");
            return cssCode;
        }
        return null;
    }

    /**
     * 提取HTML代码内容
     *
     * @param content 原始内容
     * @return HTML代码
     */
    private static String extractHtmlCode(String content) {
        Matcher matcher = HTML_CODE_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 根据正则模式提取代码
     *
     * @param content 原始内容
     * @param pattern 正则模式
     * @return 提取的代码
     */
    private static String extractCodeByPattern(String content, Pattern pattern) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
