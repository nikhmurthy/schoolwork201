package nikhilmurthy_CSCI201_Assignment2;

import java.util.Vector;

public class Corporation {
	private String name;
	private String ticker;
	private String description;
	private String startDate;
	private String exchangeCode;
	
	private Vector<Order> orders;
	private int stockBrokers;
	
	public Corporation(String name, String ticker, String description, String startDate, String exchangeCode, int stockBrokers) {
		this.name = name;
		this.ticker = ticker;
		this.description = description;
		this.startDate = startDate;
		this.exchangeCode = exchangeCode;
		this.stockBrokers = stockBrokers;
		this.orders = new Vector<Order>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExchangeCode() {
		return exchangeCode;
	}

	public void setEC(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}
	
	public int getSB() {
		return stockBrokers;
	}
	
	public void setSB(int stockBrokers) {
		this.stockBrokers = stockBrokers;
	}
	
	public Vector<Order> getOrders(){
		return this.orders;
	}
	
	public void setOrders(Vector<Order> orders) {
		this.orders = orders;
	}
}
