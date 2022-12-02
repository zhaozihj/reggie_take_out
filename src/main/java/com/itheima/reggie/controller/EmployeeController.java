package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /*
    登录功能
     */
    //加上外面类的实际就是/employee/login
    //这个是根据点击登陆后url得到的
    @PostMapping("/login")
    //这里使用@RequestBody是因为前端用ajax请求以json字符串格式发送数据
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee)
    {
        //   1.将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        // 2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //eq方法是判断这个有没有username是等于第二个参数的
        lambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        //数据库中设置username的时候，设置的是unique，所以最多只有一条，所以可以用getOne()方法
        Employee one = employeeService.getOne(lambdaQueryWrapper);

        //3.如果没有查询到则返回登陆失败结果
        //这个R.error就可以创建一个R对象，详情见R类那
        if(one==null){
            return R.error("用户名不存在");
        }
        // 4.密码比对，如果不一致则返回登陆失败结果
        if(!one.getPassword().equals(password)){
            return R.error("密码错误");
        }
        //    5.查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(one.getStatus()==0){
            return R.error("员工已被禁用");
        }
        //        6.登陆成功，将员工id存入Session并返回登陆成功结果
        request.getSession().setAttribute("employee",one.getId());
        return R.success(one);

    }


    /*
    登出功能
     */
@PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //1.清理Session中的用户id
    HttpSession session = request.getSession();
      session.removeAttribute("employee");

      //2.返回结果
    return R.success("退出成功");
}


/*
新增员工
 */
//路径是/employee所以不用加
@PostMapping
//@RequestBody接受json数据格式
    public R<String> saveEmployee(HttpServletRequest request,@RequestBody Employee employee){

    log.info("员工信息:{}",employee.toString());
    //设置默认密码，密码进行md5加密
    employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
    //下面代码被自动填充代替了
    /*
    //设置开始创立时间
    employee.setCreateTime(LocalDateTime.now());
    //设置更新创立时间
    employee.setUpdateTime(LocalDateTime.now());

    //获取当前用登陆用户的id
    //获取的默认是Object对象，进行向下转型
    long id = (Long)request.getSession().getAttribute("employee");
    employee.setCreateUser(id);
    employee.setUpdateUser(id);


     */
    employeeService.save(employee);
    return R.success("新增员工成功");
}

//查询分页信息
@GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
    log.info("page={},pageSize={},name={}",page,pageSize,name);

    //构造分页构造器
    Page pageInfo=new Page(page,pageSize);
    //构造条件构造器
    LambdaQueryWrapper<Employee> lambdaQueryWrapper=new LambdaQueryWrapper<>();
    //这个意思是在name不为空的情况下，加上这个条件是employee的name为传递的参数
    //第一个参数为true就加上这个条件，第一个参数为false就不加这个参数
    lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
    //添加排序条件
    lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

    //查询满足这个条件构造器的记录，然后分页之后对应的数据
    employeeService.page(pageInfo,lambdaQueryWrapper);
    return R.success(pageInfo);
}

//修改员工状态方法
    //前端传回来的是要修改的员工信息的id和修改之后的状态
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
    log.info(employee.toString());

//记录更新时间，和更新的人的id
        //获取登录用户的id
        //下面代码被自动填充代替了
        /*
        Long id = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(id);
        employee.setUpdateTime(LocalDateTime.now());
        */


       //进行更新操作
        employeeService.updateById(employee);
       return R.success("员工信息修改成功");
    }

    //根据id把员工信息显示到修改的页面上
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable String id) {

        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if(employee!=null) {
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
        }





}
