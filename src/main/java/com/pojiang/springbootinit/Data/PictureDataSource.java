package com.pojiang.springbootinit.Data;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pojiang.springbootinit.common.ErrorCode;
import com.pojiang.springbootinit.model.entity.Picture;
import com.pojiang.springbootinit.exception.BusinessException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PictureDataSource implements DataSource<Picture> {
    @Override
    public Page<Picture> doSearch(String searchText, long pageIndex, long pageSize) {
        long current = (pageIndex - 1) * pageSize;
        if (searchText.equals(""))
            searchText = "null";
        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%s", searchText, current);
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加载图片失败！！！");
        }
        Elements elements = document.select(".iuscp.isv.smallheight");
        List<Picture> picture_list = new ArrayList<>();
        for (Element element : elements) {
            String imgElm = element.select(".iusc").get(0).attr("m");
            Map<String, Object> bean = JSONUtil.toBean(imgElm, Map.class);
            String img_url = (String) bean.get("murl");
            String img_title = (String) bean.get("t");
            Picture picture = new Picture();
            picture.setTitle(img_title);
            picture.setUrl(img_url);
            picture_list.add(picture);
            if (picture_list.size() >= pageSize) {
                break;
            }
        }
        Page<Picture> page = new Page<>(pageIndex, pageSize);
        page.setRecords(picture_list);
        return page;

    }
}
