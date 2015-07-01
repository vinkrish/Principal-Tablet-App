package in.principal.model;

public class Circle {
	private int progressInt;
	private String sec;
	private boolean selected;
	
	public Circle(int progressInt, String sec, boolean selected){
		this.progressInt = progressInt;
		this.sec = sec;
		this.selected = selected;
	}
	
	public int getProgressInt() {
		return progressInt;
	}
	public void setProgressInt(int progressInt) {
		this.progressInt = progressInt;
	}
	public String getSec() {
		return sec;
	}
	public void setSec(String sec) {
		this.sec = sec;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
