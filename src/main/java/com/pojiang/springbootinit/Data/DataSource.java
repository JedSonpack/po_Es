package com.pojiang.springbootinit.Data;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface DataSource<T> {
    Page<T> doSearch(String searchText, long pageIndex, long pageSize);
}
