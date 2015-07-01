package in.principal.sqlite;

public class Exams {
	private int schoolId;
	private int classId;
	private int examId;
	private String subjectIDs;
	private String examName;
	private int orderId;
	private int term;
	private String percentage;
	private String timeTable;
	private String portions;
	private String FileName;
	private int markUploaded;
	private int gradeSystem;
	
	public int getClassId() {
		return classId;
	}
	public void setClassId(int classId) {
		this.classId = classId;
	}
	public int getExamId() {
		return examId;
	}
	public void setExamId(int examId) {
		this.examId = examId;
	}
	public String getSubjectIDs() {
		return subjectIDs;
	}
	public void setSubjectIDs(String subjectIDs) {
		this.subjectIDs = subjectIDs;
	}
	public int getMarkUploaded() {
		return markUploaded;
	}
	public void setMarkUploaded(int markUploaded) {
		this.markUploaded = markUploaded;
	}
	public int getGradeSystem() {
		return gradeSystem;
	}
	public void setGradeSystem(int gradeSystem) {
		this.gradeSystem = gradeSystem;
	}
	public String getExamName() {
		return examName;
	}
	public void setExamName(String examName) {
		this.examName = examName;
	}
	public int getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getTerm() {
		return term;
	}
	public void setTerm(int term) {
		this.term = term;
	}
	public String getPercentage() {
		return percentage;
	}
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	public String getTimeTable() {
		return timeTable;
	}
	public void setTimeTable(String timeTable) {
		this.timeTable = timeTable;
	}
	public String getPortions() {
		return portions;
	}
	public void setPortions(String portions) {
		this.portions = portions;
	}
	public String getFileName() {
		return FileName;
	}
	public void setFileName(String fileName) {
		FileName = fileName;
	}
}
