package com.phc.phc_ai_code_platform.model.dto.chathistory;

import com.phc.phc_ai_code_platform.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 聊天历史查询请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatHistoryQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 消息类型：user/ai/error
     */
    private String messageType;

    /**
     * 用户id
     */
    private Long userId;
}