package com.yupi.springbootinit.esdao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.dto.post.PostEsDTO;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Slf4j
public class PostEsDaoTest {
    @Resource
    private PostEsDao postEsDao;
    @Resource
    private PostService postService;

    @Test
    void test() {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        Page<Post> page = postService.searchFromEs(postQueryRequest);
        System.out.println(page);
    }

    @Test
    void testSelect() {
        System.out.println(postEsDao.count());
        org.springframework.data.domain.Page<PostEsDTO> postPage = postEsDao.findAll(PageRequest.of(0, 5, Sort.by("createTime")));
        List<PostEsDTO> postList = postPage.getContent();
        System.out.println(postList);
    }

    @Test
    void testAdd() {
        PostEsDTO postEsDTO = new PostEsDTO();
        postEsDTO.setId(2L);
        postEsDTO.setTitle("迫降今天去哪玩");
        postEsDTO.setContent("新年好啊");
        postEsDTO.setTags(Arrays.asList("java", "python"));
        postEsDTO.setUserId(1L);
        postEsDTO.setCreateTime(new Date());
        postEsDTO.setUpdateTime(new Date());
        postEsDTO.setIsDelete(0);
        postEsDao.save(postEsDTO);
        System.out.println(postEsDTO.getId());
    }

    @Test
    void testFindById(){
        Optional<PostEsDTO> postEsDTO = postEsDao.findById(1L);
        System.out.println(postEsDTO);
    }
    @Test
    void testCount(){
        System.out.println(postEsDao.count());
    }
    @Test
    void testFindByCategory(){
        List<PostEsDTO> postEsDTOList = postEsDao.findByUserId(1L);
        System.out.println(postEsDTOList);
    }
    @Test
    void findByTitle(){
        List<PostEsDTO> postEsDTOList = postEsDao.findByTitle("玩");
        System.out.println(postEsDTOList);
    }
}
