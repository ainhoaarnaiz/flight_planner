package F28DA_CW2;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.Formatter;

public class FlyingPlannerMainPartBC {

	public static void main(String[] args) {

		/* Your implementation should be in FlyingPlanner.java, this class is only to
		 * run the user interface of your program
		 */

		FlyingPlanner fi;
		fi = new FlyingPlanner();
		try {
			
			//Populate the Flying Planner
			fi.populate(new FlightsReader());
			
			/* Print all the airports names and  codes so the user knows all the alternatives.
			 * The user will have to enter the airport code later (not the name)
			 */ 
			System.out.println("The following airports are used:");
			Set <Airport> airports = fi.getAllVertex();
			for (Airport current:airports) {
				System.out.println(current.getName() + "  " + current.getCode());
			}
			
			//Scan airports chosen by the user (he/she will give the airport code)
			System.out.println("");
			Scanner input = new Scanner(System.in);
	        System.out.println("Please enter the start airport CODE");
	        String start = input.nextLine().toUpperCase();
	        System.out.println("Please enter the destination airport CODE");
	        String destination = input.nextLine().toUpperCase();
	        input.close();
	        
	        //Create and print the cheapest journey between them in "table" format using String.format
	        System.out.println("");
	        Journey cheapestJourney = fi.leastCost(start, destination);
	        System.out.println("Journey for " + fi.airport(start).getName() +" ("+ start + ") to " + fi.airport(destination).getName() +" ("+ destination+")" );
	        
	        //Headlines first
	        //The largest airport name occupies 34 character spaces that is why, leave an arrive are that wide
	        System.out.print(String.format("%-6s", "Legs"));
	        System.out.print(String.format("%-35s", "Leave"));
	        System.out.print(String.format("%-6s", "At"));
	        System.out.print(String.format("%-8s", "On"));
	        System.out.print(String.format("%-35s", "Arrive"));
	        System.out.print(String.format("%-6s", "At")+"\n");
	        
	        //Then iterates through the list of flights to print all the info of each flight on each row of the "table" 
	        List <String> flights = cheapestJourney.getFlights();
	        int i = 1;
	        for(String fcode:flights) {
	        	
		    	String leg =  Integer.toString(i);
		    	String leave = fi.flight(fcode).getFrom().getName()+ " ("+ fi.flight(fcode).getFrom().getCode()+")";
		    	String fromTime = fi.flight(fcode).getFromGMTime();
		    	String arrive = fi.flight(fcode).getTo().getName()+ " ("+ fi.flight(fcode).getTo().getCode()+")";
		    	String toTime = fi.flight(fcode).getToGMTime();
		    	
		    	System.out.print(String.format("%-6s", leg));
		    	System.out.print(String.format("%-35s", leave));
		    	System.out.print(String.format("%-6s", fromTime));
		    	System.out.print(String.format("%-8s", fcode));
		    	System.out.print(String.format("%-35s", arrive));
		    	System.out.print(String.format("%-6s", toTime)+"\n");
		    	
		    	i++;
	        }
	        
	        //Finally prints the total cost and air time of the journey
	        System.out.println("Total Journey Cost = " + cheapestJourney.totalCost()+"€");
	        System.out.println("Total Time in the Air = " + cheapestJourney.airTime()+" minutes");
	        
		} catch (FileNotFoundException | FlyingPlannerException e) {
			e.printStackTrace();
		}

	}

}


