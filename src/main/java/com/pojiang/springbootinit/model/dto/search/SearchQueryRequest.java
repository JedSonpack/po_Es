package com.pojiang.springbootinit.model.dto.search;

import com.pojiang.springbootinit.common.PageRequest;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询请求
 *
 * 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchQueryRequest extends PageRequest implements Serializable {

    public String searchText;
    public String type;
    private static final long serialVersionUID = 1L;
}