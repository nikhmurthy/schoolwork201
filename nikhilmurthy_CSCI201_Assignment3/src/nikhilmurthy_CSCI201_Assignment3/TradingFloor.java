package nikhilmurthy_CSCI201_Assignment3;

import java.io.*;

import java.util.Vector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import java.lang.Integer;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.validator.GenericValidator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.apache.commons.lang3.time.StopWatch;

import com.google.gson.Gson;

@SuppressWarnings("unused")
public class TradingFloor {
	private final static String apiKey = "cdbnliaad3ibgg4mruj0cdbnliaad3ibgg4mrujg";
	private static Vector<Order> orderList = new Vector<Order>();
	private static Vector<Trader> traderList = new Vector<Trader>();
	private Vector<TraderLink> threadList = new Vector<TraderLink>();
	
	public TradingFloor(int port) {
		ExecutorService executors = Executors.newCachedThreadPool();
		PriorityBlockingQueue<Integer> readyThreads = new PriorityBlockingQueue<Integer>(traderList.size());
		Vector<Boolean> possibilities = new Vector<Boolean>();
		
		try {
			System.out.println("Listening on port " + port + ".");
			System.out.println("Waiting for traders...");
			int remaining = traderList.size();
			ServerSocket ss = new ServerSocket(port);
			StopWatch watch = new StopWatch();
			
			while(remaining-- > 0) {
				Socket s = ss.accept();
				System.out.println("Connection from: " + s.getInetAddress());
				Trader t = traderList.remove(0);
				TraderLink tt = new TraderLink(t, s, this);
				threadList.add(tt);
				readyThreads.add(t.getID());
				possibilities.add(false);
				
				if(remaining > 0) {
					if(remaining == 1) {
						System.out.println("Waiting for " + remaining + " more trader...");
						broadcast(remaining + " more trader is needed before the service can begin.");
					}	
					else{
						System.out.println("Waiting for " + remaining + " more traders...");
						broadcast(remaining + " more traders are needed before the service can begin.");
					}
					broadcast("Waiting...");
				}
			}
			
			System.out.println("Starting service.");
			broadcast("All traders have arrived!\nStarting service.");
			TraderLink.setTime(watch);
			TraderLink.startTime();
			TraderLink.readyThreads = readyThreads;
			TraderLink.possibilities = possibilities;
			
			for(TraderLink tt : threadList)
				executors.execute(tt);
			
			executors.shutdown();
			while(!executors.isTerminated())
				Thread.yield();
			
			System.out.println("Processing complete.");
			ss.close();
		} catch (Exception e){
			System.out.println("Couldn't connect. Exception: " + e.getMessage());
		}
	}
	
	public void broadcast(String message) {
		if(message != null) {
			for(TraderLink cThread : threadList)
				cThread.sendMessage(message);
		}
	}
	
	public static double getPrice(String ticker) {
		String search = "https://finnhub.io/api/v1/quote?symbol=" + ticker + "&token=" + apiKey;
		URL url;
		HttpURLConnection connection;
		InputStream responseStream;
		String contents = "";
		APICall curr;
		try {
			url = new URL(search);
			connection = (HttpURLConnection) url.openConnection();
			responseStream = connection.getInputStream();
			
			Scanner currScan = new Scanner(responseStream);
			contents = currScan.nextLine();
			currScan.close();
			
			Gson gson = new Gson();
			curr = gson.fromJson(contents, APICall.class);
			if(curr.getC() == 0.0)
				return -1.0;
			
			return curr.getC();
		} catch (Exception e) {
			System.out.println("Call failed.");
		}
		
		return -1.0;
	}
	
	public static void readInfo() {
		Scanner input = new Scanner(System.in);
		String csvFile = "";
		
		boolean readingSchedule = true;
		HashMap<String, Double> tickerPrice = new HashMap<String, Double>();
		while(readingSchedule) {
			System.out.println("What is the path of the schedule file? ");
			csvFile = input.nextLine();
			
			File file = new File(csvFile);
			if(!file.exists()) {
				System.out.println("The file could not be found." + "\n");
				continue;
			}
			
			Scanner currScan;
			try {
				currScan = new Scanner(file);
			} catch (FileNotFoundException e) {
				System.out.println("Something went wrong. Please try again." + "\n");
				continue;
			}
			
			// referencing https://www.javatpoint.com/how-to-read-csv-file-in-java
			boolean invalid = false;
			while(currScan.hasNextLine()) {
				String raw = currScan.nextLine();
				String[] temp = raw.split(",");
				Vector<String> curr = new Vector<String>();
				curr.addAll(Arrays.asList(temp));
				
				if(curr.size() != 3) {
					invalid = true;
					break;
				}
				
				int delay = 0;
				String ticker = "";
				int netSales = 0;
				double price = 0;
				try {
					delay = Integer.parseInt(curr.get(0));
					ticker = curr.get(1);
					netSales = Integer.parseInt(curr.get(2));
				}
				catch(Exception e){
					invalid = true;
					break;
				}
				
				// TO-DO: using ticker name, get stock price from FH API. put data into hash-map to minimize API calls.
				if(!tickerPrice.containsKey(ticker)) {
					price = getPrice(ticker);
					if(price == -1) {
						invalid = true;
						break;
					}
					
					tickerPrice.put(ticker, price);
				}
				else
					price = tickerPrice.get(ticker);
				
				Order cOrder = new Order(delay, netSales, ticker, price);
				orderList.add(cOrder);
			}
			
			if(invalid) {
				System.out.println("\nThe file is formatted incorrectly. Please use another.\n");
				continue;
			}
			readingSchedule = false;
			currScan.close();
		}
		System.out.println("\nThe schedule file has been properly read.\n");
		
		boolean readingTraders = true;
		while(readingTraders) {
			System.out.println("What is the path of the traders file? ");
			csvFile = input.nextLine();
			
			File file = new File(csvFile);
			if(!file.exists()) {
				System.out.println("The file could not be found." + "\n");
				continue;
			}
			
			Scanner currScan;
			try {
				currScan = new Scanner(file);
			} catch (FileNotFoundException e) {
				System.out.println("Something went wrong. Please try again." + "\n");
				continue;
			}
			
			// referencing https://www.javatpoint.com/how-to-read-csv-file-in-java
			boolean invalid = false;
			while(currScan.hasNextLine()) {
				String raw = currScan.nextLine();
				String[] temp = raw.split(",");
				Vector<String> curr = new Vector<String>();
				curr.addAll(Arrays.asList(temp));
				
				if(curr.size() != 2) {
					invalid = true;
					break;
				}
				
				int traderID = -1;
				int cBalance = -1;
	
				try {
					traderID = Integer.parseInt(curr.get(0));
					cBalance = Integer.parseInt(curr.get(1));
				}
				catch(Exception e){
					invalid = true;
					break;
				}
				
				Trader cTrader = new Trader(traderID, cBalance);
				traderList.add(cTrader);
			}
			
			if(invalid) {
				System.out.println("\nThe file is formatted incorrectly. Please use another.\n");
				continue;
			}
			readingTraders = false;
			currScan.close();
		}
		Trader.ordersWaiting = orderList;
		System.out.println("\nThe traders file has been properly read.\n");
		input.close();
		
//		DE-BUGGING CODE 
//		System.out.println("Printing out all orders.");
//		for(Order c : Trader.ordersWaiting) {
//			System.out.print(c);
//		}
//		
//		System.out.println("\nPrinting out all traders.");
//		for(Trader c : traderList) {
//			System.out.print(c);
//		}
	}
	
	public static void main(String[] args) {
		readInfo();
		
		TradingFloor tf = new TradingFloor(3456);
	}
}
