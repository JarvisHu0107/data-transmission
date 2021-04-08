package com.jarvis.dts.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.jarvis.dts.strategy.AbstractDataTransmitStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/1 23:15
 * @Desc:
 **/
@Slf4j
public class ClassFinder {

    /**
     * 扫描指定路径下的所有类，找到指定类型的子类
     *
     * @param superClass
     * @param scanPath
     *            com/zy/sms。不指定则进行全部路径扫描
     * @return
     */
    public static List<Class<?>> findSubclass(Class superClass, String scanPath) {
        if (StringUtils.isEmpty(scanPath)) {
            scanPath = "*";
        }
        List<Class<?>> subclassList = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        // 接口不会被扫描出来，只有类会被扫描出来
        provider.addIncludeFilter(new AssignableTypeFilter(superClass));

        // scan in com.zy.sms
        Set<BeanDefinition> components = provider.findCandidateComponents(scanPath);
        for (BeanDefinition component : components) {
            try {
                Class cls = Class.forName(component.getBeanClassName());
                // use class cls found
                if (superClass.isAssignableFrom(cls)) {
                    subclassList.add(cls);
                }
            } catch (Exception e) {
                log.error("找指定父类" + superClass + "下的所有子类，路径为" + scanPath + "，出错", e);
            }
        }

        return subclassList;
    }

    public static void main(String[] args) {
        System.out.println(findSubclass(AbstractDataTransmitStrategy.class, null));
    }

}
