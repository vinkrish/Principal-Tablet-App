package in.principal.sqlite;

public class StudentAttendance {

    private int schoolId;
    private int classId;
    private int sectionId;
    private long studentId;
    private String dateAttendance;
    private String typeOfLeave;

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
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

    public String getDateAttendance() {
        return dateAttendance;
    }

    public void setDateAttendance(String dateAttendance) {
        this.dateAttendance = dateAttendance;
    }

    public String getTypeOfLeave() {
        return typeOfLeave;
    }

    public void setTypeOfLeave(String typeOfLeave) {
        this.typeOfLeave = typeOfLeave;
    }

}
