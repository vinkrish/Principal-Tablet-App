package in.principal.sqlite;

public class SlipTestt {
	private String idx;
	private long slipTestId;
	private int schoolId;
	private int classId;
	private int sectionId;
	private String slipTestName;
	private int subjectId;
	private int newSubjectId;
	private String portion;
	private String extraPortion;
	private int maximumMark;
	private double averageMark;
	private int markEntered;
	private String testDate;
	private String submissionDate;
	private int progress;	
	private String portionName;
	private int isActivity;
	private int Grade;
	private int Count;
	private int employeeId;
	private double Weightage;
	
	public SlipTestt(){	}
	public SlipTestt(String idx, String date, String portionName, int progress){
		this.idx = idx;
		this.testDate = date;
		this.portionName = portionName;
		this.progress = progress;
	}
	
	public String getSubmissionDate() {
		return submissionDate;
	}
	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
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
	public String getSlipTestName() {
		return slipTestName;
	}
	public void setSlipTestName(String slipTestName) {
		this.slipTestName = slipTestName;
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
	public int getMaximumMark() {
		return maximumMark;
	}
	public void setMaximumMark(int maximumMark) {
		this.maximumMark = maximumMark;
	}
	public double getAverageMark() {
		return averageMark;
	}
	public void setAverageMark(double averageMark) {
		this.averageMark = averageMark;
	}
	public int getMarkEntered() {
		return markEntered;
	}
	public void setMarkEntered(int markEntered) {
		this.markEntered = markEntered;
	}
	public long getSlipTestId() {
		return slipTestId;
	}
	public void setSlipTestId(long slipTestId) {
		this.slipTestId = slipTestId;
	}
	public String getTestDate() {
		return testDate;
	}
	public void setTestDate(String testDate) {
		this.testDate = testDate;
	}
	public String getPortionName() {
		return portionName;
	}
	public void setPortionName(String portionName) {
		this.portionName = portionName;
	}
	public String getExtraPortion() {
		return extraPortion;
	}
	public void setExtraPortion(String extraPortion) {
		this.extraPortion = extraPortion;
	}
	public String getIdx() {
		return idx;
	}
	public void setIdx(String idx) {
		this.idx = idx;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public int getIsActivity() {
		return isActivity;
	}
	public void setIsActivity(int isActivity) {
		this.isActivity = isActivity;
	}
	public int getGrade() {
		return Grade;
	}
	public void setGrade(int grade) {
		Grade = grade;
	}
	public int getCount() {
		return Count;
	}
	public void setCount(int count) {
		Count = count;
	}
	public int getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}
	public double getWeightage() {
		return Weightage;
	}
	public void setWeightage(double weightage) {
		Weightage = weightage;
	}
	public int getNewSubjectId() {
		return newSubjectId;
	}
	public void setNewSubjectId(int newSubjectId) {
		this.newSubjectId = newSubjectId;
	}

}
