package com.phc.phc_ai_code_platform.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.phc.phc_ai_code_platform.model.dto.chathistory.ChatHistoryQueryRequest;
import com.phc.phc_ai_code_platform.model.entity.ChatHistory;
import com.phc.phc_ai_code_platform.model.entity.User;
import com.phc.phc_ai_code_platform.model.vo.ChatHistoryVO;

import java.util.List;

/**
 *  服务层。
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 保存用户消息
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @param message 消息内容
     * @return 保存的消息ID
     */
    Long saveUserMessage(Long appId, Long userId, String message);

    /**
     * 保存AI消息
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @param message 消息内容
     * @return 保存的消息ID
     */
    Long saveAiMessage(Long appId, Long userId, String message);

    /**
     * 保存错误消息
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @param errorMessage 错误消息内容
     * @return 保存的消息ID
     */
    Long saveErrorMessage(Long appId, Long userId, String errorMessage);

    /**
     * 获取应用的最新聊天历史
     *
     * @param appId 应用ID
     * @param limit 限制条数
     * @return 聊天历史列表
     */
    List<ChatHistoryVO> getLatestChatHistory(Long appId, Integer limit);

    /**
     * 根据游标获取更多聊天历史
     *
     * @param appId 应用ID
     * @param cursor 游标（上一页最早消息的ID）
     * @param pageSize 每页大小
     * @return 聊天历史列表
     */
    List<ChatHistoryVO> getChatHistoryByCursor(Long appId, Long cursor, Integer pageSize);

    /**
     * 根据应用ID删除所有聊天历史
     *
     * @param appId 应用ID
     * @return 是否成功
     */
    boolean deleteByAppId(Long appId);

    /**
     * 获取聊天历史VO
     *
     * @param chatHistory 聊天历史
     * @return 聊天历史VO
     */
    ChatHistoryVO getChatHistoryVO(ChatHistory chatHistory);

    /**
     * 获取聊天历史VO列表
     *
     * @param chatHistoryList 聊天历史列表
     * @return 聊天历史VO列表
     */
    List<ChatHistoryVO> getChatHistoryVOList(List<ChatHistory> chatHistoryList);

    /**
     * 分页获取聊天历史
     *
     * @param chatHistoryQueryRequest 查询请求
     * @return 分页结果
     */
    Page<ChatHistoryVO> listChatHistoryVOByPage(ChatHistoryQueryRequest chatHistoryQueryRequest);

    /**
     * 检查用户是否有权限访问应用的聊天历史
     *
     * @param appId 应用ID
     * @param loginUser 登录用户
     * @return 是否有权限
     */
    boolean hasAccessPermission(Long appId, User loginUser);
}
