package nikhilmurthy_CSCI201_Assignment1;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.*;

import org.apache.commons.text.WordUtils;
import org.apache.commons.validator.GenericValidator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StockReader {
	public static void main(String [] args) {
		Scanner input = new Scanner(System.in);
		boolean reading = true;
		boolean running = false;
		StockList contents = null;
		String filename = "";
		String fullString = "";
		HashSet<String> names = new HashSet<String>();
		HashSet<String> tickers = new HashSet<String>();;
		
		while(reading) {
			System.out.print("What is the name of the company file? ");
			filename = input.nextLine();
			
			File file = new File(filename);
			if(!file.exists()) {
				System.out.println("The file " + filename + " could not be found." + "\n");
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
						if(currCompany.size() != 5) {
							System.out.print("Your data is missing some parameters. ");
							invalid = true;
							break;
						}
						
						currCompany.clear();
					}
					
					if(cDesc) {  // aka within company desc
						if(currCompany.size() == 5) { // if you have extra attributes it's invalid
							invalid = true;
							break;
						}
						
						// referencing https://stackoverflow.com/questions/1473155/how-to-get-data-between-quotes-in-java
						Pattern p = Pattern.compile("\"([^\"]*)\"");
						Matcher m = p.matcher(temp);
						// end reference
						
						String currAttribute;
						String cValue;
						try {
							m.find();
							currAttribute = m.group(1);
							m.find();
							cValue = m.group(1);
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
						
						if(currCompany.size() == 5)
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
				System.out.println("Could not parse. The file " + filename + " is not formatted properly.\n");
				continue;
			}
			
			if(invalid) { 
				System.out.println("The file " + filename + " is not formatted properly.\n");
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
				System.out.println("Could not parse. The file " + filename + " is not formatted properly.\n");
				continue;
			}
			reading = false;
			running = true;
			currScan.close();
		}
		
		boolean didSomething = true;
		boolean edited = false;
		while(running) {
			if(didSomething) {
				System.out.println("\n		1) Display all public companies");
				System.out.println("		2) Search for a stock (by ticker)");
				System.out.println("		3) Search for all stocks on an exchange");
				System.out.println("		4) Add a new company/stock");
				System.out.println("		5) Remove a company");
				System.out.println("		6) Sort companies");
				System.out.println("		7) Exit");
				didSomething = false;
			}
			Scanner currScan = new Scanner(System.in);
			System.out.print("What would you like to do? ");
			String action = currScan.nextLine();
			System.out.println();
			
			if(action.length() > 1 || action.length() == 0 || Integer.parseInt(action) < 1 || Integer.parseInt(action) > 7) {
				System.out.println("That is not a valid option.\n");
				continue;
			}
			
			int choice = Integer.parseInt(action);
			
			
			if(choice == 1) {
				int cSize = contents.size();
				if(cSize == 0) {
					System.out.print("There are no companies to display.\n");
					didSomething = true;
					continue;
				}

				for(int i = 0; i < contents.size(); i++) {
					Company curr = contents.getDataByIndex(i);
					System.out.println(curr.getName() + ", symbol " + curr.getTicker() + ", started on " + curr.getStartDate() + ", listed on " + curr.getExchangeCode() + ",");
					String prettyDesc = WordUtils.wrap(contents.getDataByIndex(i).getDescription(), 60, "\n\t", true);
					System.out.println("	" + prettyDesc);
				}
				didSomething = true;
			}
			else if(choice == 2) {
				int cSize = contents.size();
				if(cSize == 0) {
					System.out.print("There are no companies to search for.\n");
					didSomething = true;
					continue;
				}
				
				boolean searching = true;				
				while(searching) {
					System.out.print("What is the ticker of the company you would like to search for? ");
					String toSearch = currScan.nextLine();
					String copy = toSearch.toUpperCase();
					Company curr = null;
					
					for(int i = 0; i < contents.size(); i++) {
						curr = contents.getDataByIndex(i);
						if(copy.equals(curr.getTicker().toUpperCase())) {
							break;
						}
						else
							curr = null;
					}
					
					if(curr == null) 
						System.out.println("\n" + toSearch + " could not be found.\n");
					else {
						System.out.println("\n" + curr.getName() + ", symbol " + curr.getTicker() + ", started on " + curr.getStartDate() + ", listed on " + curr.getExchangeCode());	
						searching = false;
					}	
				}
				didSomething = true;
			}
			else if(choice == 3) {
				boolean searching = true;
				while(searching) {
					System.out.print("What stock exchange would you like to search for? ");
					String toSearch = currScan.nextLine();
					String copy = toSearch.toUpperCase();
					StockList inEC = new StockList();
					if(!copy.equals("NYSE") && !copy.equals("NASDAQ")) {
						System.out.println("\nNo exchange named " + toSearch + " found.\n");
						continue;
					}
					
					for(int i = 0; i < contents.size(); i++) {
						Company curr = contents.getDataByIndex(i);
						if(copy.equals(curr.getExchangeCode().toUpperCase()))
							inEC.insert(curr);
					}
					
					if(inEC.size() == 0) {
						System.out.println("\nNo companies found on the " + copy + " exchange.");
						searching = false;
					}
					else if(inEC.size() == 1) {
						Company curr = inEC.getDataByIndex(0);
						System.out.println("\n" + curr.getTicker() + " found on the " + copy + " exchange.");
						searching = false;
					}
					else if(inEC.size() == 2) {
						Company c1 = inEC.getDataByIndex(0);
						Company c2 = inEC.getDataByIndex(1);
						
						System.out.println("\n" + c1.getTicker() + " and " + c2.getTicker() + " found on the"  + copy + " exchange.");
						searching = false;
					}
					else {
						System.out.print("\n" + inEC.getDataByIndex(0).getTicker() + ", ");
						for(int i = 1; i < inEC.size()-1; i++) {
							System.out.print(inEC.getDataByIndex(i).getTicker() + ", ");
						}
						System.out.println("and " + inEC.getDataByIndex(inEC.size()-1).getTicker() + " found on the " + copy + " exchange.");
						searching = false;
					}
				}
				didSomething = true;
			}
			else if(choice == 4) {
				edited = true;
				String cName = "";
				String cTicker = "";
				String cDate = "";
				String cExchange = "";
				String cDesc = "";
				boolean invalid = false;
				boolean naming = true;
				while(naming) {
					System.out.print("What is the name of the company you would like to add? ");
					invalid = false;
					cName = currScan.nextLine();
					
					if(cName.equals("")) {
						System.out.println("\nPlease enter a name. \n");
						continue;
					}
					
					for(int i = 0; i < contents.size(); i++) {
						if(cName.toUpperCase().equals(contents.getDataByIndex(i).getName().toUpperCase())) {
							invalid = true;
							break;
						}
					}
					
					if(invalid) {
						System.out.println("\nThere is already an entry for " + cName + ".\n");
						continue;
					}
					
					boolean symbolizing = true;
					while(symbolizing) {
						invalid = false;
						System.out.print("\nWhat is the stock symbol of " + cName + "? ");
						cTicker = currScan.nextLine();
						
						if(cTicker.equals("")) {
							System.out.println("\nPlease enter a ticker.");
							continue;
						}
						
						for(int i = 0; i < contents.size(); i++) {
							if(cTicker.toUpperCase().equals(contents.getDataByIndex(i).getTicker().toUpperCase())){
								invalid = true;
								break;
							}
						}
						
						if(invalid) {
							System.out.println("\nThere is already a ticker " + cTicker + ".");
							continue;
						}
						
						boolean dating = true;
						while(dating) {
							invalid = false;
							System.out.print("\nWhat is the start date of " + cName + "? ");
							cDate = currScan.nextLine();
							
							if(!GenericValidator.isDate(cDate, "yyyy-MM-dd", true))
								invalid = true;
							
							if(invalid) {
								System.out.println("\nDate entered is invalid.");
								continue;
							}
							
							boolean exchanging = true;
							while(exchanging) {
								invalid = false;
								System.out.print("\nWhat is the exchange where " + cName + " is listed? ");
								cExchange = currScan.nextLine();
								cExchange = cExchange.toUpperCase();
								
								if(!cExchange.equals("NASDAQ") && !cExchange.equals("NYSE")) 
									invalid = true;
								
								if(invalid) {
									System.out.println("\nThere is no exchange called " + cExchange.toUpperCase() + ".");
									continue;
								}
								
								boolean describing = true;
								while(describing) {
									invalid = false;
									System.out.print("\nWhat is the description of " + cName + "? ");
									cDesc = currScan.nextLine();
									
									if(cDesc.equals(""))
										invalid = true;
									
									if(invalid) {
										System.out.println("\nPlease enter a description.");
										continue;
									}
									describing = false;
								}
								exchanging = false;
							}
							dating = false;
						}
						symbolizing = false;
					}
					naming = false;
				}
				
				Company toAdd = new Company(cName, cTicker, cDesc, cDate, cExchange);
				contents.insert(toAdd);
				
				System.out.print("\nThere is now a new entry for:");
				System.out.print("\n" + cName + ", symbol " + cTicker + ", started on " + cDate + ", listed on " + cExchange + ",\n");
				String prettyDesc = WordUtils.wrap(cDesc, 60, "\n\t", true);
				System.out.print("\t" + prettyDesc);
				System.out.println();
				didSomething = true;
			}
			else if(choice == 5) {
				int cSize = contents.size();
				if(cSize == 0) {
					System.out.print("There are no companies to remove.\n");
					didSomething = true;
					continue;
				}
				
				edited = true;				
				for(int i = 0; i < cSize; i++) {
					System.out.println("	" + (i+1) + ") " + contents.getDataByIndex(i).getName());
				}
				boolean removing = true;
				while(removing) {
					System.out.print("Which company would you like to remove? ");
					int toRemove = 0;
					try {
						toRemove = currScan.nextInt();
					}
					catch(Exception e) {
						@SuppressWarnings("unused")
						String junk = currScan.next();
						junk = "";
						System.out.print("\nPlease enter a valid input.\n");
						System.out.println("");
						continue;
					}

					if(toRemove < 1 || toRemove > contents.size()) {
						System.out.print("\nPlease enter a valid input.\n");
						System.out.println("");
						continue;
					}
					
					String cName = contents.getDataByIndex(toRemove-1).getName();
					contents.remove(toRemove-1);
					System.out.print("\n" + cName + " is now removed.\n");
					removing = false;
				}
				didSomething = true;
			}
			else if(choice == 6) {
				didSomething = true;
				int cSize = contents.size();
				if(cSize == 0) {
					System.out.print("There are no companies to sort.\n");
					didSomething = true;
					continue;
				}
				
				edited = true;
				boolean sorting = true;
				while(sorting){
					ArrayList<Company> sorted = contents.getData();
					sorted.sort(Comparator.comparing(Company::getName));
					System.out.println("1) A to Z");
					System.out.println("2) Z to A\n");
					System.out.print("How would you like to sort by? ");
					int toRemove = 0;
					try {
						toRemove = currScan.nextInt();
					}
					catch(Exception e) {
						@SuppressWarnings("unused")
						String junk = currScan.next();
						System.out.print("\nPlease enter a valid input.\n\n");
						continue;
					}
					if(toRemove != 1 && toRemove != 2) {
						System.out.print("\nPlease enter a valid input.\n");
						continue;
					}
					
					if(toRemove == 1) {
						contents.setData(sorted);
						System.out.print("\nYour companies are now sorted in alphabetical order.\n");
					}
					else {
						Collections.reverse(sorted);
						contents.setData(sorted);
						System.out.print("\nYour companies are now sorted in reverse alphabetical order.\n");
					}					
					sorting = false;
				}
			}
			else {
				if(edited) {
					boolean deciding = true;
					System.out.println("	1) Yes");
					System.out.println("	2) No");
					while(deciding) {
						System.out.print("Would you like to save your edits? ");
						String saving = currScan.nextLine();
						int decision = -1;
						try {
							decision = Integer.parseInt(saving);
						}
						catch (Exception e){
							System.out.print("\nThat is not a valid option.\n");
							System.out.println("");
							continue;						
						}
						
						if(decision != 1 && decision != 2) {
							System.out.print("\nThat is not a valid option.\n");
							System.out.println("");
							continue;	
						}
						
						if(decision == 1) {
							System.out.println("\nYour edits have been saved to " + filename);
							try {
								BufferedWriter bf = Files.newBufferedWriter(Path.of(filename), StandardOpenOption.TRUNCATE_EXISTING);
								bf.close();
							}
							catch (Exception e){
								break;
							}
							Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
							String json = gson.toJson(contents);
							
							try{
								FileWriter fw = new FileWriter(filename);
								PrintWriter pw = new PrintWriter(fw);
								pw.println(json);
								pw.close();
								fw.close();
							}
							catch (Exception e){
								System.out.println("Catching this exception.");
							}
							deciding = false;
						}
						else {
							System.out.println("\nYour edits have not been saved to " + filename);
							deciding = false;
						}
					}
					System.out.println("Thank you for using my program!");
				}
				else 
					System.out.print("Thank you for using my program!");
				
				running = false;
				currScan.close();
			}
		}
		input.close();
		return;
	}
}