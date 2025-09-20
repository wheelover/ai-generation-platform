package com.phc.phc_ai_code_platform.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天历史视图对象
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 */
@Data
public class ChatHistoryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 消息
     */
    private String message;

    /**
     * 消息类型：user/ai/error
     */
    private String messageType;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 创建用户
     */
    private UserVO user;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}