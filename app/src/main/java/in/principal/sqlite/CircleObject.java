package in.principal.sqlite;

public class CircleObject {
	private int progressInt;
	private String clas;
	
	public CircleObject(int progressInt, String clas){
		this.progressInt = progressInt;
		this.clas = clas;
	}
	
	public String getClas() {
		return clas;
	}

	public void setClas(String clas) {
		this.clas = clas;
	}

	public int getProgressInt() {
		return progressInt;
	}
	public void setProgressInt(int progressInt) {
		this.progressInt = progressInt;
	}

}
