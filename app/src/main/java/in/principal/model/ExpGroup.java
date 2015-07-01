package in.principal.model;

import java.util.ArrayList;

public class ExpGroup {
	private int image1;
	private String text1;
	private ArrayList<ExpChild> items;
	
	public int getImage1() {
		return image1;
	}
	public void setImage1(int image1) {
		this.image1 = image1;
	}
	public String getText1() {
		return text1;
	}
	public void setText1(String text1) {
		this.text1 = text1;
	}
	public ArrayList<ExpChild> getItems() {
		return items;
	}
	public void setItems(ArrayList<ExpChild> items) {
		this.items = items;
	}

}
