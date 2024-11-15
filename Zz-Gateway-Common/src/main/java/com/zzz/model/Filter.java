package com.zzz.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class Filter {

    /**
     * 过滤器id
     */
    Long filterId;

    /**
     * 过滤器名称
     */
    String filterName;


}
