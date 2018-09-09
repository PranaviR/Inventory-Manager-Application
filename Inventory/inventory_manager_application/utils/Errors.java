package inventory_manager_application.utils;

public class Errors {	
	//For checking the date is valid by value
	public static boolean dateisValid(String date) {
		if(date.length() != 10) {
			return false;
		}
		try {
			int month = Integer.parseInt(date.substring(0,2));
			int day = Integer.parseInt(date.substring(3,5));
			int year = Integer.parseInt(date.substring(6));
			if( (!(year%4 == 0)) && (month == 2)) {
				if(day>28)
					return false;
			}
			else if(month == 1 || month == 3 || month == 5 || month == 7 
					|| month == 8 || month == 10 || month == 12) {
				if(!(day <= 31))
					return false;
			}
			else if(month == 4 || month == 6 || month == 9 || month == 11) {
				if(!(day <= 30))
					return false;
			}
		}catch(Exception e) {
			return false;
		}
		return true;		
	}
}
