package in.principal.sqlite;

public class Clas {
	private int classId;
	private String className;
	private String classType;
	private int schoolId;
	private String subjectGroupIds;
	
	public String getSubjectGroupIds() {
		return subjectGroupIds;
	}
	public void setSubjectGroupIds(String subjectGroupIds) {
		this.subjectGroupIds = subjectGroupIds;
	}
	public int getClassId() {
		return classId;
	}
	public void setClassId(int classId) {
		this.classId = classId;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getClassType() {
		return classType;
	}
	public void setClassType(String classType) {
		this.classType = classType;
	}
	public int getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}
}
