package nikhilmurthy_CSCI201_Assignment3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.time.StopWatch;
import nikhilmurthy_CSCI201_Assignment3.Util;

@SuppressWarnings("unused")
public class TraderClient extends Thread {
	private ObjectInputStream ois;
	private Trader t;
	private Socket s;
	private static Lock lock = new ReentrantLock(true);
	private static StopWatch watch = new StopWatch();
	public static Vector<Order> ordersWaiting;
	public static int index = 0;
	private boolean bool;
	
	public TraderClient(String hostName, int port) {
		try {
			s = new Socket(hostName, port);
			ois = new ObjectInputStream(s.getInputStream());
			
			receiveData();
			if(watch.isStopped())
				watch.start();
						
			run();
		} catch (Exception e) {
			System.out.println("Error in TraderClient constructor: " + e.getMessage());
		}
	}
	
	private void setOrdersWaiting(Vector<Order> ordersWaiting) {
		TraderClient.ordersWaiting = ordersWaiting;
	}
	
	@SuppressWarnings("unused")
	public void receiveData() {
		try {
			boolean foundTrader = false;
			while(true) {
				if(!foundTrader) {
					String message = (String)ois.readObject();
					
					if(message.equals("sending trader"))
						foundTrader = true;
					else
						System.out.println(message);
				}
				else {
					try {
						t = (Trader)ois.readObject();
						@SuppressWarnings("unchecked")
						Vector<Order> orders = (Vector<Order>)ois.readObject();

						
						if(ordersWaiting==null) {
							setOrdersWaiting(orders);
							bool = false;
						}
						break;
					} catch (Exception e){
						System.out.println("Error in reading trader object: " + e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error in TraderClient receiveData: " + e.getMessage());
		}
	}
	
	public void run() {
		System.out.println("Trader " + t.getID());
		while(true) {
			Vector<Order> toDo = new Vector<Order>();
			// basically, while A. there are orders left, B. time > delay, and C. order is within balance
			// System.out.println("Bool: " + bool);
			lock.lock();
			try {
				double cBal = t.getBalance();
				while(index < ordersWaiting.size() && watch.getTime(TimeUnit.SECONDS) > (long)(ordersWaiting.elementAt(index).getDelay()) 
				&& cBal-(ordersWaiting.elementAt(index).getNetSales()*ordersWaiting.elementAt(index).getPrice()) >= 0){
					//System.out.println("Current balance: " + cBal);
					Order cOrder = ordersWaiting.elementAt(index++);
					//System.out.println("Removed " + cOrder);
					//System.out.println("First element in vector: " + ordersWaiting.get(0));
					toDo.add(cOrder);
					cBal -= cOrder.getPrice() * cOrder.getNetSales();
					if(cOrder.getNetSales() > 0) {
						System.out.println(Util.printMessage(watch) + "Assigned purchase of " + cOrder.getNetSales() + " stock(s) of " + cOrder.getTicker() 
						+ ". Total cost estimate: " + cOrder.getPrice() + " * " + cOrder.getNetSales() + " = " + cOrder.getPrice()*cOrder.getNetSales());
					}
					else {
						System.out.println(Util.printMessage(watch) + "Assigned sale of " + -1*cOrder.getNetSales() + " stock(s) of " + cOrder.getTicker() 
						+ ". Total gain estimate: " + cOrder.getPrice() + " * " + -1*cOrder.getNetSales() + " = " + -1*cOrder.getPrice()*cOrder.getNetSales());
					}
					//System.out.println(ordersWaiting.size() + " orders remaining.");
					
				}
			}
			finally {
				lock.unlock();
			}
			
			while(!toDo.isEmpty()) {
				Order c = toDo.remove(0);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println("ThreadClient run() interrupted while sleeping: " + e.getStackTrace());
				}
				double cBal = t.getBalance();
				t.setBalance(Util.round(cBal - (c.getNetSales()*c.getPrice()),2));
				
				if(c.getNetSales() > 0)
					System.out.println(Util.printMessage(watch) + "Finished purchase of " + c.getNetSales() + " stock(s) of " + c.getTicker() + ".");
				else
					System.out.println(Util.printMessage(watch) + "Finished sale of " + -1*c.getNetSales() + " stock(s) of " + c.getTicker() + ".");
			}
		}
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Scanner currScan = new Scanner(System.in);
		String hostName = "";
		int port = -1;
		
		System.out.println("Welcome to SalStocks v2.0!");
		
		System.out.println("Enter the server hostname:");
		boolean naming = true;
		while(naming) {
			try {
				hostName = currScan.next();
			} catch (Exception e) {
				System.out.println("Please enter a valid name.");
				String dummy = currScan.next();
				continue;
			}
			naming = false;
		}
				
		System.out.println("Enter the server port:");
		boolean porting = true;
		while(porting) {
			try {
				port = currScan.nextInt();
			} catch (Exception e) {
				System.out.println("Please enter a valid port number.");
				String dummy = currScan.next();
				continue;
			}
			porting = false;
		}

		currScan.close();
		TraderClient tc = new TraderClient(hostName, port);

		try {
			tc.s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
