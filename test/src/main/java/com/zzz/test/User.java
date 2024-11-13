package com.zzz.test;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user") // 指定表名称
public class User {

    @TableId
    private Long uid; // 用户ID

    @TableField(value = "userNo")
    private String userNo; // 健康号

    private String nickname; // 昵称

    private String mobile; // 手机号

    @TableField(value = "appId")
    private String appId; // appId

    private String avatar; // 头像

    private String gender; // 性别

    private LocalDateTime posttime; // 注册时间

    private LocalDateTime lmodify; // 最后修改时间

    @TableField(value = "mobileAes")
    private String mobileAes; // AES加密电话号码
}
