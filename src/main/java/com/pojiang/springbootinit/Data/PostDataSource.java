package com.pojiang.springbootinit.Data;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pojiang.springbootinit.model.dto.post.PostQueryRequest;
import com.pojiang.springbootinit.model.entity.Post;
import com.pojiang.springbootinit.model.vo.PostVO;
import com.pojiang.springbootinit.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
public class PostDataSource implements DataSource<PostVO> {
    @Resource
    private PostService postService;

    @Override
    public Page<PostVO> doSearch(String searchText, long pageIndex, long pageSize) {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        postQueryRequest.setCurrent((int) pageIndex);
        postQueryRequest.setPageSize((int) pageSize);
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        // 从ES中找
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);

//        long current = postQueryRequest.getCurrent();
//        long size = postQueryRequest.getPageSize();
//        Page<Post> postPage = postService.page(new Page<>(current, size),
//                postService.getQueryWrapper(postQueryRequest));
        return postService.getPostVOPage(postPage, request);
    }
}