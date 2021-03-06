package org.demo.service;

import org.demo.model.HwCollege;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jzchen on 2015/3/16 0016.
 */
public interface ICollegeService {

    public HwCollege load(Integer id);
    public void addCollege(int campusId,String collegeName);
    public void updateCollege(int collegeId,int campusId,String collegeName);
    public List<Map<String,Object>> getCollegeByCampus(int campusId);

    //ChenJianzhao 2015.04.04
    public Map allCollege();
}
