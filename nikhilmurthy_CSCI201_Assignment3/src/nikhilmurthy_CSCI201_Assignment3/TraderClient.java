package nikhilmurthy_CSCI201_Assignment3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.time.StopWatch;
import nikhilmurthy_CSCI201_Assignment3.Util;

@SuppressWarnings("unused")
public class TraderClient{
	private ObjectInputStream ois;
	private Socket s;

	public TraderClient(String hostName, int port) {
		try {
			s = new Socket(hostName, port);
			ois = new ObjectInputStream(s.getInputStream());
			receiveData();
		} catch (Exception e) {
			System.out.println("Error in TraderClient constructor: " + e.getMessage());
		}
	}
	
	@SuppressWarnings("unused")
	public void receiveData() {
		try {
			while(true) {
				String message = (String)ois.readObject();
				if(message != null) {
					if(message.equals("Processing complete.")) {
						System.out.print(message);
						return;
					} 
					System.out.println(message);
				}
			}
		} catch (Exception e) {
			System.out.println("Error in TraderClient receiveData: " + e.getMessage());
			return;
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
	}
}
