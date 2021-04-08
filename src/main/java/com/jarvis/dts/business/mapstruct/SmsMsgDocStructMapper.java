package com.jarvis.dts.business.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.jarvis.dts.business.doc.SmsMsgDoc;
import com.jarvis.dts.business.entity.SmsMsg;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/2 17:34
 * @Desc:
 **/
@Mapper(componentModel = "spring")
public interface SmsMsgDocStructMapper {

    @Mappings({@Mapping(source = "msgId", target = "id")})
    SmsMsgDoc convertEntity2Doc(SmsMsg msg);

}
