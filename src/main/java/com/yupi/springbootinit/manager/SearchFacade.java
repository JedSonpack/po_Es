package com.yupi.springbootinit.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.Data.*;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.dto.search.SearchQueryRequest;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.enums.SearchTypeEnum;
import com.yupi.springbootinit.model.vo.PostVO;
import com.yupi.springbootinit.model.vo.SearchVO;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.service.PictureService;
import com.yupi.springbootinit.service.PostService;
import com.yupi.springbootinit.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Currency;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

/**
 * 门面模式，让前端只需要穿参数就行
 */
@Component
public class SearchFacade {
    @Resource
    private UserDataSource userDataSource;
    @Resource
    private PictureDataSource pictureDataSource;
    @Resource
    private PostDataSource postDataSource;
    @Resource
    private DataSourceRegister dataSourceRegister;

    public SearchVO searchAll(@RequestBody SearchQueryRequest searchQueryRequest,
                              HttpServletRequest request) {
        String type = searchQueryRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        // 请求为空直接报错
        String searchText = searchQueryRequest.getSearchText();
        if (searchText == null)
            searchText = "";
        SearchVO searchVO = new SearchVO();
        // 搜索出所有的参数
        int current = searchQueryRequest.getCurrent();
        int pageSize = searchQueryRequest.getPageSize();
        if (searchTypeEnum == null) {
            String finalSearchText = searchText;
            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                // 存在ThreadLocal中
                RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
                try {
                    return pictureDataSource.doSearch(finalSearchText, current, pageSize);
                } finally {
                    // 清除
                    RequestContextHolder.resetRequestAttributes();
                }
            });
            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                try {
                    PostQueryRequest postQueryRequest = new PostQueryRequest();
                    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
                    postQueryRequest.setSearchText(finalSearchText);
                    Page<PostVO> postVOPage = postDataSource.doSearch(finalSearchText, current, pageSize);
                    return postVOPage;
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                }
            });
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                try {
                    UserQueryRequest userQueryRequest = new UserQueryRequest();
                    userQueryRequest.setUserName(finalSearchText);
                    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
                    Page<UserVO> userVOPage = userDataSource.doSearch(finalSearchText, current, pageSize);
                    return userVOPage;
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                }
            });


            CompletableFuture.allOf(pictureTask, postTask, userTask).join();

            try {
                Page<Picture> picturePage = pictureTask.get();
                Page<UserVO> userVOPage = userTask.get();
                Page<PostVO> postVOPage = postTask.get();
                searchVO.setPictureList(picturePage.getRecords());
                searchVO.setUserList(userVOPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常！！！");
            }
        } else { // 查询部分内容
            // 注册器模式：通过提前通过一个Map或者其他类型存储好后面需要调用的对象
            DataSource<?> dataSource = dataSourceRegister.getDataSourceByType(type);
            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataList(page.getRecords());
        }
        return searchVO;
    }
}