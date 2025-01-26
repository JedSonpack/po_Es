package com.yupi.springbootinit.Data;

import com.yupi.springbootinit.model.enums.SearchTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataSourceRegister {
    @Resource
    private UserDataSource userDataSource;
    @Resource
    private PictureDataSource pictureDataSource;
    @Resource
    private PostDataSource postDataSource;
    private HashMap<String, DataSource<?>> hm;

    @PostConstruct
    public void doInit() {
        hm = new HashMap<String, DataSource<?>>() {{
            put(SearchTypeEnum.POST.getValue(), postDataSource);
            put(SearchTypeEnum.USER.getValue(), userDataSource);
            put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
        }};
    }

    public DataSource<?> getDataSourceByType(String type) {
        if (hm == null) {
            return null;
        }
        return hm.get(type);
    }
}