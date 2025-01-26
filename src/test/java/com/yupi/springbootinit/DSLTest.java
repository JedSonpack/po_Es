package com.yupi.springbootinit;

import java.util.Collections;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.esdao.PostEsDao;
import com.yupi.springbootinit.model.dto.post.PostEsDTO;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.descriptor.TaglibDescriptor;

@Slf4j
@SpringBootTest
public class DSLTest {

    @Resource
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void testTransJava(PostQueryRequest postQueryRequest) {
        Long id = postQueryRequest.getId();
        Long notId = postQueryRequest.getNotId();
        String searchText = postQueryRequest.getSearchText();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getContent();
        List<String> tags = postQueryRequest.getTags();
        List<String> orTags = postQueryRequest.getOrTags();
        Long userId = postQueryRequest.getUserId();
        int current = postQueryRequest.getCurrent();
        int pageSize = postQueryRequest.getPageSize();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 先过滤term
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        if (notId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("notId", notId));
        }
        if (userId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
        }

        // 必须包含所有标签
        if (CollUtil.isEmpty(tags)) {
            for (String tag : tags) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("tags", tag));
            }
        }

        // 包含任意一个标签就行
        if (CollUtil.isEmpty(orTags)) {
            // 内建一个新的
            BoolQueryBuilder orTagBoolQueryBuilder = QueryBuilders.boolQuery();
            for (String orTag : orTags) {
                orTagBoolQueryBuilder.should(QueryBuilders.termQuery("tags", orTag));
            }
            orTagBoolQueryBuilder.minimumShouldMatch(1);
            boolQueryBuilder.filter(orTagBoolQueryBuilder);
        }

        // 按照关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }

        // 按照 标题检索
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", title));
        }

        //按照 内容检索
        if (StringUtils.isNotBlank(content)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("content", title));
        }

        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        }

        //分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);

        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(pageRequest).
                withSorts(sortBuilder).build();

        SearchHits<PostEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, PostEsDTO.class);

    }

}
