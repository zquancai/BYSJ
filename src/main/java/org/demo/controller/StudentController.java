package org.demo.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import org.demo.model.*;
import org.demo.service.ICourseSelectingService;
import org.demo.service.ICourseTeachingService;
import org.demo.service.IHomeworkInfoService;
import org.demo.service.IHomeworkService;
import org.demo.tool.DateJsonValueProcessor;
import org.demo.tool.ObjectJsonValueProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

import javax.annotation.Resource;
import java.sql.Timestamp;

/**
 * Created by jzchen on 2015/3/12 0012.
 */
@Controller
@RequestMapping("/student")
public class StudentController {
    /**
     * 用于解决Jsonp跨域问题
     */
    @ControllerAdvice
    private static class JsonpAdvice extends AbstractJsonpResponseBodyAdvice {
        public JsonpAdvice() {
            super("callback");
        }
    }

    private ICourseSelectingService courseSelectingService;
    private ICourseTeachingService courseTeachingService;
    private IHomeworkInfoService homeworkInfoService;
    private IHomeworkService homeworkService;
    /**
     *
     * @param //cid 教师授课关系 courseTeaching id
     * @return 学生选课关系json分页，包含学生信息
     */
    @RequestMapping(value = "/studentList", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject studentList(Integer cid) {
        Page<HwCourseSelecting> cs = courseSelectingService.selectingCoursePage(cid);
        JsonConfig jsonConfig = new JsonConfig();
        /**过滤简单属性*/
        jsonConfig.setExcludes(new String[]{"hibernateLazyInitializer", "handler"/*"hwCourseTeaching",*//*",hwStudent"*/});
        /**过滤复杂属性 hwStudent*/
        jsonConfig.registerJsonValueProcessor(HwStudent.class,
                new ObjectJsonValueProcessor(new  String[] {"id","studentNo","name"},
                        HwStudent.class));
        /**过滤复杂属性 hwCourseTeaching*/
        jsonConfig.registerJsonValueProcessor(HwCourseTeaching.class,
                new ObjectJsonValueProcessor(new  String[] {"id"},
                        HwCourseTeaching.class));
        return JSONObject.fromObject(cs, jsonConfig);
}

    /**
     *
     * @param cid 授课关系 courseTeaching id
     * @param sid 学生 id
     * @return
     */
    @RequestMapping(value = "/homeworkInfoList", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject homeworkInfoList(Integer cid, Integer sid) {
        JsonConfig jsonConfig = new JsonConfig();
        /**过滤简单属性*/
        jsonConfig.setExcludes(new String[] {"hibernateLazyInitializer", "handler","hwCourse","hwStudent",
                "hwTeacher","hwHomeworkInfo"});
        /**过滤复杂属性 hwHomeworkInfo*/
        jsonConfig.registerJsonValueProcessor(HwHomeworkInfo.class,
                new ObjectJsonValueProcessor(new String[]{"id","title","deadline","createDate","overtime"}, HwHomework.class));
        /**自解析Timestamp属性，避免JsonObject自动解析 */
        jsonConfig.registerJsonValueProcessor(Timestamp.class,new DateJsonValueProcessor("yyyy-MM-dd"));
        //System.out.println(JSONObject.fromObject(homeworkService.homeworkPage(cid, sid),jsonConfig));
        return JSONObject.fromObject(homeworkService.homeworkPage(cid, sid),jsonConfig);
    }


    public ICourseSelectingService getCourseSelectingService() {
        return courseSelectingService;
    }

    @Resource
    public void setCourseSelectingService(ICourseSelectingService courseSelectingService) {
        this.courseSelectingService = courseSelectingService;
    }

    public ICourseTeachingService getCourseTeachingService() {
        return courseTeachingService;
    }

    @Resource
    public void setCourseTeachingService(ICourseTeachingService courseTeachingService) {
        this.courseTeachingService = courseTeachingService;
    }

    public IHomeworkInfoService getHomeworkInfoService() {
        return homeworkInfoService;
    }

    @Resource
    public void setHomeworkInfoService(IHomeworkInfoService homeworkInfoService) {
        this.homeworkInfoService = homeworkInfoService;
    }

    public IHomeworkService getHomeworkService() {
        return homeworkService;
    }

    @Resource
    public void setHomeworkService(IHomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }
}