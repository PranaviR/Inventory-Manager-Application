package inventory_manager_application;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class Database {
	String name;
	String company;
	LocalDate date;
	int quan;
	public Database() {

	}
	//object for adding to the ArrayList
	public void obj(String Name, String Company, LocalDate Date, int Quantity) {
		   this.name = Name;
		   this.company = Company;
		   this.date = Date;
		   this.quan = Quantity;
	}
}
	
	
	
