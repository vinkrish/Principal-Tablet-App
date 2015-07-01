package in.principal.sqlite;

public class Activiti {
	private int schoolId;
	private int activityId;
	private int classId;
	private int sectionId;
	private int examId;
	private int subjectId;
	private String activityName;
	private int maximumMark;
	private double weightage;
	private int subActivity;
	private int calculation;
	private int rubrixId;
	private double activityAvg;
	private int completeEntry;
	private String uniqueKey;
	
	public String getUniqueKey() {
		return uniqueKey;
	}
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
	public int getActivityId() {
		return activityId;
	}
	public void setActivityId(int activityId) {
		this.activityId = activityId;
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
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public int getMaximumMark() {
		return maximumMark;
	}
	public void setMaximumMark(int maximumMark) {
		this.maximumMark = maximumMark;
	}
	public double getWeightage() {
		return weightage;
	}
	public void setWeightage(double weightage) {
		this.weightage = weightage;
	}
	public int getSubActivity() {
		return subActivity;
	}
	public void setSubActivity(int subActivity) {
		this.subActivity = subActivity;
	}
	/**
	 * @return the calculations
	 */
	public int getCalculation() {
		return calculation;
	}
	/**
	 * @param calculations the calculations to set
	 */
	public void setCalculation(int calculation) {
		this.calculation = calculation;
	}
	public int getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}
	public int getRubrixId() {
		return rubrixId;
	}
	public void setRubrixId(int rubrixId) {
		this.rubrixId = rubrixId;
	}
	public double getActivityAvg() {
		return activityAvg;
	}
	public void setActivityAvg(double activityAvg) {
		this.activityAvg = activityAvg;
	}
	public int getCompleteEntry() {
		return completeEntry;
	}
	public void setCompleteEntry(int completeEntry) {
		this.completeEntry = completeEntry;
	}
}
