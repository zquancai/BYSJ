package org.demo.dao.impl;

import org.demo.dao.IUserDao;
import org.demo.model.HwUser;
import org.springframework.stereotype.Repository;

/**
 * Created by jzchen on 2015/1/13.
 */
@Repository
public class UserDao extends BaseDao<HwUser> implements IUserDao {
}
