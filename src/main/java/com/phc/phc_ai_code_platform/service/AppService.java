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

import java.util.List;

/**
 *  服务层。
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
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


    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

}
