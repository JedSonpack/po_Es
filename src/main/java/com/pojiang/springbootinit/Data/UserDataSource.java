package com.pojiang.springbootinit.Data;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pojiang.springbootinit.model.entity.User;
import com.pojiang.springbootinit.model.vo.UserVO;
import com.pojiang.springbootinit.service.UserService;
import com.pojiang.springbootinit.model.dto.user.UserQueryRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserDataSource implements DataSource<UserVO> {
    @Resource
    UserService userService;

    @Override
    public Page<UserVO> doSearch(String searchText, long pageIndex, long pageSize) {
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        userQueryRequest.setCurrent((int) pageSize);
        userQueryRequest.setPageSize((int) pageSize);

        Page<User> userPage = userService.page(new Page<>(pageIndex, pageSize), userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(pageIndex, pageSize, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return userVOPage;
    }
    // 存储用户名的所有id
}
