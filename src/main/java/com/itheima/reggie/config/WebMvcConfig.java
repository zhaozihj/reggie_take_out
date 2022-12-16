package com.itheima.reggie.config;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.JacksonObjectMapper;
import com.itheima.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.RequestContext;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.alibaba.fastjson.JSONPath.paths;

@EnableSwagger2
@EnableKnife4j
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    //配置拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("拦截器加载成功");
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")  //所有请求都被拦截包括静态资源
                .excludePathPatterns("/employee/login","/backend/**","/front/**","/user/sendMsg","/user/login","/doc.html","/webjars/**","/swagger-resources","/v2/api-docs"); //放行的请求
}

    //配置静态资源的映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射");
        //这个的意思是  /backend/**请求对应到 类路径下的backend文件夹下的资源
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:front/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    //扩展mvc框架消息转换器
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器");
        //创建消息转换器对象
        //作用：将controller方法的返回值转成相应的json，再通过输出流响应给我们页面
        MappingJackson2HttpMessageConverter messageConverter=new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将java对象转换为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        //第一个参数是指顺序，0就是第一个，把我们的转换器放到第一个
        converters.add(0,messageConverter);
    }

    @Bean
    public Docket createRestApi(){
        //文档类型
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //对应controller一个包结构，生成接口会扫描这个包
                //controller中每一个公共方法都会映射成一个接口
                .apis(RequestHandlerSelectors.basePackage("com.itheima.reggie.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    //封装这个接口文档的标题版本描述之类的
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("瑞吉外卖")
                .version("1.0")
                .description("瑞吉外卖接口文档")
                .build();
    }


}