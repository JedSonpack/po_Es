package com.pojiang.springbootinit.controller;

import com.pojiang.springbootinit.common.BaseResponse;
import com.pojiang.springbootinit.common.ResultUtils;
import com.pojiang.springbootinit.model.dto.search.SearchQueryRequest;
import com.pojiang.springbootinit.manager.SearchFacade;
import com.pojiang.springbootinit.model.vo.SearchVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

/**
 * 图片接口
 *
 * 
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private SearchFacade searchFacade;

    /**
     * 搜索全部资源
     *
     * @return
     */

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchQueryRequest searchQueryRequest,
                                            HttpServletRequest request) throws ExecutionException {
        SearchVO searchVO = searchFacade.searchAll(searchQueryRequest, request);
        return ResultUtils.success(searchVO);
    }

}
