package in.principal.sqlite;

public class Temp {
    private int id;
    private String deviceId;
    private int schoolId;
    private int classId;
    private int sectionId;
    private String sectionName;
    private String className;
    private int teacherId;
    private int studentId;
    private int subjectId;
    private long examId;
    private long examId2;
    private long activityId;
    private long subActivityId;
    private long slipTestId;
    private String selectedDate;
    private String yesterDate;
    private String otherDate;
    private String syncTime;
    private int isSync;

    public long getExamId() {
        return examId;
    }

    public void setExamId(long examId) {
        this.examId = examId;
    }

    public long getExamId2() {
        return examId2;
    }

    public void setExamId2(long examId2) {
        this.examId2 = examId2;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public long getSubActivityId() {
        return subActivityId;
    }

    public void setSubActivityId(long subActivityId) {
        this.subActivityId = subActivityId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public long getSlipTestId() {
        return slipTestId;
    }

    public void setSlipTestId(long slipTestId) {
        this.slipTestId = slipTestId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(String syncTime) {
        this.syncTime = syncTime;
    }

    public int getIsSync() {
        return isSync;
    }

    public void setIsSync(int isSync) {
        this.isSync = isSync;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getYesterDate() {
        return yesterDate;
    }

    public void setYesterDate(String yesterDate) {
        this.yesterDate = yesterDate;
    }

    public String getOtherDate() {
        return otherDate;
    }

    public void setOtherDate(String otherDate) {
        this.otherDate = otherDate;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

}
