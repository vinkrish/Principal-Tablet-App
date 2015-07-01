package in.principal.sqlite;

public class SubjectExams {
	private int schoolId;
	private int classId;
	private int examId;
	private int subjectId;
	private int maximumMark;
	private int failMark;
	private String session;
	private String timeTable;
	private String uniqueKey;
	
	public String getUniqueKey() {
		return uniqueKey;
	}
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
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
	public int getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}
	public int getMaximumMark() {
		return maximumMark;
	}
	public void setMaximumMark(int maximumMark) {
		this.maximumMark = maximumMark;
	}
	public int getFailMark() {
		return failMark;
	}
	public void setFailMark(int failMark) {
		this.failMark = failMark;
	}
	public int getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getTimeTable() {
		return timeTable;
	}
	public void setTimeTable(String timeTable) {
		this.timeTable = timeTable;
	}

}
