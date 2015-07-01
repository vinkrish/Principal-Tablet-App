package in.principal.sqlite;

public class SubActivity {
	private int schoolId;
	private int subActivityId;
	private int classId;
	private int sectionId;
	private int examId;
	private int subjectId;
	private int activityId;
	private String subActivityName;
	private int maximumMark;
	private int weightage;
	private int calculation;
	private double subActivityAvg;
	private int completeEntry;
	private String uniqueKey;
	
	public String getUniqueKey() {
		return uniqueKey;
	}
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
	public int getSubActivityId() {
		return subActivityId;
	}
	public void setSubActivityId(int subActivityId) {
		this.subActivityId = subActivityId;
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
	public String getSubActivityName() {
		return subActivityName;
	}
	public void setSubActivityName(String subActivityName) {
		this.subActivityName = subActivityName;
	}
	public int getActivityId() {
		return activityId;
	}
	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	public int getMaximumMark() {
		return maximumMark;
	}
	public void setMaximumMark(int maximumMark) {
		this.maximumMark = maximumMark;
	}
	public int getWeightage() {
		return weightage;
	}
	public void setWeightage(int weightage) {
		this.weightage = weightage;
	}
	public int getCalculation() {
		return calculation;
	}
	public void setCalculation(int calculation) {
		this.calculation = calculation;
	}
	public int getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}
	public double getSubActivityAvg() {
		return subActivityAvg;
	}
	public void setSubActivityAvg(double subActivityAvg) {
		this.subActivityAvg = subActivityAvg;
	}
	public int getCompleteEntry() {
		return completeEntry;
	}
	public void setCompleteEntry(int completeEntry) {
		this.completeEntry = completeEntry;
	}
}
