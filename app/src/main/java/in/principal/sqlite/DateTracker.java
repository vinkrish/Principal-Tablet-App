package in.principal.sqlite;

public class DateTracker {
	private int id;
	private String firstDate;
	private String lastDate;
	private int noOfDays;
	private int selectedMonth;
	
	public String getFirstDate() {
		return firstDate;
	}
	public void setFirstDate(String firstDate) {
		this.firstDate = firstDate;
	}
	public String getLastDate() {
		return lastDate;
	}
	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}
	public int getNoOfDays() {
		return noOfDays;
	}
	public void setNoOfDays(int noOfDays) {
		this.noOfDays = noOfDays;
	}
	public int getSelectedMonth() {
		return selectedMonth;
	}
	public void setSelectedMonth(int selectedMonth) {
		this.selectedMonth = selectedMonth;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

}
