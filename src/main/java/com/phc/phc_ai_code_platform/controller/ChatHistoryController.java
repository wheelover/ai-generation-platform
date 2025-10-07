package com.phc.phc_ai_code_platform.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.phc.phc_ai_code_platform.annotation.AuthCheck;
import com.phc.phc_ai_code_platform.common.BaseResponse;
import com.phc.phc_ai_code_platform.common.DeleteRequest;
import com.phc.phc_ai_code_platform.common.ResultUtils;
import com.phc.phc_ai_code_platform.constant.UserConstant;
import com.phc.phc_ai_code_platform.exception.BusinessException;
import com.phc.phc_ai_code_platform.exception.ErrorCode;
import com.phc.phc_ai_code_platform.exception.ThrowUtils;
import com.phc.phc_ai_code_platform.model.dto.chathistory.ChatHistoryQueryRequest;
import com.phc.phc_ai_code_platform.model.entity.ChatHistory;
import com.phc.phc_ai_code_platform.model.entity.User;
import com.phc.phc_ai_code_platform.model.vo.ChatHistoryVO;
import com.phc.phc_ai_code_platform.service.ChatHistoryService;
import com.phc.phc_ai_code_platform.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 *  控制层。
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 */
@RestController
@RequestMapping("/chatHistory")
public class ChatHistoryController {

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Resource
    private UserService userService;

    /**
     * 获取应用的最新聊天历史
     *
     * @param appId 应用ID
     * @param limit 限制条数
     * @param request HTTP请求
     * @return 聊天历史列表
     */
    @GetMapping("/latest")
    public BaseResponse<List<ChatHistoryVO>> getLatestChatHistory(@RequestParam Long appId, 
                                                                 @RequestParam(required = false) Integer limit,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 检查用户是否有权限访问该应用的聊天历史
        boolean hasPermission = chatHistoryService.hasAccessPermission(appId, loginUser);
        ThrowUtils.throwIf(!hasPermission, ErrorCode.NO_AUTH_ERROR, "无权访问该应用的聊天历史");
        
        // 获取最新聊天历史
        List<ChatHistoryVO> chatHistoryVOList = chatHistoryService.getLatestChatHistory(appId, limit);
        return ResultUtils.success(chatHistoryVOList);
    }

    /**
     * 根据游标获取更多聊天历史
     *
     * @param appId 应用ID
     * @param cursor 游标（上一页最早消息的ID）
     * @param pageSize 每页大小
     * @param request HTTP请求
     * @return 聊天历史列表
     */
    @GetMapping("/more")
    public BaseResponse<List<ChatHistoryVO>> getMoreChatHistory(@RequestParam Long appId,
                                                               @RequestParam(required = false) Long cursor,
                                                               @RequestParam(required = false) Integer pageSize,
                                                               HttpServletRequest request) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 检查用户是否有权限访问该应用的聊天历史
        boolean hasPermission = chatHistoryService.hasAccessPermission(appId, loginUser);
        ThrowUtils.throwIf(!hasPermission, ErrorCode.NO_AUTH_ERROR, "无权访问该应用的聊天历史");
        
        // 根据游标获取更多聊天历史
        List<ChatHistoryVO> chatHistoryVOList = chatHistoryService.getChatHistoryByCursor(appId, cursor, pageSize);
        return ResultUtils.success(chatHistoryVOList);
    }

    /**
     * 删除应用的所有聊天历史
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/delete/app")
    public BaseResponse<Boolean> deleteAppChatHistory(@RequestBody DeleteRequest deleteRequest, 
                                                     HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        Long appId = deleteRequest.getId();
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 检查用户是否有权限访问该应用的聊天历史
        boolean hasPermission = chatHistoryService.hasAccessPermission(appId, loginUser);
        ThrowUtils.throwIf(!hasPermission, ErrorCode.NO_AUTH_ERROR, "无权删除该应用的聊天历史");
        
        // 删除应用的所有聊天历史
        boolean result = chatHistoryService.deleteByAppId(appId);
        return ResultUtils.success(result);
    }

    /**
     * 管理员分页获取所有聊天历史
     *
     * @param chatHistoryQueryRequest 查询请求
     * @return 分页结果
     */
    @PostMapping("/admin/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<ChatHistoryVO>> listChatHistoryByPage(@RequestBody ChatHistoryQueryRequest chatHistoryQueryRequest) {
        ThrowUtils.throwIf(chatHistoryQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        Page<ChatHistoryVO> chatHistoryVOPage = chatHistoryService.listChatHistoryVOByPage(chatHistoryQueryRequest);
        return ResultUtils.success(chatHistoryVOPage);
    }

    /**
     * 根据主键删除聊天历史（管理员）
     *
     * @param deleteRequest 删除请求
     * @return 是否成功
     */
    @PostMapping("/admin/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteChatHistory(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        Long id = deleteRequest.getId();
        boolean result = chatHistoryService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 分页查询某个应用的对话历史（游标查询）
     *
     * @param appId          应用ID
     * @param pageSize       页面大小
     * @param lastCreateTime 最后一条记录的创建时间
     * @param request        请求
     * @return 对话历史分页
     */
    @GetMapping("/app/{appId}")
    public BaseResponse<Page<ChatHistory>> listAppChatHistory(@PathVariable Long appId,
                                                              @RequestParam(defaultValue = "10") int pageSize,
                                                              @RequestParam(required = false) LocalDateTime lastCreateTime,
                                                              HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Page<ChatHistory> result = chatHistoryService.listAppChatHistoryByPage(appId, pageSize, lastCreateTime, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 管理员分页查询所有对话历史
     *
     * @param chatHistoryQueryRequest 查询请求
     * @return 对话历史分页
     */
    @PostMapping("/admin/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<ChatHistory>> listAllChatHistoryByPageForAdmin(@RequestBody ChatHistoryQueryRequest chatHistoryQueryRequest) {
        ThrowUtils.throwIf(chatHistoryQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = chatHistoryQueryRequest.getPageNum();
        long pageSize = chatHistoryQueryRequest.getPageSize();
        // 查询数据
        QueryWrapper queryWrapper = chatHistoryService.getQueryWrapper(chatHistoryQueryRequest);
        Page<ChatHistory> result = chatHistoryService.page(Page.of(pageNum, pageSize), queryWrapper);
        return ResultUtils.success(result);
    }



}
