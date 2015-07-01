package in.principal.sqlite;

public class DashObject {
	private int progressInt;
	private String outOf;
	private String str;
	public DashObject(int int1, String s1, String s2){
		progressInt = int1;
		outOf = s1;
		str = s2;
	}
	public int getProgressInt() {
		return progressInt;
	}
	public void setProgressInt(int progressInt) {
		this.progressInt = progressInt;
	}
	public String getOutOf() {
		return outOf;
	}
	public void setOutOf(String outOf) {
		this.outOf = outOf;
	}
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
}
