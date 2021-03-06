package org.demo.service.impl;

import freemarker.template.utility.DateUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.io.FileUtils;
import org.demo.dao.*;
import org.demo.dao.impl.StudentDao;
import org.demo.dao.impl.UserDao;
import org.demo.model.*;
import org.demo.service.IHomeworkInfoService;
import org.demo.service.IHomeworkService;
import org.demo.tool.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.persistence.OneToMany;
import javax.tools.Tool;
import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jzchen on 2015/1/14.
 */

@Service
public class HomewrokService implements IHomeworkService {

    private String homeworkBaseDir;
    private IHomeworkDao homeworkDao;
    private ICourseSelectingDao courseSelectingDao;
    private ICourseTeachingDao courseTeachingDao;
    private IHomeworkInfoDao homeworkInfoDao;
    private IStudentDao studentDao;
    private ITeacherDao teacherDao;
    private IHomeworkInfoService homeworkInfoService;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void add(HwHomework homework) {
        if( homework != null )
            homeworkDao.add(homework);
    }

    /**
     * 已提交或未提交（未提交的也已经初始化作业对象）的学生作业分页
     * @param hwInfoId 作业信息id
     * @param submitted 是否已经提交的
     * @return 分页的JSONObjcet 对象
     */
    @Override
    public JSONObject submittedHomeworkPage(Integer hwInfoId, boolean submitted) {
        Page page = homeworkDao.submittedHomeworkPage(hwInfoId, submitted);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setExcludes(new String[] { "hibernateLazyInitializer", "handler","hwCourse","hwHomeworkInfo","hwTeacher","hwStudent"});
        jsonConfig.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
        return JSONObject.fromObject(page, jsonConfig);
    }

    @Override
    public HwHomework load(Integer id) {
        return homeworkDao.load(id);
    }

    @Override
    public void update(HwHomework homework) {
        homeworkDao.update(homework);
    }

    /**
     * 学生管理，根据授课关系id，学生id，查询该生该门课程所有布置的作业详细信息列表
     * @param courseTeachingId 授课关系id
     * @param studentId 学生id
     * @return 作业分页JSONObject
     */
    @Override
    public List homeworkList(Integer courseTeachingId, Integer studentId) {
        List<Object[]> homeworkList = homeworkDao.homeworkList(courseTeachingId, studentId);
        List<Map<String,Object>> homeworkViewList = new ArrayList<Map<String, Object>>();
        for( Object[] hw : homeworkList ){
            Map<String, Object> homeworkView = new HashMap<String, Object>();
            homeworkView.put("id", hw[0]);
            homeworkView.put("checkFlag", hw[1]);
            homeworkView.put("hwNo", hw[2]);
            homeworkView.put("lastModifyDate",simpleDateFormat.format(hw[3]));
            homeworkView.put("mark",hw[4]);
            homeworkView.put("markDate",hw[5]); //可能为空，
            homeworkView.put("markType",hw[6]);
            homeworkView.put("status",hw[7]);
            homeworkView.put("studentName",hw[8]);
            homeworkView.put("studentNo",hw[9]);
            homeworkView.put("submitDate",hw[10]); //可能为空，未格式化
            homeworkView.put("title",hw[11]);
            homeworkView.put("url",hw[12]);
            homeworkView.put("deadline",simpleDateFormat.format(hw[13]));
            homeworkView.put("overtime",hw[14]);
            homeworkView.put("courseName",hw[15]);
            homeworkViewList.add(homeworkView);
        }

        return homeworkViewList;
    }

    //根据学年，学期，当前登录学生用户筛选选课关系、课程列表分页
    @Override
    public JSONObject courseSelectingPage(HwUser user, Integer startYear, Integer schoolTerm) {
        HwStudent student = studentDao.load(user.getTypeId());
        Page page =  courseSelectingDao.courseSelectingPage(student, startYear, schoolTerm);
        Page newPage = new Page();
        List<HwCourseTeaching> list = new ArrayList<HwCourseTeaching>();
        /**获取选课关系中的授课关系，构造分页类*/
        for(Object o : (List)page.getData() ) {
            HwCourseSelecting cs =  (HwCourseSelecting) o;
            list.add(cs.getHwCourseTeaching());
        }
        newPage.setData(list);
        newPage.setPageSize(page.getPageSize());
        newPage.setTotalRecord(page.getTotalRecord());
        newPage.setPageOffset(page.getPageOffset());

        JsonConfig jsonConfig = new JsonConfig();
        /**过滤简单属性*/
        jsonConfig.setExcludes(new String[] {"hibernateLazyInitializer", "handler",
                "hwTeacher","hwHomeworkInfos","password","hwCourseSelectings","email"});
        /**过滤复杂属性*/
        jsonConfig.registerJsonValueProcessor( HwCourse.class,
                new ObjectJsonValueProcessor(new String[] {"id","courseNo","courseName"}, HwCourse.class) );
        return JSONObject.fromObject(newPage, jsonConfig);
    }

    //根据学年，学期，当前登录教师用户筛选选课关系、课程列表分页
    @Override
    public JSONObject courseTeachingPage(HwUser user, Integer startYear, Integer schoolTerm) {
        HwTeacher teacher = teacherDao.load(user.getTypeId());
        Page page =  courseTeachingDao.courseTeachingPage(teacher, startYear, schoolTerm);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setExcludes(new String[] {"hibernateLazyInitializer", "handler",
                "hwTeacher","hwHomeworkInfos","password","hwCourseSelectings","email"});
        jsonConfig.registerJsonValueProcessor( HwCourse.class,
                new ObjectJsonValueProcessor(new String[] {"id","courseNo","courseName"}, HwCourse.class) );
        return JSONObject.fromObject(page, jsonConfig);
    }

    //根据授课关系id，当前登录用户类型返回相应的课程作业信息。
    //教师显示每次作业的统计信息，学生显示每次作业自己的上交/评分情况
    @Override
    public JSONArray homeworListInfo(Integer courseTeachingId, HwUser user) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(Timestamp.class,new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
        HwCourseTeaching ct = courseTeachingDao.load(courseTeachingId);

        //登录角色为学生。
        if( user.getUserType() == UserType.STUDENT ){
            HwStudent student = studentDao.load(user.getTypeId());
            //HwCourseSelecting cs = courseSelectingDao.findCSByCTAndStudent(ct, student);
            List<Object[]> studentHwInfoList = homeworkInfoDao.studentHomeworkLsit(ct, student);
            List<HashMap<String,Object>> studentResultList = new ArrayList<HashMap<String,Object>>();
            //构造键值
            for(Object[] o : studentHwInfoList) {
                HashMap<String, Object> resultMap = new HashMap<String, Object>();
                resultMap.put("hwInfoId", o[0]);
                resultMap.put("title",o[1]);
                resultMap.put("overtime",o[2]);
                resultMap.put("deadline",o[3]);
                resultMap.put("status",o[4]);
                resultMap.put("courseName", o[5]);
                studentResultList.add(resultMap);
            }
            return JSONArray.fromObject(studentResultList, jsonConfig);
        }
        //登录角色为教师
        else {
            //根据授课关系id统计该门课程选课总人数，即应交作业总人数。
            Long sum = courseSelectingDao.countByCtId(courseTeachingId);
            //根据授课关系id统计该门课程每次作业已经上交的人数。
            List<Object[]> countSubmittedList = homeworkDao.countSubmitted(ct.getHwCourse().getId(), ct.getHwTeacher().getId());
            //存放返回结果的列表
            List<HashMap<String,Object>> teacherResultList = new ArrayList<HashMap<String,Object>>();
            //构造键值
            for(Object[] o : countSubmittedList) {
                HashMap<String, Object> resultMap = new HashMap<String, Object>();
                resultMap.put("hwInfoId", o[0]);
                resultMap.put("title",o[1]);
                resultMap.put("deadline",o[2]);
                resultMap.put("overtime",o[3]);
                resultMap.put("submitted",o[4]);
                resultMap.put("courseName",o[5]);
                resultMap.put("sum",sum);
                teacherResultList.add(resultMap);
            }
            return JSONArray.fromObject(teacherResultList,jsonConfig);
        }
    }

    //根据信息id查看作业信息详细
    @Override
    public JSONObject homeworkInfoDetail(Integer hwInfoId) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setExcludes(new String[]{"hwHomeworks","hwCourseTeaching","url",
                "hibernateLazyInitializer", "handler"});
        jsonConfig.registerJsonValueProcessor(Timestamp.class,new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
        return JSONObject.fromObject(homeworkInfoDao.load(hwInfoId), jsonConfig);
    }

    //教师布置作业，同时为每个选课的学生初始化该次作业信息对应的作业对象
    @Override
    public void addHomeworkInfo(String jsonObject, HwUser user) throws Exception {
        HwTeacher teacher = teacherDao.load(user.getTypeId());
        /** 将前端传递过来的json字符串解析成JsonObject对象 */
        JSONObject jo = JSONObject.fromObject(jsonObject);
        Integer cid = jo.getInt("cid");
        /**查询出对应的courseTeaching*/
        HwCourseTeaching courseTeaching = courseTeachingDao.load(cid);
        /** 构造一个新的HwHomeworkInfo对象 */
        HwHomeworkInfo hwinfo = new HwHomeworkInfo();
        /** 从前端传递过来的json中解析出参数 */
        hwinfo.setTitle(jo.getString("title"));
        hwinfo.setHwDesc(jo.getString("hwDesc"));
        hwinfo.setDeadline(new java.sql.Timestamp(jo.getLong("deadline")));
        hwinfo.setMarkType(MarkType.valueOf(jo.getString("markType")));

        /**往HwHomeworkInfo填入其他信息*/
        hwinfo.setCourseName(courseTeaching.getHwCourse().getCourseName());
        hwinfo.setCreateDate(new java.sql.Timestamp(System.currentTimeMillis()));
        hwinfo.setDeleteFlag(false);
        String emai = courseTeaching.getEmail();
        if( emai == null || emai.equals("") ){
            throw new Exception("未设置该课程收件邮箱");
        }
        hwinfo.setEmail(courseTeaching.getEmail());
        hwinfo.setHwCourseTeaching(courseTeaching);
        hwinfo.setOvertime(false);
        String url = "/" + courseTeaching.getStartYear().toString()
                + "/" + courseTeaching.getSchoolTerm().toString()
                + "/" + courseTeaching.getHwCourse().getCourseNo()
                + "/" + teacher.getTeacherNo() + "/";
        //+ "/" + hwinfo.getId() + "/" ;

        hwinfo.setUrl(url);
        homeworkInfoDao.add(hwinfo);

        /**查询出所有选课的学生
         * 初始化该次作业所有选课学生的作业。
         * */
        List<HwCourseSelecting> csList = courseSelectingDao.courseSelectingList(cid);
        for (HwCourseSelecting cs : csList) {
            //构建一个新的作业对象
            HwHomework hw = new HwHomework();
            hw.setHwStudent(cs.getHwStudent());
            hw.setHwCourse(cs.getHwCourseTeaching().getHwCourse());
            hw.setCheckedFlag(false);
            hw.setHwHomeworkInfo(hwinfo);
            hw.setHwTeacher(teacher);
            hw.setStudentName(cs.getHwStudent().getName());
            hw.setStudentNo(cs.getHwStudent().getStudentNo());
            hw.setTitle(hwinfo.getTitle());
            hw.setLastModifyDate(new java.sql.Timestamp(System.currentTimeMillis()));
            hw.setMarkType(hwinfo.getMarkType());
            hw.setUrl("");
            hw.setStatus(HomeworkStatus.UNSUBMITTED);
            hw.setHwNo("");
            hw.setMark("");
            hw.setComment("");
            homeworkDao.add(hw);
        }
    }

    //学生上传作业
    @Override
    public void upload(MultipartFile hw, Integer hwinfoId,HwUser user, String backupPath ) throws IOException {
        HwHomeworkInfo hwinfo = homeworkInfoDao.load(hwinfoId);
        HwStudent student = studentDao.load(user.getTypeId());
        /**拼装文件名以及获取文件后缀名*/
        String hwName = student.getStudentNo() + "_" +student.getName() +
                hw.getOriginalFilename().substring( hw.getOriginalFilename().lastIndexOf("."));
        //获取作业信息的 baseUrl
        String baseUrl = hwinfo.getUrl();
        //拼接最终的文件url
        String url = baseUrl + hwinfo.getId() + "/" + hwName;
        System.out.println(url);
        HwCourseTeaching ct = hwinfo.getHwCourseTeaching();
        //拼接作业编号
        String hwNo = ct.getStartYear().toString() + ct.getSchoolTerm().toString() +  ct.getHwCourse().getCourseNo()  + student.getStudentNo();
        //获取作业对象并更新作业的信息
        HwHomework homework = homeworkDao.findHomework(hwinfoId, student);
        System.out.println(homework.getId());
        homework.setTitle(hwinfo.getTitle());
        homework.setUrl(url);
        homework.setHwNo(hwNo);
        homework.setSubmitDate(new java.sql.Timestamp(System.currentTimeMillis()));
        homework.setStatus(HomeworkStatus.SUBMITTED);
        homeworkDao.update(homework);

        /**判断预设的目录的是否存在，不存在则使用web应用路径下的默认目录*/
        //声明最终的路径
        String realPath = homeworkBaseDir;
        try {
            /* 若指定目录不存在，则抛异常*/
            File dirname = new File(homeworkBaseDir);
            if ( !dirname.isDirectory() ) {
                throw new NotDirectoryException("homeworkBaseDir ---> " + homeworkBaseDir + " is not found! "
                        + " files were all putted into [" + backupPath + "]");
            }
            realPath = realPath + "/doc";
        } catch (NotDirectoryException e ) {
            /**使用web应用路径下的默认目录*/
            e.printStackTrace();
            realPath = backupPath;
        }finally {
            //realPath = backupPath;
            System.out.println("homeworkBaseDir ---> " + realPath);
            File f = new File( realPath + url );
            FileUtils.copyInputStreamToFile(hw.getInputStream(), f);
        }
    }

    //删除作业信息，同时将级联删除该次作业信息对应的所有学生作业
    @Override
    public void deleteHomeworkInfo(Integer hwInfoId){
        /*Page page = homeworkDao.submittedHomeworkPage(hwInfoId, true);
        //没有学生已经提交作业则执行删除
        if( page.getData().isEmpty()) {
            List<HwHomework> hwList = homeworkDao.homeworkList(hwInfoId);
            for(HwHomework hw : hwList) {
                homeworkDao.delete(hw);
            }
            homeworkInfoDao.delete(homeworkInfoDao.load(hwInfoId));
        }else {
            throw new Exception("已有学生提交作业，不可删除作业信息！");
        }*/
       /* HwHomeworkInfo hwInfo = homeworkInfoDao.load(hwInfoId);
        hwInfo.setDeleteFlag(true);
        homeworkInfoDao.update(hwInfo)*/;

        List<HwHomework> homeworkList = homeworkDao.homeworkList(hwInfoId);
        for( HwHomework hw : homeworkList ){
            homeworkDao.delete(hw);
        }
        homeworkInfoDao.delete(homeworkInfoDao.load(hwInfoId));

    }

    //教师批改完作业之后更新作业信息。
    @Override
    public void updateHomework(Integer hwId, String mark, String comment) {
        HwHomework homework = homeworkDao.load(hwId);
        homework.setMark(mark);
        homework.setComment(comment);
        Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
        homework.setMarkDate(timestamp);
        homework.setLastModifyDate(timestamp);
        homework.setStatus(HomeworkStatus.MARKED);
        homeworkDao.update(homework);
    }

    //返回当前登录学生某次作业的评评语
    @Override
    public Map<String,Object>  comment(Integer hwInfoId, HwUser user) {
        HwStudent student = studentDao.load(user.getTypeId());
        Object[] hw =  (Object[])homeworkDao.feedbackDetail(hwInfoId, student);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("comment",hw[0]);
        resultMap.put("mark",hw[1]);
        resultMap.put("markType",hw[2]);
        return resultMap;
    }

    //标记该反馈为已读
    @Override
    public void updateCheckedFlag(Integer hwInfoId, HwUser user) {
        HwStudent student = studentDao.load(user.getTypeId());
        HwHomework homework = homeworkDao.findHomework(hwInfoId, student);
        if( !homework.getCheckedFlag() ){
            homework.setCheckedFlag(true);
            homeworkDao.update(homework);
        }
    }


    public IHomeworkDao getHomeworkDao() {
        return homeworkDao;
    }

    @Resource
    public void setHomeworkDao(IHomeworkDao homeworkDao) {
        this.homeworkDao = homeworkDao;
    }

    public ICourseSelectingDao getCourseSelectingDao() {
        return courseSelectingDao;
    }

    @Resource
    public void setCourseSelectingDao(ICourseSelectingDao courseSelectingDao) {
        this.courseSelectingDao = courseSelectingDao;
    }

    public ICourseTeachingDao getCourseTeachingDao() {
        return courseTeachingDao;
    }

    @Resource
    public void setCourseTeachingDao(ICourseTeachingDao courseTeachingDao) {
        this.courseTeachingDao = courseTeachingDao;
    }

    public IHomeworkInfoDao getHomeworkInfoDao() {
        return homeworkInfoDao;
    }

    @Resource
    public void setHomeworkInfoDao(IHomeworkInfoDao homeworkInfoDao) {
        this.homeworkInfoDao = homeworkInfoDao;
    }

    public String getHomeworkBaseDir() {
        return homeworkBaseDir;
    }

    @Value("${homeworkDir}")
    public void setHomeworkBaseDir(String homeworkBaseDir) {
        this.homeworkBaseDir = homeworkBaseDir;
    }

    public IStudentDao getStudentDao() {
        return studentDao;
    }

    @Resource
    public void setStudentDao(IStudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public ITeacherDao getTeacherDao() {
        return teacherDao;
    }

    @Resource
    public void setTeacherDao(ITeacherDao teacherDao) {
        this.teacherDao = teacherDao;
    }

    public IHomeworkInfoService getHomeworkInfoService() {
        return homeworkInfoService;
    }

    @Resource
    public void setHomeworkInfoService(IHomeworkInfoService homeworkInfoService) {
        this.homeworkInfoService = homeworkInfoService;
    }
}
