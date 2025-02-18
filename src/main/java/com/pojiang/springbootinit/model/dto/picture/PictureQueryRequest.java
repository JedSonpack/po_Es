package com.pojiang.springbootinit.model.dto.picture;

import com.pojiang.springbootinit.common.PageRequest;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询请求
 * 限制请求的条件，只能通过搜索词
 * 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PictureQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;


    private static final long serialVersionUID = 1L;
}