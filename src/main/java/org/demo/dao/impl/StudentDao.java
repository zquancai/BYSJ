package org.demo.dao.impl;

import org.demo.dao.IStudentDao;
import org.demo.model.HwStudent;
import org.springframework.stereotype.Repository;

/**
 * Created by jzchen on 2015/1/13.
 */
@Repository
public class StudentDao extends BaseDao<HwStudent> implements IStudentDao {
    @Override
    public HwStudent findStudnetByStudentNo(String studentNo) {
        String hql  = "from HwStudent st where st.studentNo = ? " +
                "and st.deleteFlag = false";
        return findObject(hql,studentNo);
    }

    @Override
    public HwStudent findDeleteStudnet(String studentNo) {
        String hql  = "from HwStudent st where st.studentNo = ? " +
                "and st.deleteFlag = true";
        return findObject(hql,studentNo);
    }
}
