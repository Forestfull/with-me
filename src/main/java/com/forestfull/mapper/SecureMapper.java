package com.forestfull.mapper;

import com.forestfull.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SecureMapper {

    UserEntity getUserEntity(@Param("email") String email);
}
