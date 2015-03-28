package org.demo.service;

import net.sf.json.JSONObject;
import org.demo.model.HwUser;
import org.demo.tool.UserType;

import java.io.Serializable;

/**
 * Created by jzchen on 2015/1/14.
 */
public interface IUserService {

    public JSONObject load(Serializable id);

    public HwUser findUser(String username);

    public void addUser(String json);

    public void deleteUser(Integer id);

    public void updateUser(String json);

    public JSONObject userInfo(HwUser user,UserType userType);

    public JSONObject userEmail(HwUser user);

}
