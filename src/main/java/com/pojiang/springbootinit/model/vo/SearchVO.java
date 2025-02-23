package com.pojiang.springbootinit.model.vo;

import com.pojiang.springbootinit.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 聚合搜索视图
 * 返回给前端
 *
 * 
 */
@Data
public class SearchVO implements Serializable {

    private List<UserVO> userList;
    private List<PostVO> postList;
    private List<Picture> pictureList;
    private List<?> dataList;

    // 序列化

    private static final long serialVersionUID = 1L;


}
