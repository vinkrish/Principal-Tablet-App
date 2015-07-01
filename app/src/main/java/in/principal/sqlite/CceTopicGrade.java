package in.principal.sqlite;

public class CceTopicGrade {
	private int ccetopicgradeid;
	private int schoolId;
	private int topicId;
	private String grade;
	private int value;
	private int coScholasticId;
	private int sectionHeadingId;
	public int getCcetopicgradeid() {
		return ccetopicgradeid;
	}
	public void setCcetopicgradeid(int ccetopicgradeid) {
		this.ccetopicgradeid = ccetopicgradeid;
	}
	public int getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}
	public int getTopicId() {
		return topicId;
	}
	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getCoScholasticId() {
		return coScholasticId;
	}
	public void setCoScholasticId(int coScholasticId) {
		this.coScholasticId = coScholasticId;
	}
	public int getSectionHeadingId() {
		return sectionHeadingId;
	}
	public void setSectionHeadingId(int sectionHeadingId) {
		this.sectionHeadingId = sectionHeadingId;
	}
}
