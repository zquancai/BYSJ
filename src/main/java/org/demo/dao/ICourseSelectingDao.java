package org.demo.dao;

import org.demo.model.HwCourseSelecting;
import org.demo.model.HwCourseTeaching;
import org.demo.model.HwStudent;
import org.demo.tool.Page;

import java.util.List;

/**
 * Created by jzchen on 2015/1/28.
 */
public interface ICourseSelectingDao extends IBaseDao<HwCourseSelecting> {
    public Page<HwCourseSelecting> courseSelectingPage(Integer courseTeachingId);

    public Page<HwCourseSelecting> courseSelectingPage(HwStudent student, Integer startYear, Integer schoolTerm);

    public List<HwCourseSelecting> courseSelectingList(Integer courseTeachingId);

    public Long countByCtId(Integer ctId);

    public HwCourseSelecting findCSByCTAndStudent(HwCourseTeaching courseTeaching, HwStudent student);
}
