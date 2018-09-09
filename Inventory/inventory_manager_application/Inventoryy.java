package inventory_manager_application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import inventory_manager_application.utils.Errors;

class Inventoryy {
	//ArrayList of objects where each object holds each entry attributes
	ArrayList<Database> databaseCollection = new ArrayList<>();
	
	//HashMap to check for duplicate entries
	HashMap<String, String> map = new HashMap<String, String>();
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	static Inventoryy inv = new Inventoryy();
	
	//Count of entries in the database at any point of time
	int global_count=0;
	
	//Main function
	public static void main(String[] args) throws FileNotFoundException {
		
		File file = new File("in.txt");
		Scanner sc = new Scanner(file);
		while(sc.hasNextLine()) {
			String str = sc.nextLine();
			ArrayList split_list = inv.split(str);
			String[] split_array = (String[]) split_list.toArray(new String[split_list.size()]);
			
			String cmd = split_array[0];
			
			switch(cmd) {
			
			case "LOAD":
				if(split_array.length != 2) 
					inv.output("LOAD: ERROR WRONG_ARGUMENT_COUNT");
				else 
					inv.load(split_array);
				break;
		        
			case "STORE":
				if(split_array.length != 2) 
					inv.output("STORE: ERROR WRONG_ARGUMENT_COUNT");
				else 
					inv.store(split_array);
				break;
					
			case "CLEAR":
				if(split_array.length != 1) 
					inv.output("CLEAR: ERROR WRONG_ARGUMENT_COUNT");
				else 
					inv.clear();
				break;
			
			case "ADD":
				if(split_array.length != 4) 
					inv.output("ADD: ERROR WRONG_ARGUMENT_COUNT");
				else 
					inv.add(split_array);
				break;
				  
			case "STATUS":
				if(split_array.length != 1) 
					inv.output("STATUS: ERROR WRONG_ARGUMENT_COUNT");
				else 
					inv.status(split_array);
				break;
				
			case "BUY":
				if(split_array.length != 4) 
					inv.output("BUY: ERROR WRONG_ARGUMENT_COUNT");
				else 
					inv.buy(split_array);
				break;
				
			case "SELL":
				if(split_array.length != 4) 
					inv.output("SELL: ERROR WRONG_ARGUMENT_COUNT");
				else 
					inv.sell(split_array);
				break;
				
			case "QUAN":
				if(split_array[1].equals("GREATER")){
					if(split_array.length != 3) 
						inv.output("QUAN: ERROR WRONG_ARGUMENT_COUNT");
					else 
						inv.quan_greater(split_array);
				}
				else if(split_array[1].equals("FEWER")){
					if(split_array.length != 3) 
						inv.output("QUAN: ERROR WRONG_ARGUMENT_COUNT");
					else 
						inv.quan_fewer(split_array);
				}
				else if(split_array[1].equals("BETWEEN")){
					if(split_array.length != 4) 
						inv.output("QUAN: ERROR WRONG_ARGUMENT_COUNT");
					else 
						inv.quan_between(split_array);
				}
				else {
					inv.output("QUAN: ERROR UNKNOWN_COMMAND");
				}
				break;

			case "SEARCH":
				if(split_array.length != 2) 
					inv.output("SEARCH: ERROR WRONG_ARGUMENT_COUNT");
				else 
					inv.search(split_array);
				break;
					
			default:
				inv.output(split_array[0]+": ERROR UNKNOWN_COMMAND");
				break;
			}
		}
	}
	
	
	
	
	public void load(String[] array) {
		
		String csvFile = array[1];
        String line = "";
        int count = 0;
        databaseCollection.clear();
		map.clear();
		global_count=0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] entry = line.split(",");
                String name = entry[0];
                String company = entry[1];
//                if(!Errors.dateisValid(entry[2])) {
//            		inv.output("ADD: ERROR INVALID_DATE");
//            		count++;
//            		continue;
//            	}
     		    LocalDate date = null;
     		    int quantity = Integer.parseInt(entry[3]);
     		    
     		    try {
     		    	date = LocalDate.parse(entry[2] , formatter);
     		    	String name_mod = inv.add_remove_quotes(name); 
     		    	String company_mod = inv.add_remove_quotes(company); 
//     		    	if((map.containsKey(name) && (map.get(name).equals(company) || map.get(name).equals(company_mod))) || 
//     		    	  (map.containsKey(name_mod) && (map.get(name_mod).equals(company) || map.get(name_mod).equals(company_mod)))){
//                     inv.output("LOAD: ERROR DUPLICATE_ENTRY");
//     		    		count++;
//     		    		continue;
//                    }
//                    
//                    else {
                    	Database db_entry = new Database();
                    	db_entry.obj(name, company, date, quantity);
    	                databaseCollection.add(db_entry);
    	                map.put(name,company);
    	                global_count++;
    	                count++;
//                    }
     		     }
     		    catch(Exception e) {
     		    	inv.output("LOAD: ERROR INVALID_DATE");
     		     }
               }
            	inv.output("LOAD:OK "+count);
            }
        
       catch (IOException e) {
          inv.output("LOAD:ERROR FILE_NOT_FOUND");   
        }
	}
	
	
	
	
	public void store(String[] array) {
		int count = 0;
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(array[1]);
			for(Database db: databaseCollection) {
				fileWriter.append(db.name+","+db.company+","+ db.date+","+db.quan);
				fileWriter.append("\n");
				count++;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}
		}
		inv.output("STORE:OK "+count);
	}
	
	
	
	
	public void clear() {
		databaseCollection.clear();
		map.clear();
		global_count=0;
		inv.output("CLEAR:OK");
	}
	
	
	

	public void add(String[] array) {
		String name_mod = inv.add_remove_quotes(array[1]);
		String company_mod = inv.add_remove_quotes(array[2]);
		if((map.containsKey(array[1]) && (map.get(array[1]).equals(array[2]) || map.get(array[1]).equals(company_mod))) || 
		    	  (map.containsKey(name_mod) && (map.get(name_mod).equals(array[2]) || map.get(name_mod).equals(company_mod)))){
				inv.output("ADD: ERROR DUPLICATE_ENTRY");	
        }
        else {
        	Database db_entry = new Database();
        	if(!Errors.dateisValid(array[3])) {
        		inv.output("ADD: ERROR INVALID_DATE");
        		return;
        	}
        	LocalDate date = null;
 		    try {
 		    	date = LocalDate.parse(array[3] , formatter);
 		    	db_entry.obj(array[1], array[2], date, 0);
 	            databaseCollection.add(db_entry);
 	            global_count++;
 	            inv.output("ADD:OK "+ db_entry.name+" "+db_entry.company);
 		    }
 		    catch(Exception e) {
 		    	inv.output("ADD: ERROR INVALID_DATE");
 		    }	                  
        }
	}
	
	
	
	
	public void status(String[] array) {
		inv.output("STATUS:OK "+ global_count);
		for(Database db:databaseCollection) {
			String date = db.date.format(formatter);
			inv.output(db.name+","+db.company+","+date+","+db.quan);
		}
	}
	
	
	
	public void buy(String[] array) {
		boolean entry_exists_buy = false;
		try {
			int quantity = Integer.parseInt(array[3]);
			if(quantity>=1) {
				for(Database db:databaseCollection) {
					if((db.name.equals(array[1])|| db.name.equals(inv.add_remove_quotes(array[1]))) 
							&& (db.company.equals(array[2]) || db.company.equals(inv.add_remove_quotes(array[2])))) {
						db.quan+=quantity;
						entry_exists_buy = true;
						inv.output("BUY:OK "+ db.name +" "+db.company+" "+db.quan);
					}
				}
				if(!entry_exists_buy) {
					inv.output("BUY: ERROR CANNOT_BUY_BEFORE_ADD");
				}
			}
			else {
				inv.output("BUY: ERROR INVALID_QUANTITY");
			}	
		}
		catch(Exception e) {
			inv.output("BUY: ERROR INVALID_QUANTITY");
		}
	}
	
	
	
	
	public void sell(String[] array) {
		boolean entry_exists_sell = false;
		try {
			int quantity = Integer.parseInt(array[3]);
			if(quantity>=1) {
				for(Database db:databaseCollection) {
					if((db.name.equals(array[1])|| db.name.equals(inv.add_remove_quotes(array[1]))) 
							&& (db.company.equals(array[2]) || db.company.equals(inv.add_remove_quotes(array[2])))) {
						if(db.quan>=quantity) {
							db.quan-=quantity;
							inv.output("SELL:OK "+ db.name +" "+db.company+" "+db.quan);
						}
						else {
							inv.output("SELL: ERROR CANNOT_SELL_QUANTITY_MORE_THAN_STOCK");
						}
						entry_exists_sell = true;
						break;
					}
				}
				if(!entry_exists_sell) {
					inv.output("SELL: ERROR CANNOT_SELL_BEFORE_ADD");
				}
			}
			else {
				inv.output("SELL: ERROR INVALID_QUANTITY");
			}	
		}
		catch(Exception e) {
			inv.output("SELL: ERROR INVALID_QUANTITY");
		}
	}
	
	
	
	public void quan_greater(String[] array) {
		int quantity = Integer.parseInt(array[2]);
		int count=0;
		ArrayList<Database> list = new ArrayList<Database>();
		for(Database db:databaseCollection) {
			if(db.quan>quantity) {
				list.add(db);
				count++;
			}
		}
		inv.output("QUAN:OK "+count);
		for(Database db:list) {
			String date = db.date.format(formatter);
			inv.output(db.name+","+db.company+","+date+","+db.quan);
		}
		list.clear();
	}
	
	
	
	public void quan_fewer(String[] array) {
		int quantity = Integer.parseInt(array[2]);
		int count=0;
		ArrayList<Database> list = new ArrayList<Database>();
		for(Database db:databaseCollection) {
			if(db.quan<quantity) {
				list.add(db);
				count++;
			}
		}
		inv.output("QUAN:OK "+count);
		for(Database db:list) {
			String date = db.date.format(formatter);
			inv.output(db.name+","+db.company+","+date+","+db.quan);
		}
		list.clear();
	}
	
	
	
	public void quan_between(String[] array) {
		int quantity1 = Integer.parseInt(array[2]);
		int quantity2 = Integer.parseInt(array[3]);
		ArrayList<Database> list = new ArrayList<Database>();
		int count=0;
		for(Database db:databaseCollection) {
			if(quantity1< db.quan && db.quan<quantity2) {
				list.add(db);
				count++;
			}
		}
		inv.output("QUAN:OK "+count);
		for(Database db:list) {
			String date = db.date.format(formatter);
			inv.output(db.name+","+db.company+","+date+","+db.quan);
		}
		list.clear();
	}
	
	
	
	
	public void search(String[] array) {
		ArrayList<Database> list = new ArrayList<Database>();
		String s = array[1];
		int count=0;
		for(Database db:databaseCollection) {
			if(db.name.contains(s) || db.company.contains(s)) {
				list.add(db);
				count++;
			}
		}
		inv.output("SEARCH:OK "+count);
		for(Database db:list) {
			String date = db.date.format(formatter);
			inv.output(db.name+","+db.company+","+date+","+db.quan);
		}
		list.clear();
	}
	
	
	
	//Function to split the entry input by space separation and 
	//protecting the arguments enclosed in double quotes
	public ArrayList<String> split(String str) {
		ArrayList<String> list = new ArrayList<String>();
		boolean doublequote = false;
		String word = "";
		for(int i=0;i<str.length();i++) {
			if(str.charAt(i)==' ' && doublequote==false){
				list.add(word);
				word="";
				continue;
			}
			else if(str.charAt(i)=='\"' && doublequote==true) {
				doublequote=false;
			}
			else if(str.charAt(i)== '\"' && doublequote==false) {
				doublequote=true;
			}
				word = word + str.charAt(i);
		}
		list.add(word);
		return list;
	}
	

	//For comparing arguments in database and the input
	//irrespective of the double quotes
	public String add_remove_quotes(String str1) {
		int len = str1.length();
		if(str1.charAt(0)=='\"' && str1.charAt(len-1)=='\"') {
			return str1.substring(1,len-1);
		}
		else if(!(str1.charAt(0)=='\"' && str1.charAt(len-1)=='\"')) {
			return '\"'+str1+'\"';
		}
		return null;
	}
	
	
	//For writing to the out.txt file			
	public void output(String str) {
		File file1 = new File("hw1\\out.txt");
		try(FileWriter fw = new FileWriter(file1, true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println(str+"\n");
			} catch (IOException e) {
			    System.out.println("error:"+e);
			}
	}
}

