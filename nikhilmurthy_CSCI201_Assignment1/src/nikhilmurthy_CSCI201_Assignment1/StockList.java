package nikhilmurthy_CSCI201_Assignment1;

import java.util.*;

// class generated via https://www.jsonschema2pojo.org/

public class StockList {
	private ArrayList<Company> data;
	
	public StockList() {
		data = new ArrayList<Company>();
	}

	public ArrayList<Company> getData() {
		return data;
	}

	public void setData(ArrayList<Company> data) {
		this.data = data;
	}
	
	public void insert(Company c) {
		if(c == null)
			return;
		data.add(c);
	}
	
	public void remove(int i) {
		data.remove(i);
	}
	
	public int size() {
		return data.size();
	}
	
	public Company getDataByIndex(int index) {
		return data.get(index);
	}
}