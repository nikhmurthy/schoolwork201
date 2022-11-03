package nikhilmurthy_CSCI201_Assignment3;

public class Order {
	private int delay;
	private int netSales;
	String ticker;
	private double price;
	
	public Order(int delay, int netSales, String ticker, double price) {
		this.delay = delay;
		this.netSales = netSales;
		this.ticker = ticker;
		this.price = price;
	}
	
	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getNetSales() {
		return netSales;
	}

	public void setNetSales(int netSales) {
		this.netSales = netSales;
	}
	
	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}


	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Order: [Ticker=" + ticker + ", Delay=" + delay + ", NetSales=" + netSales + ", Price=" + price + "]\n";
	}
	
	
	
}
