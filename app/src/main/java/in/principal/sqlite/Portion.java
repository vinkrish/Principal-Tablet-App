package in.principal.sqlite;

public class Portion {
	private int portionId;
	private int schoolId;
	private int classId;
	private int subjectId;
	private int  newSubjectId;
	private String portion;
	
	public int getPortionId() {
		return portionId;
	}
	public void setPortionId(int portionId) {
		this.portionId = portionId;
	}
	public int getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}
	public int getClassId() {
		return classId;
	}
	public void setClassId(int classId) {
		this.classId = classId;
	}
	public int getNewSubjectId() {
		return newSubjectId;
	}
	public void setNewSubjectId(int newSubjectId) {
		this.newSubjectId = newSubjectId;
	}
	public int getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}
	public String getPortion() {
		return portion;
	}
	public void setPortion(String portion) {
		this.portion = portion;
	}
}
