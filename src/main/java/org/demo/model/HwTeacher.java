package org.demo.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * HwTeacher entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "hw_teacher", catalog = "homework")
public class HwTeacher implements java.io.Serializable {

	// Fields

	private Integer id;
	private HwMajor hwMajor;
	private HwCollege hwCollege;
	private String teacherNo;
	private String name;
	private String sex;
	private String email;
	//private Set<HwHomeworkInfo> hwHomeworkInfos = new HashSet<HwHomeworkInfo>(0);
	//private Set<HwCourse> hwCourses = new HashSet<HwCourse>(0);
	private Set<HwHomework> hwHomeworks = new HashSet<HwHomework>(0);
	//private Set<HwCourseSelecting> hwCourseSelectings = new HashSet<HwCourseSelecting>(0);
	private Set<HwCourseTeaching> hwCourseTeachings = new HashSet<HwCourseTeaching>(0);

	// Constructors

	/** default constructor */
	public HwTeacher() {
	}

	/** minimal constructor */
	public HwTeacher(String teacherNo, String name, String sex) {
		this.teacherNo = teacherNo;
		this.name = name;
		this.sex = sex;
	}

	/** full constructor */
	public HwTeacher(HwMajor hwMajor, HwCollege hwCollege,
			String teacherNo, String name, String sex, String email,
			/*Set<HwHomeworkInfo> hwHomeworkInfos,*/ /*Set<HwCourse> hwCourses,*/
			Set<HwHomework> hwHomeworks/*, Set<HwCourseSelecting>hwCourseSelectings*/) {
		this.hwMajor = hwMajor;
		this.hwCollege = hwCollege;
		this.teacherNo = teacherNo;
		this.name = name;
		this.sex = sex;
		this.email = email;
		//this.hwHomeworkInfos = hwHomeworkInfos;
		//this.hwCourses = hwCourses;
		this.hwHomeworks = hwHomeworks;
		//this.hwCourseSelectings = hwCourseSelectings;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "major_id")
	public HwMajor getHwMajor() {
		return this.hwMajor;
	}

	public void setHwMajor(HwMajor hwMajor) {
		this.hwMajor = hwMajor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "college_id")
	public HwCollege getHwCollege() {
		return this.hwCollege;
	}

	public void setHwCollege(HwCollege hwCollege) {
		this.hwCollege = hwCollege;
	}

	@Column(name = "teacher_no", nullable = false, length = 50)
	public String getTeacherNo() {
		return this.teacherNo;
	}

	public void setTeacherNo(String teacherNo) {
		this.teacherNo = teacherNo;
	}

	@Column(name = "name", nullable = false, length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "sex", nullable = false, length = 10)
	public String getSex() {
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Column(name = "email", length = 50)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

/*	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "hwTeacher")
	public Set<HwHomeworkInfo> getHwHomeworkInfos() {
		return this.hwHomeworkInfos;
	}

	public void setHwHomeworkInfos(Set<HwHomeworkInfo> hwHomeworkInfos) {
		this.hwHomeworkInfos = hwHomeworkInfos;
	}*/

/*	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "hw_course_teacher", catalog = "homework", joinColumns = { @JoinColumn(name = "teacher_id", updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "course_id", updatable = false) })
	public Set<HwCourse> getHwCourses() {
		return this.hwCourses;
	}

	public void setHwCourses(Set<HwCourse> hwCourses) {
		this.hwCourses = hwCourses;
	}*/

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "hwTeacher")
	public Set<HwHomework> getHwHomeworks() {
		return this.hwHomeworks;
	}

	public void setHwHomeworks(Set<HwHomework> hwHomeworks) {
		this.hwHomeworks = hwHomeworks;
	}

/*	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "hwTeacher")
	public Set<HwCourseSelecting> getHwCourseSelectings() {
		return hwCourseSelectings;
	}

	public void setHwCourseSelectings(Set<HwCourseSelecting> hwCourseSelectings) {
		this.hwCourseSelectings = hwCourseSelectings;
	}*/

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "hwTeacher")
	public Set<HwCourseTeaching> getHwCourseTeachings() {
		return hwCourseTeachings;
	}

	public void setHwCourseTeachings(Set<HwCourseTeaching> hwCourseTeachings) {
		this.hwCourseTeachings = hwCourseTeachings;
	}
}