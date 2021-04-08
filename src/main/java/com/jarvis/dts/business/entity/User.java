package com.jarvis.dts.business.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/7 16:37
 * @Desc:
 **/
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private Integer age;
}
