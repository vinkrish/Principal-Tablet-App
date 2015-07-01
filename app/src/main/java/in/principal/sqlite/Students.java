package in.principal.sqlite;

import android.graphics.Bitmap;

public class Students {
	private int schoolId;
	private String subjectIds;
	private String admissionNo;
	private int studentId;
	private int classId;
	private int sectionId;
	private int rollNoInClass;
	private String name;
	private Bitmap attMarker;
	private String score;
	
	public Students(){}
	
	public Students(int rollno, String name, Bitmap attMarker){
		super();
		this.name = name;
		this.rollNoInClass = rollno;
		this.setAttMarker(attMarker);
	}
	
	public Students(int rollno, String name, String score){
		super();
		this.name = name;
		this.rollNoInClass = rollno;
		this.score = score;
	}
	
	public Students(int rollno, String name, String score, Bitmap attMarker){
		super();
		this.name = name;
		this.rollNoInClass = rollno;
		this.score = score;
		this.setAttMarker(attMarker);
	}
	
	public int getStudentId() {
		return studentId;
	}
	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}
	
	public String getSubjectIds() {
		return subjectIds;
	}

	public void setSubjectIds(String subjectIds) {
		this.subjectIds = subjectIds;
	}

	public int getClassId() {
		return classId;
	}
	public void setClassId(int classId) {
		this.classId = classId;
	}
	public int getSectionId() {
		return sectionId;
	}
	public void setSectionId(int sectionId) {
		this.sectionId = sectionId;
	}
	public int getRollNoInClass() {
		return rollNoInClass;
	}
	public void setRollNoInClass(int rollNoInClass) {
		this.rollNoInClass = rollNoInClass;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Bitmap getAttMarker() {
		return attMarker;
	}

	public void setAttMarker(Bitmap attMarker) {
		this.attMarker = attMarker;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public int getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}

	public String getAdmissionNo() {
		return admissionNo;
	}

	public void setAdmissionNo(String admissionNo) {
		this.admissionNo = admissionNo;
	}

}
