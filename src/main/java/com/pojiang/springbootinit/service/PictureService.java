package com.pojiang.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pojiang.springbootinit.model.entity.Picture;

/**
 * 图片服务
 *
 * 
 */
public interface PictureService{


    /**
     * 分页获取图片封装
     *
     */
    Page<Picture> getPicture(String searchText, long pageIndex, long pageSize);
}
