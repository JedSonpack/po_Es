package com.pojiang.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pojiang.springbootinit.common.BaseResponse;
import com.pojiang.springbootinit.common.ErrorCode;
import com.pojiang.springbootinit.common.ResultUtils;
import com.pojiang.springbootinit.model.entity.Picture;
import com.pojiang.springbootinit.service.PictureService;
import com.pojiang.springbootinit.exception.ThrowUtils;
import com.pojiang.springbootinit.model.dto.picture.PictureQueryRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 图片接口
 *
 * 
 */
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {

    @Resource
    private PictureService pictureService;


    /**
     * 分页获取列表（封装类）
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPostVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                        HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        String search_text = pictureQueryRequest.getSearchText();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        Page<Picture> picturePage = pictureService.getPicture(search_text,current,size);
        return ResultUtils.success(picturePage);
    }

}
