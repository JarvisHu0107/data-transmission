package com.jarvis.dts.business.mapstruct;

import org.mapstruct.Mapper;

import com.jarvis.dts.business.doc.UserDoc;
import com.jarvis.dts.business.entity.User;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/7 16:44
 * @Desc:
 **/
@Mapper(componentModel = "spring")
public interface UserDocStructMapper {

    UserDoc convertEntity2Doc(User user);
}
