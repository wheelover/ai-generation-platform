package com.phc.phc_ai_code_platform.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.phc.phc_ai_code_platform.model.dto.app.AppQueryRequest;
import com.phc.phc_ai_code_platform.model.entity.App;
import com.phc.phc_ai_code_platform.model.entity.User;
import com.phc.phc_ai_code_platform.model.vo.AppVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;

import java.io.Serializable;
import java.util.List;

/**
 *  服务层。
 *
 *
 */
public interface AppService extends IService<App> {

    /**
     * 创建应用
     *
     * @param app 应用信息
     * @param request HTTP请求
     * @return 应用ID
     */
    long addApp(App app, HttpServletRequest request);

    /**
     * 修改应用
     *
     * @param app 应用信息
     * @param request HTTP请求
     * @return 是否成功
     */
    boolean updateApp(App app, HttpServletRequest request);

    /**
     * 删除应用
     *
     * @param id 应用ID
     * @param request HTTP请求
     * @return 是否成功
     */
    boolean deleteApp(long id, HttpServletRequest request);

    boolean removeById(Serializable id);

    /**
     * 获取应用VO
     *
     * @param app 应用信息
     * @return 应用VO
     */
    AppVO getAppVO(App app);

    /**
     * 获取应用VO列表
     *
     * @param appList 应用列表
     * @return 应用VO列表
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 获取查询条件
     *
     * @param appQueryRequest 查询请求
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 聊天生成代码
     *
     * @param appId 应用ID
     * @param message 消息
     * @param loginUser 登录用户
     * @return 生成的代码
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    /**
     * 部署应用
     *
     * @param appId 应用ID
     * @param loginUser 登录用户
     * @return 部署结果
     */
    String deployApp(Long appId, User loginUser);
}
