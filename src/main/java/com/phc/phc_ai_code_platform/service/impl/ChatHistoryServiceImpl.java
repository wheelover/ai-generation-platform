package com.phc.phc_ai_code_platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.phc.phc_ai_code_platform.exception.BusinessException;
import com.phc.phc_ai_code_platform.exception.ErrorCode;
import com.phc.phc_ai_code_platform.exception.ThrowUtils;
import com.phc.phc_ai_code_platform.mapper.ChatHistoryMapper;
import com.phc.phc_ai_code_platform.model.dto.chathistory.ChatHistoryQueryRequest;
import com.phc.phc_ai_code_platform.model.entity.App;
import com.phc.phc_ai_code_platform.model.entity.ChatHistory;
import com.phc.phc_ai_code_platform.model.entity.User;
import com.phc.phc_ai_code_platform.model.enums.MessageTypeEnum;
import com.phc.phc_ai_code_platform.model.enums.UserRoleEnum;
import com.phc.phc_ai_code_platform.model.vo.ChatHistoryVO;
import com.phc.phc_ai_code_platform.model.vo.UserVO;
import com.phc.phc_ai_code_platform.service.AppService;
import com.phc.phc_ai_code_platform.service.ChatHistoryService;
import com.phc.phc_ai_code_platform.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *  服务层实现。
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    @Resource
    private UserService userService;

    @Resource
    private AppService appService;

    @Override
    public Long saveUserMessage(Long appId, Long userId, String message) {
        return saveMessage(appId, userId, message, MessageTypeEnum.USER.getValue());
    }

    @Override
    public Long saveAiMessage(Long appId, Long userId, String message) {
        return saveMessage(appId, userId, message, MessageTypeEnum.AI.getValue());
    }

    @Override
    public Long saveErrorMessage(Long appId, Long userId, String errorMessage) {
        return saveMessage(appId, userId, errorMessage, MessageTypeEnum.ERROR.getValue());
    }

    /**
     * 保存消息通用方法
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @param message 消息内容
     * @param messageType 消息类型
     * @return 保存的消息ID
     */
    private Long saveMessage(Long appId, Long userId, String message, String messageType) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        ThrowUtils.throwIf(message == null, ErrorCode.PARAMS_ERROR, "消息内容不能为空");

        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .userId(userId)
                .message(message)
                .messageType(messageType)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDelete(0)
                .build();

        boolean result = this.save(chatHistory);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "保存消息失败");
        return chatHistory.getId();
    }

    @Override
    public List<ChatHistoryVO> getLatestChatHistory(Long appId, Integer limit) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        
        // 默认获取最新的10条消息
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        // 按创建时间降序查询最新的消息
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("chat_history.appId = ?", appId)
                .and("chat_history.isDelete = ?", 0)
                .orderBy("chat_history.createTime DESC")
                .limit(limit);

        List<ChatHistory> chatHistoryList = this.list(queryWrapper);
        
        // 按时间升序排序，使最早的消息在前面
        chatHistoryList.sort((a, b) -> a.getCreateTime().compareTo(b.getCreateTime()));
        
        return getChatHistoryVOList(chatHistoryList);
    }

    @Override
    public List<ChatHistoryVO> getChatHistoryByCursor(Long appId, Long cursor, Integer pageSize) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        
        // 默认每页10条
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }

        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("chat_history.appId = ?", appId)
                .and("chat_history.isDelete = ?", 0);
        
        // 如果有游标，则查询比游标ID小的消息（即更早的消息）
        if (cursor != null && cursor > 0) {
            queryWrapper.and("chat_history.id < ?", cursor);
        }
        
        queryWrapper.orderBy("chat_history.createTime DESC")
                .limit(pageSize);

        List<ChatHistory> chatHistoryList = this.list(queryWrapper);
        
        // 按时间升序排序，使最早的消息在前面
        chatHistoryList.sort((a, b) -> a.getCreateTime().compareTo(b.getCreateTime()));
        
        return getChatHistoryVOList(chatHistoryList);
    }

    @Override
    public boolean deleteByAppId(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("chat_history.appId = ?", appId);
        
        return this.remove(queryWrapper);
    }

    @Override
    public ChatHistoryVO getChatHistoryVO(ChatHistory chatHistory) {
        if (chatHistory == null) {
            return null;
        }
        
        ChatHistoryVO chatHistoryVO = new ChatHistoryVO();
        BeanUtil.copyProperties(chatHistory, chatHistoryVO);
        
        // 关联查询用户信息
        Long userId = chatHistory.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            chatHistoryVO.setUser(userVO);
        }
        
        return chatHistoryVO;
    }

    @Override
    public List<ChatHistoryVO> getChatHistoryVOList(List<ChatHistory> chatHistoryList) {
        if (CollUtil.isEmpty(chatHistoryList)) {
            return new ArrayList<>();
        }
        
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = chatHistoryList.stream()
                .map(ChatHistory::getUserId)
                .collect(Collectors.toSet());
        
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        
        return chatHistoryList.stream().map(chatHistory -> {
            ChatHistoryVO chatHistoryVO = new ChatHistoryVO();
            BeanUtil.copyProperties(chatHistory, chatHistoryVO);
            
            // 设置用户信息
            UserVO userVO = userVOMap.get(chatHistory.getUserId());
            chatHistoryVO.setUser(userVO);
            
            return chatHistoryVO;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<ChatHistoryVO> listChatHistoryVOByPage(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        ThrowUtils.throwIf(chatHistoryQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        
        Long appId = chatHistoryQueryRequest.getAppId();
        String messageType = chatHistoryQueryRequest.getMessageType();
        Long userId = chatHistoryQueryRequest.getUserId();
        String sortField = chatHistoryQueryRequest.getSortField();
        String sortOrder = chatHistoryQueryRequest.getSortOrder();
        long pageSize = chatHistoryQueryRequest.getPageSize();
        long pageNum = chatHistoryQueryRequest.getPageNum();
        
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.where("chat_history.isDelete = ?", 0);
        if (appId != null) {
            queryWrapper.and("chat_history.appId = ?", appId);
        }
        if (messageType != null) {
            queryWrapper.and("chat_history.messageType = ?", messageType);
        }
        if (userId != null) {
            queryWrapper.and("chat_history.userId = ?", userId);
        }
        
        // 默认按创建时间降序排序
        if (sortField == null) {
            sortField = "chat_history.createTime";
        } else {
            sortField = "chat_history." + sortField;
        }
        
        queryWrapper.orderBy(sortField + " " + ("ascend".equals(sortOrder) ? "ASC" : "DESC"));
        
        Page<ChatHistory> chatHistoryPage = this.page(Page.of(pageNum, pageSize), queryWrapper);
        
        // 转换为VO
        Page<ChatHistoryVO> chatHistoryVOPage = new Page<>(pageNum, pageSize, chatHistoryPage.getTotalRow());
        List<ChatHistoryVO> chatHistoryVOList = getChatHistoryVOList(chatHistoryPage.getRecords());
        chatHistoryVOPage.setRecords(chatHistoryVOList);
        
        return chatHistoryVOPage;
    }

    @Override
    public boolean hasAccessPermission(Long appId, User loginUser) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        
        // 管理员有权限访问所有应用的聊天历史
        if (UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole())) {
            return true;
        }
        
        // 查询应用信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        
        // 只有应用创建者可以访问应用的聊天历史
        return app.getUserId().equals(loginUser.getId());
    }
}
