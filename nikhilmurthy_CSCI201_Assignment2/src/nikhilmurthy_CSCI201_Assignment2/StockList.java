package nikhilmurthy_CSCI201_Assignment2;

import java.util.ArrayList;

public class StockList {
	private ArrayList<Corporation> data;
	
	public StockList() {
		data = new ArrayList<Corporation>();
	}

	public ArrayList<Corporation> getData() {
		return data;
	}

	public void setData(ArrayList<Corporation> data) {
		this.data = data;
	}
	
	public void insert(Corporation c) {
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
	
	public Corporation getDataByIndex(int index) {
		return data.get(index);
	}
	
	public Corporation getCompany(String ticker) {
		for(Corporation c : data) {
			if(c.getTicker().equals(ticker))
				return c;
		}
		return null;
	}
}