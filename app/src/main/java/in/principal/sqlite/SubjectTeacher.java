package in.principal.sqlite;

public class SubjectTeacher {
	private int schoolId;
	private int classId;
	private int sectionId;
	private int subjectId;
	private int teacherId;
	private int subjectGroupId;
	
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
	public int getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}
	public int getTeacherId() {
		return teacherId;
	}
	public void setTeacherId(int teacherId) {
		this.teacherId = teacherId;
	}
	public int getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}
	public int getSubjectGroupId() {
		return subjectGroupId;
	}
	public void setSubjectGroupId(int subjectGroupId) {
		this.subjectGroupId = subjectGroupId;
	}

}
