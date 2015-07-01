package in.principal.sqlite;

public class Section {
	private int schoolId;
	private int sectionId;
	private int classId;
	private String sectionName;
	private int classTeacherId;

	public int getSectionId() {
		return sectionId;
	}
	public void setSectionId(int sectionId) {
		this.sectionId = sectionId;
	}
	public int getClassId() {
		return classId;
	}
	public void setClassId(int classId) {
		this.classId = classId;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public int getClassTeacherId() {
		return classTeacherId;
	}
	public void setClassTeacherId(int classTeacherId) {
		this.classTeacherId = classTeacherId;
	}
	public int getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}

}
