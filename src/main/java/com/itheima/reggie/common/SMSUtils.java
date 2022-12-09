package com.itheima.reggie.common;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

/**
 * 短信发送工具类
 */
public class SMSUtils {

    /**
     * 发送短信
     * @param signName 签名，签名就是之前申请的签名
     * @param templateCode 模板，申请的模板有一个属性就是模板Code
     * @param phoneNumbers 手机号,要发送短信的手机号
     * @param param 参数，是用来把模板内容中的${code}换为这个param
     */
    public static void sendMessage(String signName, String templateCode,String phoneNumbers,String param){
        //后面两个参数分别是AccessID合AccessSecret
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5tC1JHJ7AWcoZqCjGbws", "oeb0As1PbClkyH4SkUqNUIBp6ZTPu0");
        IAcsClient client = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setSysRegionId("cn-hangzhou");
        request.setPhoneNumbers(phoneNumbers);
        request.setSignName(signName);
        request.setTemplateCode(templateCode);
        request.setTemplateParam("{\"code\":\""+param+"\"}");
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println("短信发送成功");
        }catch (ClientException e) {
            e.printStackTrace();
        }
    }

}