package in.principal.sqlite;

public class StAvg {
	private int classId;
	private int sectionId;
	private int subjectId;
	private int slipTestAvg;
	
	public int getSectionId() {
		return sectionId;
	}
	public void setSectionId(int sectionId) {
		this.sectionId = sectionId;
	}
	public int getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}
	public int getSlipTestAvg() {
		return slipTestAvg;
	}
	public void setSlipTestAvg(int slipTestAvg) {
		this.slipTestAvg = slipTestAvg;
	}
	public int getClassId() {
		return classId;
	}
	public void setClassId(int classId) {
		this.classId = classId;
	}

}
