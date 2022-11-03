package nikhilmurthy_CSCI201_Assignment3;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.locks.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import java.lang.Math;
import java.text.SimpleDateFormat;  
import java.util.Date; 

@SuppressWarnings("unused")
public class TraderLink extends Thread{
	private Trader assignedTrader;
	private ObjectOutputStream oos;
	private TradingFloor tf;
	private double profit = 0;
	
	private static StopWatch watch;
	private static Semaphore sem = new Semaphore(1);
	public static PriorityBlockingQueue<Integer> readyThreads;
	public static Vector<Boolean> possibilities;
		
	public TraderLink(Trader assignedTrader, Socket s, TradingFloor tf) {
		try {
			this.assignedTrader = assignedTrader;
			this.oos = new ObjectOutputStream(s.getOutputStream());
		} catch (Exception e){
			System.out.println("Error in TraderThread constructor: " + e.getMessage());
		}
	}
	
	public static void setTime(StopWatch watch) {
		TraderLink.watch = watch;
	}
	
	public static void startTime() {
		watch.start();
	}
	
	private boolean impossible() {
		for(Order c : Trader.ordersWaiting) {
			if(c.getNetSales() < 0)
				return false;
			else {
				if(assignedTrader.getBalance() < c.getNetSales()*c.getPrice())
					continue;
				return false;
			}
		}
		if(readyThreads.contains(assignedTrader.getID()))
			readyThreads.remove(assignedTrader.getID());
		
		possibilities.set(assignedTrader.getID()-1, true);
		return true;
	}
	
	private boolean allDone() {
		if(Trader.ordersWaiting.isEmpty())
			return true;
		for(boolean c : possibilities) {
			if(!c)
				return false;
		}
		return true;
	}
	
	private boolean earliestReady() {
		if(assignedTrader.getID() != readyThreads.peek())
			return false;
		
		return true;
	}
	
	public Vector<Order> assignTrades() {
		int index = 0;
		double cBal = assignedTrader.getBalance();
		Vector<Order> toDo = new Vector<Order>();
		
		// thread busy waiting for the next to come into time
		int count = 0;
		while(!Trader.ordersWaiting.isEmpty() && watch.getTime(TimeUnit.SECONDS)+1 < Trader.ordersWaiting.elementAt(0).getDelay()) {
			count++;
		}
		
		if(!earliestReady())
			return null;
				
		long cTime = watch.getTime(TimeUnit.SECONDS)+1;
		while(index < Trader.ordersWaiting.size() && Trader.ordersWaiting.elementAt(index).getDelay() < cTime) {
			Order c = Trader.ordersWaiting.elementAt(index);
			if(c.getNetSales() < 0) {
				// System.out.print("Trader " + assignedTrader.getID() + " just got: " + Trader.ordersWaiting.elementAt(index));
				sendMessage(Util.printMessage(watch) + "Assigned sale of " + -1*c.getNetSales() + " stock(s) of " + c.getTicker() 
				+ ". Total gain estimate: " + Util.round(c.getPrice(),2) + " * " + -1*c.getNetSales() + " = " + Util.round(-1*c.getPrice()*c.getNetSales(),2));
				toDo.add(Trader.ordersWaiting.remove(index));
			}
			else if (cBal >= c.getNetSales() * c.getPrice()){
				//System.out.println("Trader " + assignedTrader.getID() + " just got: " + Trader.ordersWaiting.elementAt(index));
				cBal -= (c.getPrice() * c.getNetSales());
				sendMessage(Util.printMessage(watch) + "Assigned purchase of " + c.getNetSales() + " stock(s) of " + c.getTicker() 
				+ ". Total cost estimate: " + Util.round(c.getPrice(),2) + " * " + c.getNetSales() + " = " + Util.round(c.getPrice()*c.getNetSales(),2));
				toDo.add(Trader.ordersWaiting.remove(index));
			}
			else {
				index++;
				//System.out.println("Trader " + assignedTrader.getID() + " couldn't do " + c);
			}
				
		}
		
		readyThreads.remove(assignedTrader.getID());
		//System.out.println("Trader " + assignedTrader.getID() + " done assigning. Number of ready traders is now " + readyThreads.size());
			
		return toDo;
	}
	
	public void executeTrades(Vector<Order> toDo) {
		for(Order c : toDo) {
			if(c.getNetSales() > 0) {
				sendMessage(Util.printMessage(watch) + "Starting purchase of " + c.getNetSales() + " stock(s) of " + c.getTicker() 
				+ ". Total cost: " + Util.round(c.getPrice(),2) + " * " + c.getNetSales() + " = " + Util.round(c.getPrice()*c.getNetSales(),2));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println("TraderLink interrupted while sleeping in executeTrades(): " + e.getStackTrace());
				}
				sendMessage(Util.printMessage(watch) + "Finished purchase of " + c.getNetSales() + " stock(s) of " + c.getTicker() + ".");
				assignedTrader.setBalance(Util.round(assignedTrader.getBalance() - (c.getNetSales()*c.getPrice()),2));
			}
			else {
				sendMessage(Util.printMessage(watch) + "Starting sale of " + -1*c.getNetSales() + " stock(s) of " + c.getTicker() 
				+ ". Total gain: " + Util.round(c.getPrice(),2) + " * " + -1*c.getNetSales() + " = " + Util.round(-1*c.getPrice()*c.getNetSales(),2));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println("TraderLink interrupted while sleeping in executeTrades(): " + e.getStackTrace());
				}
				sendMessage(Util.printMessage(watch) + "Finished sale of " + -1*c.getNetSales() + " stock(s) of " + c.getTicker() + ".");
				profit -= c.getNetSales()*c.getPrice();
			}
			
		}
	}
	
	private String getIncompleteTrades(Trader t) {
		String ans = "";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
		Date date = new Date();
		for(Order c : Trader.ordersWaiting) {
			ans += "(" + c.getDelay() + ", " + c.getTicker() + ", " + c.getNetSales() + ", " + formatter.format(date) + ") ";
		}
		return ans;
	}
	
	public void run() {
		try {
			Vector<Order> toDo;
			while(!Trader.ordersWaiting.isEmpty()) {
//				if(!earliestReady())
//					continue;
				
				sem.acquire();
				//System.out.println("Trader " + assignedTrader.getID() + " acquired lock.");
				try {
					if(impossible()) {
						sem.release();
						break;
					}
					
					toDo = assignTrades();
				}
				finally {
					sem.release();
				}
				
				if(toDo != null) {
					executeTrades(toDo);
				}
				
				if(!readyThreads.contains(assignedTrader.getID())) 
					readyThreads.add(assignedTrader.getID());
			}
		} catch (Exception e) {
			System.out.println("Error in TraderLink run() caused by Trader " + assignedTrader.getID() + ": " + e.getMessage());
		}
		
		while(!allDone());
		
		if(Trader.ordersWaiting.isEmpty()) {
			sendMessage(Util.printMessage(watch) + "Incomplete Trades: NONE");
		}
		else 
			sendMessage(Util.printMessage(watch) + "Incomplete Trades: " + getIncompleteTrades(assignedTrader));
		sendMessage("Total Profit Earned: $" + Util.round(profit, 2) + "\n");
		sendMessage("Processing complete.");
	}
	
	public void sendMessage(String message) {
		try {
			oos.writeObject(message);
			oos.flush();
		}
		catch (Exception e) {
			System.out.println("Error in TraderThread sendMessage: " + e.getMessage());
		}
	}
}