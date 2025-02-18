package com.pojiang.springbootinit.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pojiang.springbootinit.model.entity.Post;
import com.pojiang.springbootinit.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 全量同步帖子到 es
 *
 * 
 */
//取消注释后开启任务，会在springboot运行后初始化执行一次,开一次就行了，获取后继续注释掉component
//@Component
@Slf4j
public class FetchPostList implements CommandLineRunner {

    @Resource
    private PostService postService;


    @Override
    public void run(String... args) {

        String json = "{" +
                "  \"pageSize\": 12,\n" +
                "  \"sortOrder\": \"descend\",\n" +
                "  \"sortField\": \"createTime\",\n" +
                "  \"tags\": [],\n" +
                "  \"current\": 1,\n" +
                "  \"reviewStatus\": 1,\n" +
                "  \"category\": \"文章\",\n" +
                "  \"hiddenContent\": true\n" +
                "}";
        String url = "https://api.codefather.cn/api/post/list/page/vo";
        String result = HttpRequest.post(url)
                .body(json)
                .execute().body();
        Map<String, Object> m = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) m.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<Post> post_list = new ArrayList<>();
        for (Object record : records) {
            JSONObject temp_record = (JSONObject) record;
            Post post = new Post();
            post.setTitle(temp_record.getStr("title"));
            post.setContent(temp_record.getStr("content"));
            JSONArray tags = (JSONArray) temp_record.get("tags");
            List<String> list = tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(list));
            post.setUserId(1L);
            post_list.add(post);
        }
        boolean b = postService.saveBatch(post_list);
        if (b) {
            log.info("获取初始化列表完成！！！获取了{}条", post_list.size());
        } else {
            log.error("获取初始化列表失败！！！");
        }
    }
}
