package com.pojiang.springbootinit;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pojiang.springbootinit.model.entity.Picture;
import com.pojiang.springbootinit.model.entity.Post;
import com.pojiang.springbootinit.service.PostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class CrawlerTest {

    @Autowired
    private PostService postService;

    @Test
    public void getImg() throws IOException {
        String url = "https://cn.bing.com/images/search?q=恶搞之家&first=1";
        Document document = Jsoup.connect(url).get();
        Elements elements = document.select(".iuscp.isv.smallheight");
        List<Picture> picture_list = new ArrayList<>();
        for (Element element : elements) {
            String imgElm = element.select(".iusc").get(0).attr("m");
            Map<String, Object> bean = JSONUtil.toBean(imgElm, Map.class);
            String img_url = (String) bean.get("murl");
            String img_title = (String) bean.get("t");
//            System.out.println(img_url);
//            System.out.println(img_title);
            Picture picture = new Picture();
            picture.setTitle(img_title);
            picture.setUrl(img_url);
            picture_list.add(picture);
        }
        System.out.println(picture_list);
    }

    @Test
    public void getPassage() {
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
                .body(json)   //希望获取的内容
                .execute().body();
        Map<String, Object> m = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) m.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<Post> post_list = new ArrayList<>();
        for (Object record : records) {
            JSONObject temp_record = (JSONObject) record;
            Post post = new Post();
            post.setTitle(temp_record.getStr("title"));
            post.setContent(temp_record.getStr("description"));
            JSONArray tags = (JSONArray) temp_record.get("tags");
            List<String> list = tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(list));
            post.setUserId(1L);
            post_list.add(post);
        }
        boolean b = postService.saveBatch(post_list);
        Assertions.assertTrue(b);
    }
}
