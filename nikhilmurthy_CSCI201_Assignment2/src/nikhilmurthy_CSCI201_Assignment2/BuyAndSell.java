package nikhilmurthy_CSCI201_Assignment2;

import java.io.*;
import java.util.Vector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Integer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.apache.commons.validator.GenericValidator;
import com.google.gson.Gson;

import org.apache.commons.lang3.time.StopWatch;

public class BuyAndSell {
	public static void main(String [] args) {
		Scanner input = new Scanner(System.in);
		boolean readingJson = true;
		StockList contents = null;
		String jsonFile = "";
		String fullString = "";
		HashSet<String> names = new HashSet<String>();
		HashSet<String> tickers = new HashSet<String>();
		
		while(readingJson) {
			System.out.println("What is the name of the file containing the company information? ");
			jsonFile = input.nextLine();
			
			if(!jsonFile.contains(".json")) {
				System.out.println("\nThe file " + jsonFile + " is not the correct type.\n");
				continue;
			}
			
			File file = new File(jsonFile);
			if(!file.exists()) {
				System.out.println("\nThe file " + jsonFile + " could not be found." + "\n");
				continue;
			}
			
			Scanner currScan;
			try {
				currScan = new Scanner(file);
			} catch (FileNotFoundException e) {
				System.out.println("Something went wrong. Please try again." + "\n");
				continue;
			}	
			
			boolean pStart = false;
			boolean pEnd = false;
			boolean cDesc = false;
			boolean invalid = false;
			HashMap<String, String> currCompany = new HashMap<String, String>();
			try {
				while(currScan.hasNextLine()) {
					String temp = currScan.nextLine();
					fullString += temp;
									
					if(temp.contains("}") && !cDesc && !pEnd) { // once you hit the } that ends a company, check validity
						if(currCompany.size() != 6) {
							System.out.print("Your data is missing some parameters. ");
							invalid = true;
							break;
						}
						
						currCompany.clear();
					}
					
					if(cDesc) {  // aka within company desc
						if(currCompany.size() == 6) { // if you have extra attributes it's invalid
							invalid = true;
							break;
						}
						
						// referencing https://stackoverflow.com/questions/1473155/how-to-get-data-between-quotes-in-java
						Pattern p = Pattern.compile("\"([^\"]*)\"");
						Matcher m = p.matcher(temp);
						// end reference
						
						String currAttribute;
						String cValue = "";
						int cInt = -1;
						try {
							m.find();
							currAttribute = m.group(1);
							if(!currAttribute.equals("stockBrokers")) {
								m.find();
								cValue = m.group(1);
							}
							else {
								String clean = temp.replaceAll("\\D+","");
								cInt = Integer.parseInt(clean);
							}
						}
						catch (Exception e) {
							System.out.print("Data is incorrect. ");
							invalid = true;
							break;
						}
						
						if(currAttribute.equals("name")) {
							if(!cValue.equals("") && !names.contains(cValue.toUpperCase())) {
								currCompany.put(currAttribute, cValue);
								names.add(cValue.toUpperCase());
							}
						}							
						if(currAttribute.equals("ticker")) {
							if(!cValue.equals("") && !tickers.contains(cValue)) {
								currCompany.put(currAttribute, cValue.toUpperCase());
								tickers.add(cValue.toUpperCase());
							}
						}
						if(currAttribute.equals("startDate")) {
							boolean formatted = true;
							if(!GenericValidator.isDate(cValue, "yyyy-MM-dd", true))
								formatted = false;
							if(formatted)
								currCompany.put(currAttribute,  cValue);
						}
						if(currAttribute.equals("description")) {
							if(!cValue.equals(""))
								currCompany.put(currAttribute, cValue);
						}
						if(currAttribute.equals("exchangeCode")) {
							if(cValue.equals("NYSE") || cValue.equals("NASDAQ"))
								currCompany.put(currAttribute,  cValue);
						}
						if(currAttribute.equals("stockBrokers")) {
							if(cInt > 0)
								currCompany.put(currAttribute, cValue);
						}
						
						if(currCompany.size() == 6)
							cDesc = false;
					}
					
					if(temp.contains("{")) { 
						if(pStart) // once in array, a { signifies the start of a company desc
							cDesc = true;
					}
					if(temp.contains("\"data\": [") && !cDesc) // all files should? contain this to start the array
						pStart = true;
					if(temp.contains("]") && !cDesc)
						pEnd = true;
				}
			}
			
			catch (Exception e) {
				System.out.println("\nCould not parse. The file " + jsonFile + " is not formatted properly.\n");
				currScan.close();
				continue;
			}
			
			if(invalid) { 
				System.out.println(" The file " + jsonFile + " is not formatted properly.\n");
				continue;
			}
			
			Gson gson = new Gson();
			try {
				contents = gson.fromJson(fullString, StockList.class);
				if(contents == null) {
					System.out.println(contents == null);
					contents = new StockList();
				}
					
			}
			catch (Exception eof) {
				System.out.println("Could not parse. The file " + jsonFile + " is not formatted properly.\n");
				continue;
			}
			readingJson = false;
			currScan.close();
			System.out.println("");
		}
			
		boolean readingCSV = true;
		String csvFile = "";
		
		while(readingCSV) {
			System.out.println("What is the name of the file containing the schedule information? ");
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
				
				if(curr.size() != 4) {
					invalid = true;
					break;
				}
				int delay = 0;
				String ticker = "";
				int netSales = 0;
				int price = 0;
				try {
					delay = Integer.parseInt(curr.get(0));
					ticker = curr.get(1);
					netSales = Integer.parseInt(curr.get(2));
					price = Integer.parseInt(curr.get(3));
				}
				catch(Exception e){
					invalid = true;
					break;
				}
				if(contents.getCompany(ticker.toUpperCase()) == null) {
					invalid = true;
					break;
				}
				
				Order cOrder = new Order(delay, netSales, price);
				if(contents.getCompany(ticker).getOrders() == null) {
					Vector<Order> cList = new Vector<Order>(Arrays.asList(cOrder));
					contents.getCompany(ticker).setOrders(cList);
				}
				else
					contents.getCompany(ticker).getOrders().add(cOrder);

			}
			
			if(invalid) {
				System.out.println("\nThe file is formatted incorrectly. Please use another.\n");
				continue;
			}
			readingCSV = false;
			currScan.close();
		}
		
		boolean balancing = true;
		int balance = 0;
		while(balancing) {
			System.out.println("\nWhat is the initial balance?");
			try {
				balance = input.nextInt();
			}
			catch (Exception e){
				System.out.print("\nPlease enter a valid balance.\n");
				@SuppressWarnings("unused")
				String dummy = input.next();
				continue;
			}
			balancing = false;
		}
		input.close();
		System.out.println("");
		
		StockBroker.setBalance(balance);
		HashMap<String, Semaphore> pastCompanies = new HashMap<String, Semaphore>();
		ExecutorService executors = Executors.newCachedThreadPool();
		StopWatch watch = new StopWatch();
		StockBroker.setTime(watch);
		watch.start();
		for(int i = 0; i < contents.size(); i++) {
			Corporation c = contents.getDataByIndex(i);
			if(c.getOrders() != null) {
				Semaphore cSem = null;
				if(pastCompanies.containsKey(c.getTicker())) 
					cSem = pastCompanies.get(c.getTicker());
				else {
					cSem = new Semaphore(Math.min(c.getSB(),c.getOrders().size()));
					pastCompanies.put(c.getTicker(), cSem);
				}
				
				for(int j = 0; j < c.getSB(); j++) {
					StockBroker curr = new StockBroker(c.getTicker(), c.getOrders(), cSem);
					executors.execute(curr);
				}
			}
		}
		executors.shutdown();
		while(!executors.isTerminated()) {
			Thread.yield();
		}
		System.out.println("All trades completed!");
		
	}
}
