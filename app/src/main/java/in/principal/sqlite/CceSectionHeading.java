package in.principal.sqlite;

public class CceSectionHeading {
    private int sectionHeadingId;
    private int schoolId;
    private int coScholasticId;
    private String sectionName;

    public int getSectionHeadingId() {
        return sectionHeadingId;
    }

    public void setSectionHeadingId(int sectionHeadingId) {
        this.sectionHeadingId = sectionHeadingId;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public int getCoScholasticId() {
        return coScholasticId;
    }

    public void setCoScholasticId(int coScholasticId) {
        this.coScholasticId = coScholasticId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}
