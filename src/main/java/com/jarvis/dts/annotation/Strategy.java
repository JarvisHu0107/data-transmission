package com.jarvis.dts.annotation;

import java.lang.annotation.*;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/2 12:13
 * @Desc:
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Strategy {
    /**
     * 库名
     * 
     * @return
     */
    String schema();

    /**
     * 表名
     * 
     * @return
     */
    String table();

    /**
     * 主键,支持联合主键
     * 
     * @return
     */
    String[] pk() default {};
}
