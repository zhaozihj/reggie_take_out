package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/*
员工实体类
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    //驼峰命名法和数据库表中的字段名相对应
   //插入时填充字段
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;

    //插入和更新时填充字段
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    private Long createUser;

    //插入和更新时填充字段
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
