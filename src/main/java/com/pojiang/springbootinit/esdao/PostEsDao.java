package com.pojiang.springbootinit.esdao;

import com.pojiang.springbootinit.model.dto.post.PostEsDTO;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 帖子 ES 操作
 *
 * 
 */

//适合简单的操作
public interface PostEsDao extends ElasticsearchRepository<PostEsDTO, Long> {

    List<PostEsDTO> findByUserId(Long userId);
    List<PostEsDTO> findByTitle(String title);
}