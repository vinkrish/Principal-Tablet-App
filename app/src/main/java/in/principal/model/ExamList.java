package in.principal.model;

public class ExamList {
	private int examId;
	private int subjectId;
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

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ExamList)
		{
			ExamList temp = (ExamList) obj;
			if(this.examId == temp.examId && this.subjectId== temp.subjectId)
				return true;
		}
		return false;

	}
	@Override
	public int hashCode(){
		return (int) this.examId*this.subjectId;
	}

}
