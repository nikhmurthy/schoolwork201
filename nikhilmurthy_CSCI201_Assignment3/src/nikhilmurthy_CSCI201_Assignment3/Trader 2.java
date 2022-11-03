package nikhilmurthy_CSCI201_Assignment3;

import java.io.Serializable;
import java.util.Vector;

public class Trader implements Serializable {
	public static final long serialVersionUID = 1;
	private int id;
	public static Vector<Order> ordersWaiting;
	@SuppressWarnings("unused")
	private double balance;
	
	// de-bugging code
	//private static int num = 0;
		
	public Trader(int id, double balance) {
		this.id = id;
		this.balance = balance;
	}
	
	public int getID() {
		return this.id;
	}
	
	public double getBalance() {
		return this.balance;
	}
	
	public void setBalance(double balance) {
		this.balance = balance;
	}

	// de-bugging code
	@Override
	public String toString() {
		return "Trader: [id=" + id + ", balance=" + balance + "]\n";
	}
}
