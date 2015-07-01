package in.principal.sqlite;

public class Teacher {
	private int schoolId;
	private int teacherId;
	private String username;
	private String password;
	private String name;
	private String mobile;
	private String tabUser;
	private String tabPass;
	
	public int getTeacherId() {
		return teacherId;
	}
	public void setTeacherId(int teacherId) {
		this.teacherId = teacherId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}
	public String getTabUser() {
		return tabUser;
	}
	public void setTabUser(String tabUser) {
		this.tabUser = tabUser;
	}
	public String getTabPass() {
		return tabPass;
	}
	public void setTabPass(String tabPass) {
		this.tabPass = tabPass;
	}

}
