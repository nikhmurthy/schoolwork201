package nikhilmurthy_CSCI201_Assignment2;

import java.util.Vector;
import java.util.concurrent.locks.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

public class StockBroker extends Thread {
	private String id;
	private Vector<Order> ordersWaiting;
	private static int balance;
	private static Lock lock = new ReentrantLock(true);
	private Semaphore semaphore;
	private static StopWatch watch;
	
	public StockBroker(String id, Vector<Order> ordersWaiting, Semaphore semaphore) {
		this.id = id;
		this.ordersWaiting = ordersWaiting;
		this.semaphore = semaphore;
	}
	
	public static void setBalance(int balance) {
		StockBroker.balance = balance;
	}
	
	public static void setTime(StopWatch watch) {
		StockBroker.watch = watch;
	}
	
	public void run() {
		try {
			semaphore.acquire();
			while(ordersWaiting != null && !ordersWaiting.isEmpty()) {
				Order order = null;
				
				synchronized(this) {
					order = ordersWaiting.remove(0);
				}
				
				while(watch.getTime(TimeUnit.SECONDS) < (long)order.getDelay()) {}
				
				if(order.getNetSales() > 0) {
					if(order.getNetSales() == 1) 
						System.out.println(Util.printMessage(watch) + "Starting purchase of " + order.getNetSales() + " stock of " + id);
					else
						System.out.println(Util.printMessage(watch) + "Starting purchase of " + order.getNetSales() + " stocks of " + id);
					try {
						Thread.sleep(2000);
					}
					catch (InterruptedException ie) {
						System.out.println("Interrupted exception occurred while waiting to buy. " + ie.getMessage());
					}
					
					lock.lock();
					try {
						if(balance < order.getNetSales()*order.getPrice()) {
							if(order.getNetSales() == 1) 
								System.out.println("Transaction failed due to insfuficient balance. Unsuccessful purchase of " + order.getNetSales() + " stock of " + id);
							else
								System.out.println("Transaction failed due to insfuficient balance. Unsuccessful purchase of " + order.getNetSales() + " stocks of " + id);
						}
							
						else {
							if(order.getNetSales() == 1) 
								System.out.println(Util.printMessage(watch) + "Finished purchase of " + order.getNetSales() + " stock of " + id);
							else
								System.out.println(Util.printMessage(watch) + "Finished purchase of " + order.getNetSales() + " stocks of " + id);
							balance -= order.getNetSales()*order.getPrice();
							System.out.println("Current balance after trade: " + balance);
						}
					}
					finally {
						lock.unlock();
					}
				}
				else {
					if(order.getNetSales() == -1) 
						System.out.println(Util.printMessage(watch) + "Started sale of " + -1*order.getNetSales() + " stock of " + id);
					else
						System.out.println(Util.printMessage(watch) + "Started sale of " + -1*order.getNetSales() + " stocks of " + id);
					try {
						Thread.sleep(3000);
					}
					catch (InterruptedException ie) {
						System.out.println("Interrupted exception occurred while waiting to sell. " + ie.getMessage());
					}
					
					lock.lock();
					try {
						if(order.getNetSales() == -1) 
							System.out.println(Util.printMessage(watch) + "Finished sale of " + -1*order.getNetSales() + " stock of " + id);
						else
							System.out.println(Util.printMessage(watch) + "Finished sale of " + -1*order.getNetSales() + " stocks of " + id);
						balance += -1*order.getNetSales()*order.getPrice();
						System.out.println("Current balance after trade: " + balance);
					}
					finally {
						lock.unlock();
					}
				}
			}
		}
		catch (InterruptedException ie) {
			System.out.println("Interrupted while waiting to run.");
		}
		finally {
			semaphore.release();
		}
	}
}
