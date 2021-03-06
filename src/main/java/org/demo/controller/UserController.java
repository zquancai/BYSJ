package org.demo.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.demo.model.*;
import org.demo.service.IStudentService;
import org.demo.service.ITeacherService;
import org.demo.service.IUserService;
import org.demo.tool.ObjectJsonValueProcessor;
import org.demo.tool.UserType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzchen on 2015/3/20 0020.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private IStudentService studentService;
    private ITeacherService teacherService;
    private IUserService userService;

    /**
     * 获取登录用户的个人详细信息
     */
    @RequestMapping(value = "/info",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject userInfo(HttpServletRequest request) {
        /**获取登录用户信息*/
        HwUser user = (HwUser)request.getSession().getAttribute("loginUser");
        UserType userType = (UserType)request.getSession().getAttribute("userType");

        try{
            return userService.userInfo(user,userType);
        }catch (Exception e) {
            e.printStackTrace();
            return getFailResultJsonObject();
        }
    }

    /**
     * 修改邮箱之前获取登录用户的邮箱和用户名username
     */
    @RequestMapping(value = "/email",method = RequestMethod.GET)
    @ResponseBody
    public Object userEmail(HttpServletRequest request) {
        HwUser user = (HwUser)request.getSession().getAttribute("loginUser");
       try {
           return userService.userEmail(user);
       }catch (Exception e) {
           e.printStackTrace();
           return getFailResultJsonObject();
       }
    }

    /**
     * 修改用户密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @RequestMapping(value = "/updatePassword",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject updatePassword(String oldPassword, String newPassword, HttpServletRequest request) {
        try{
            HwUser user = (HwUser)request.getSession().getAttribute("loginUser");
            return userService.updatePassword(oldPassword, newPassword, user);
        }catch (Exception e){
            e.printStackTrace();
            return getFailResultJsonObject();
        }
    }

    @RequestMapping(value = "/updateEmail",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject updateEmail(String email, HttpServletRequest request){
        try{
            HwUser user = (HwUser)request.getSession().getAttribute("loginUser");
            userService.updateEmail(email, user);
            return null;
        }catch (Exception e) {
            e.printStackTrace();
            return getFailResultJsonObject();
        }
    }

    /****************************管理员功能**************************/

    /**
     * 根据条件筛选用户列表
     * @param username 用户名
     * @param trueName 真名
     * @param userType 用户类型
     * @return
     */
    @RequestMapping(value = "/userList",method = RequestMethod.GET)
    @ResponseBody
    public Object userList(String username, String trueName, String userType){
        try{
            return userService.serachUser(username, trueName, userType);
        }catch (Exception e){
            e.printStackTrace();
            return getFailResultJsonObject();
        }
    }

    /**
     * 根据用户id查看用户详细，用于更新用户
     * @param userId 用户id
     * @return
     */
    @RequestMapping(value = "/userDetail",method = RequestMethod.GET)
    @ResponseBody
    public Object userDetail(Integer userId){
        try {
            return userService.userDetail(userId);
        }catch (Exception e){
            e.printStackTrace();
            return getFailResultJsonObject();
        }
    }


    /**
     * 更改用户密码
     * @param userId 用户id
     * @param managerPassword 管理员密码
     * @param newPassword 新密码
     */
    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    @ResponseBody
    public Object updateUser(Integer userId,String managerPassword, String newPassword, HttpServletRequest request){
        try{
            HwUser user = (HwUser)request.getSession().getAttribute("loginUser");
            return userService.updatePassword(userId ,user, managerPassword, newPassword);
        }catch (Exception e){
            e.printStackTrace();
            return getFailResultJsonObject();
        }
    }



    public IStudentService getStudentService() {
        return studentService;
    }

    @Resource
    public void setStudentService(IStudentService studentService) {
        this.studentService = studentService;
    }

    public ITeacherService getTeacherService() {
        return teacherService;
    }

    @Resource
    public void setTeacherService(ITeacherService teacherService) {
        this.teacherService = teacherService;
    }

    public IUserService getUserService() {
        return userService;
    }

    @Resource
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    private JSONObject getFailResultJsonObject(){
        JSONObject result = new JSONObject();
        result.put("status","fail");
        return result;
    }
    private JSONObject getSuccessResultJsonObject(){
        JSONObject result = new JSONObject();
        result.put("status","success");
        return result;
    }
}
