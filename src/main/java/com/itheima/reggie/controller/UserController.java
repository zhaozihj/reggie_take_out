package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.common.SMSUtils;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone=user.getPhone();
        if(phone!=null){
            //获取验证码
            String code = ValidateCodeUtils.generateValidateCode4String(4).toString();

            //发送短信
            SMSUtils.sendMessage("rgwm","SMS_262585563",phone,code);

            //将发送的验证码放到session域中，方便与用户输入的作比较
            session.setAttribute("code",code);

            return R.success("手机验证码短信发送成功");
        }


        return R.error("手机验证码短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody  User user, HttpServletRequest request){

        HttpSession session=request.getSession();
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getPhone,user.getPhone());
        User user1 = userService.getOne(lambdaQueryWrapper);
        //如果是第一次登录的用户就保存用户信息到数据库
        if(user1==null){
            User user2=new User();
            user2.setPhone(user.getPhone());
            userService.save(user2);
            session.setAttribute("user",user2.getId());
        }
        else {
            //这里是因为过滤器里面要求必须有user才能够放行
            session.setAttribute("user", user1.getId());
        }
        return R.success(user1);
    }

}