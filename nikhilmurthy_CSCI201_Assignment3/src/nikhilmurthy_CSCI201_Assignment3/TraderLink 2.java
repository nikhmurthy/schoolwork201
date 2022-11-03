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
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

@SuppressWarnings("unused")
public class TraderLink {
	private Trader assignedTrader;
	private ObjectOutputStream oos;
	private TradingFloor tf;
		
	public TraderLink(Trader assignedTrader, Socket s, TradingFloor tf) {
		try {
			this.assignedTrader = assignedTrader;
			this.oos = new ObjectOutputStream(s.getOutputStream());
		} catch (Exception e){
			System.out.println("Error in TraderThread constructor: " + e.getMessage());
		}
	}
	
	public void sendTrader() {
		try {
			sendMessage("sending trader");
			oos.writeObject(assignedTrader);
			oos.writeObject(Trader.ordersWaiting);
			oos.flush();
		}
		catch (Exception e) {
			System.out.println("Error in TraderThread sendData: " + e.getMessage());
		}
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