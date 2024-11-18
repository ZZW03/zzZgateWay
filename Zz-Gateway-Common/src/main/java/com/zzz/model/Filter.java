package com.zzz.model;
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
