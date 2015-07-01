package in.principal.sqlite;

public class Homework {
	private long homeworkId;
	private int schoolId;
	private int classId;
	private int sectionId;
	private int teacherId;
	private String messageFrom;
	private String messageVia;
	private String subjectIDs;
	private String Homework;
	private String HomeworkDate;
	
	public long getHomeworkId() {
		return homeworkId;
	}
	public void setHomeworkId(long homeworkId) {
		this.homeworkId = homeworkId;
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
	public int getSectionId() {
		return sectionId;
	}
	public void setSectionId(int sectionId) {
		this.sectionId = sectionId;
	}
	public int getTeacherId() {
		return teacherId;
	}
	public void setTeacherId(int teacherId) {
		this.teacherId = teacherId;
	}
	public String getMessageFrom() {
		return messageFrom;
	}
	public void setMessageFrom(String messageFrom) {
		this.messageFrom = messageFrom;
	}
	public String getMessageVia() {
		return messageVia;
	}
	public void setMessageVia(String messageVia) {
		this.messageVia = messageVia;
	}
	public String getSubjectIDs() {
		return subjectIDs;
	}
	public void setSubjectIDs(String subjectIDs) {
		this.subjectIDs = subjectIDs;
	}
	public String getHomework() {
		return Homework;
	}
	public void setHomework(String homework) {
		Homework = homework;
	}
	public String getHomeworkDate() {
		return HomeworkDate;
	}
	public void setHomeworkDate(String homeworkDate) {
		HomeworkDate = homeworkDate;
	}

}
