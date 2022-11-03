package nikhilmurthy_CSCI201_Assignment2;

public class Order {
	private int delay;
	private int netSales;
	private int price;
	
	public Order(int delay, int netSales, int price) {
		this.delay = delay;
		this.netSales = netSales;
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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	
	
	
	
}
