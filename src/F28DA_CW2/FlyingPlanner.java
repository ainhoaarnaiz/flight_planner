package F28DA_CW2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.BreadthFirstIterator;


/**
 * FLying planner composed by multiple methods that aim to find different 
 * journey types (cheapest, least cost, least cost meet up...) between airports
 * @author Ainhoa Arnaiz
 *
 */
public class FlyingPlanner implements IFlyingPlannerPartB<Airport,Flight>, IFlyingPlannerPartC<Airport,Flight> {
	
	
	
	/**
	 * HashMap with all the airports and airport codes so if you give the 
	 * code it returns the airport
	 */
	private HashMap <String,Airport> airportFinder;
	/**
	 * HashMap with all the flights and flights codes so if you give the
	 * code it returns the flight
	 */
	private HashMap <String,Flight> flightFinder;
	/**
	 * SimpleDirectedWeightedGraph which weight is the cost of each flight 
	 * (useful in leastCost methods)
	 */
	private Graph<Airport, Flight> costGraph;
	/**
	 * SimpleDirectedWeightedGraph which weight is always 1 so that when we try
	 * to find the shortest path (DijkstraShortestPath)it finds the least
	 * hop path (useful in leastCost methods)
	 */
	private Graph<Airport, Flight> hopGraph;
	/**
	 * DAG containing all airports but only the flights flying to an airport destination
	 * with strictly higher number of directly connected airports than the origin airport
	 * (useful in setDirectlyConnectedOrder)
	 */
	private Graph<Airport, Flight> dco;
	/**
	 * Used algorithm to find the shortest path (both cost and hop)
	 */
	private DijkstraShortestPath <Airport, Flight> shortestPath;
	
	
	/**
	 * Creates a new blank instance of a flying planner
	 */
	public FlyingPlanner() {
		airportFinder = new HashMap <String,Airport>();
		flightFinder = new HashMap <String,Flight>();
		costGraph = new SimpleDirectedWeightedGraph<>(Flight.class);
		hopGraph = new SimpleDirectedWeightedGraph<>(Flight.class);
		dco = new DirectedAcyclicGraph<>(Flight.class);
	}
	
	
	@Override
	public boolean populate(FlightsReader fr) {
		
		HashSet<String[]> airports = fr.getAirports();
		HashSet<String[]> flights = fr.getFlights();
		
		return populate(airports, flights);
	}

	
	@Override
	public boolean populate(HashSet<String[]> airports, HashSet<String[]> flights) {
		
		//Create iterators to iterate through the HashSets and get the airports and flights
		Iterator <String[]> itrAirport = airports.iterator();
		Iterator <String[]> itrFlight = flights.iterator();
		
		//Variables declaration
		String[] allAirports;
		String[] allFlights;
		Airport currentAirport;
		Flight currentFlight;
		Airport from;
		Airport to;
		int cost;
		Boolean add = false;
		
		//Iterate through the airports HashSet and add them to both graphs as vertex
		while (itrAirport.hasNext()) {
			
			//Gives back an airport by giving 0: airport code, 1: city, 2: airport name (an array of Strings)
			allAirports = itrAirport.next();
			
			//Create a new Airport using the airport code and city of the array
			currentAirport = new Airport (allAirports[0], allAirports[1]);
			
			//Add this Airport to the graphs
			//If the Airport has already been added it will return false
			//If it is the first time it will return true
			if (costGraph.addVertex(currentAirport) == true);
				add = true;
			
			if (hopGraph.addVertex(currentAirport) == true);
				add = true;
			
			//Add this Airport to the airports HashMap to find it easily just by giving the code
			airportFinder.put(allAirports[0], currentAirport);
				
		}
		
		//Iterate through the flights HashSet and add them to both graphs as edges
		while (itrFlight.hasNext()) {
			
			/*Gives back an airport by giving 0: flight code, 1: airport code of
			 * departure, 2: departure time GMT, 3: airport code of arrival, 4: arrival time
			 * GMT, 5: flight cost */
			allFlights = itrFlight.next();
			
			//Get the departure and arrival airport and the flight cost from the array 
			//Using the airportFinder to find the Airport from the code
			from = airportFinder.get(allFlights[1]);
			to = airportFinder.get(allFlights[3]);
			cost = Integer.parseInt(allFlights[5]);
			
			//Create a new Flight with all the info
			currentFlight = new Flight (allFlights[0],from,allFlights[2],to,allFlights[4],cost);
			
			//Add this Flight to the graphs as edge and set the weights of each (cost and 1)
			//If the Flight has already been added it will return false
			//If it is the first time it will return true
			if (costGraph.addEdge(from, to, currentFlight) == true);
				add = true;
			if (hopGraph.addEdge(from, to, currentFlight) == true);
				add = true;
			costGraph.setEdgeWeight(from, to, cost);
			hopGraph.setEdgeWeight(from, to, 1);
			
			//Add this Flight to the flights HashMap to find it easily just by giving the code
			flightFinder.put(allFlights[0], currentFlight);
			
		}	
		
		return add;
	}
	
	@Override
	public Airport airport(String code) {
		
		return airportFinder.get(code);
	}
	
	@Override
	public Flight flight(String code) {
		
		return flightFinder.get(code);
	}
	
	@Override
	public Journey leastCost(String from, String to) throws FlyingPlannerException {
		
		//Check if the airports exist
		checkAirports(airport(from), airport(to));
		
		//Set the departure and arrival Airports
		Airport departure = airport(from);
		Airport arrival = airport(to);
		
		//Create the cheapest path using the DijkstraShortestPath algorithm and the costGraph
		shortestPath = new DijkstraShortestPath <Airport, Flight> (costGraph);
		
		//Check if such a path (Journey) exists
		checkPath(shortestPath,departure,arrival);
		
		
		//List of flights of the path
		List<Flight> flights = shortestPath.getPath(departure, arrival).getEdgeList();
		
		//create a new Journey with this path and return it (cost, hops, air time, airport codes, flight codes, connecting time, total time)
		return new Journey (getCost(flights), flights.size(), airTime(flights), getAirportCodes(shortestPath,departure,arrival), 
				getFlightCodes(flights), connectingTime(flights),totalTime(flights));
	}
	
	
	@Override
	public Journey leastHop(String from, String to) throws FlyingPlannerException {
		
		//Check if the airports exist
		checkAirports(airport(from), airport(to));
		
		//Set the departure and arrival Airports
		Airport departure = airport(from);
		Airport arrival = airport(to);
		
		//Create the least hops path using the DijkstraShortestPath algorithm and the hopGraph
		shortestPath = new DijkstraShortestPath <Airport, Flight> (hopGraph);
		
		//Check if such a path (Journey) exists
		checkPath(shortestPath,departure,arrival);
		
		//List of flights of the path
		List<Flight> flights = shortestPath.getPath(departure, arrival).getEdgeList();
		
		//create a new Journey with this path and return it (cost, hops, air time, airport codes, flight codes, connecting time, total time)
		return new Journey (getCost(flights), flights.size(), airTime(flights), getAirportCodes(shortestPath,departure,arrival), 
				getFlightCodes(flights), connectingTime(flights),totalTime(flights));
	}

	@Override
	public Journey leastCost(String from, String to, List<String> excluding) throws FlyingPlannerException {
		
		//Check if the airports exist
		checkAirports(airport(from), airport(to));
		
		//Set the departure and arrival Airports
		Airport departure = airport(from);
		Airport arrival = airport(to);
		
		//Create a new graph (based on costGraph) to hold all the vertex except the excluding ones		
		Graph<Airport, Flight> costExGraph = costGraph;
		//Iterate through the excluding list 
		for (String currentExcluding:excluding) {
			//Remove all the Airport vertex from the graph and all the edges connected to them automatically
			costExGraph.removeVertex(airport(currentExcluding));
		}
		
		//Create the cheapest path using the DijkstraShortestPath algorithm and the costExGraph
		shortestPath = new DijkstraShortestPath <Airport, Flight> (costExGraph);
		
		//Check if such a path (Journey) exists
		checkPath(shortestPath,departure,arrival);
		
		//List of flights of the path
		List<Flight> flights = shortestPath.getPath(departure, arrival).getEdgeList();
		
		//create a new Journey with this path and return it (cost, hops, air time, airport codes, flight codes, connecting time, total time)
		return new Journey (getCost(flights), flights.size(), airTime(flights), getAirportCodes(shortestPath,departure,arrival), 
				getFlightCodes(flights), connectingTime(flights),totalTime(flights));
		
	}
	
	@Override
	public Journey leastHop(String from, String to, List<String> excluding) throws FlyingPlannerException {
		
		//Check if the airports exist
		checkAirports(airport(from), airport(to));
		
		//Set the departure and arrival Airports
		Airport departure = airport(from);
		Airport arrival = airport(to);
		
		//Create a new graph (based on hopGraph) to hold all the vertex except the excluding ones		
		Graph<Airport, Flight> hopExGraph = hopGraph;
		//Iterate through the excluding list 
		for (String currentExcluding:excluding) {
			//Remove all the Airport vertex from the graph and all the edges connected to them automatically
			hopExGraph.removeVertex(airport(currentExcluding));
		}
		
		//Create the cheapest path using the DijkstraShortestPath algorithm and the costExGraph
		shortestPath = new DijkstraShortestPath <Airport, Flight> (hopExGraph);
		
		//Check if such a path (Journey) exists
		checkPath(shortestPath,departure,arrival);
		
		//List of flights of the path
		List<Flight> flights = shortestPath.getPath(departure, arrival).getEdgeList();
		
		//create a new Journey with this path and return it (cost, hops, air time, airport codes, flight codes, connecting time, total time)
		return new Journey (getCost(flights), flights.size(), airTime(flights), getAirportCodes(shortestPath,departure,arrival), 
				getFlightCodes(flights), connectingTime(flights),totalTime(flights));
	}

	///////////////////////////////////////////////////////////////////////////////////////////MY METHODS
	
	
	/**
	 * It @return all the Vertex/Airports of the costGraph
	 */
	public Set <Airport> getAllVertex(){
		return costGraph.vertexSet();
	}
	
	/**
	 * Checks if the departure and arrival airports exist ( @param from and @param to) 
	 * If not @throws a FlyingPlannerException
	 */
	public void checkAirports (Airport from, Airport to) throws FlyingPlannerException {
		if (from == null || to == null) {
			throw new FlyingPlannerException("Please enter valid Airport codes.");
		}
	}
	
	/**
	 * Checks if the created path/journey @param shortestPath from @param departure to @param arrival exists
	 * If not @throws a FlyingPlannerException 
	 */
	public void checkPath (DijkstraShortestPath <Airport, Flight> shortestPath, Airport departure, Airport arrival) throws FlyingPlannerException{
		if (shortestPath.getPath(departure, arrival) == null) {
			throw new FlyingPlannerException("Sorry there is no flights between these airports.");
		}
	}
	
	/**
	 * It @return a List(String) of flight codes coming from the @param flights (List(Flight))
	 */
	public List<String> getFlightCodes (List<Flight> flights){
		
		//List of flight codes
		List<String> flightCodes = new LinkedList<String>();
		//Iterate through all the flights, get the codes and add them to code's list
		for (Flight currentFlight:flights) {
			flightCodes.add(currentFlight.getFlightCode());
		}
		
		//return list of codes
		return flightCodes;
	}
	

	/**
	 * Given a @param shortestPath from @param departure to @param arrival, it first gets all 
	 * the airports of the path and then gets the code of each and @return them as a List(String)
	 */
	public List<String> getAirportCodes (DijkstraShortestPath <Airport, Flight> shortestPath, Airport departure, Airport arrival){
		
		//List of airports of the path
		List<Airport> airports = shortestPath.getPath(departure, arrival).getVertexList();
		//List of airport codes
		List<String> airportCodes = new LinkedList<String>();
		//Iterate through all the airports, get the codes and add them to code's list
		for (Airport currentAirport : airports) {
			airportCodes.add(currentAirport.getCode());
		}
		
		//return list of codes
		return airportCodes;
	}
	
	
	/**
	 * Given a list of flights @param flights, iterates and @return the sum of the cost of all of them
	 */
	public int getCost (List<Flight> flights){
		
		int cost = 0;
		//Iterate through all the flights, get the costs and add them to the total cost
		for (Flight currentFlight:flights) {
			cost = cost + currentFlight.getCost();
		}
		return cost;
	}
	
	
	/**
	 * Given list of flights  @param flights, first gets the departure and arrival hours of each flight, 
	 * convert them into minutes and subtract them and then add the result to the total air time. 
	 * It  @return the total air time of that journey.
	 */
	public int airTime (List<Flight> flights) {
		
		//Variables
		int from = 0;
		int to = 0;
		int airTime = 0;
		
		//Iterate through all the flights and sum each's air time
		for (Flight flight : flights) {
			
			//get the hours of departure and arrival
			from = Integer.parseInt(flight.getFromGMTime());
			to = Integer.parseInt(flight.getToGMTime());
			//Convert both of them into minutes by multiplying the hours by 60 and adding the minutes
			from = ((from/100)*60) + (from%100);
			to = ((to/100)*60) + (to%100);
			
			/* If the arrival hour is smaller than the departure (D:2330 A:0045) add 24h (1440 minutes) so that
			 * arrival is always greater than departure (D:2330 A:2445)
			 */
			if (from>to) {
				to = to + 1440;
			}
			//Subtract the departure minutes to the arrival ones and add them to the total time
			airTime = airTime + (to - from);
		
		}
		
		return airTime;
	}
	
	/**
	 * Given list of flights  @param flights, first gets the departure and arrival hours of each flight, 
	 * convert them into minutes and subtract the current arrival time to the next flights departure time and
	 * add the result to the total connecting time. It  @return the total connecting time of that journey.
	 */
	public int connectingTime (List<Flight> flights) {
		
		//Variables
		int connectTime = 0;
		int convertFrom;
		int convertTo;
		int from[] = new int[flights.size()];
		int to[] = new int[flights.size()];
		int i = 0;
		int arrival;
		int departure;
		
		//Iterate through all the flights and add all the departing and arriving hours(minutes) in arrays
		for (Flight flight: flights) {
			//get the hours of departure and arrival
			convertFrom = Integer.parseInt(flight.getFromGMTime());
			convertTo = Integer.parseInt(flight.getToGMTime());
			//Convert both of them into minutes by multiplying the hours by 60 and adding the minutes and add them to the arrays
			from [i] = ((convertFrom/100)*60) + (convertFrom%100);
			to [i] = ((convertTo/100)*60) + (convertTo%100);
			i++;
		}
		
		//Iterate through the arrays and add the connecting time of every stop
		for (int k = 0; k<flights.size()-1; k++) {
			
			//Current flight arriving time
			arrival = to[k];
			//Next flight departure time
			departure = from[k+1];
			
			/* If the departure hour is smaller than the arrival, add 24h (1440 minutes) so that
			 * departure time is always greater than arrival one
			 */
			if (arrival>departure) {
				departure = departure + 1440;
			}
			//Subtract the arrival minutes to the departure ones and add them to the total time
			connectTime = connectTime + (departure - arrival);
			
		}
		
		return connectTime;
		
	}
	

	/**
	 * Given a list of flights  @param flights it calls airTime(flights) and 
	 * connectingTime(flights) and adds then together to calculate and @return the
	 * total time of the journey
	 */
	public int totalTime (List<Flight> flights){
		//Add air time and connecting time to get total time
		int totalTime = airTime(flights) + connectingTime(flights);
		return totalTime;
	}
		
		

	//////////////////////////////////////////////////////////////////////////////////////////////PART C
	
	@Override
	public Set<Airport> directlyConnected(Airport airport) {
		
		//create a new Set of airports
		Set <Airport> airports = new HashSet<>();
		//Set of flights the given airport always as departure airport
		Set <Flight> flights = costGraph.outgoingEdgesOf(airport);
		//Iterate through the flights
		for (Flight currentFlight : flights) {
			/* If the graph contains a flight which has the given airport as arrival airport
			 * and the current arrival airport as departure airport(two flights connecting them in
	  		 * a single hop in both direction, which means they are directly connected), add the current 
			 * arrival airport to the Set
			 */
			if (costGraph.containsEdge(currentFlight.getTo(), airport)) {
				airports.add(currentFlight.getTo());
			}
		}
		
		//set this airport's directly connected flights (useful to get in other methods)
		airport.setDicrectlyConnected(airports);
		
		return airports;
	}

	@Override
	public int setDirectlyConnected() {
		
		//Set of all the airports of the costGraph
		Set <Airport> airports = costGraph.vertexSet();
		int sum = 0;
		
		//Iterate through the airports
		for (Airport currentAirport: airports) {
			//Add the size of the set of directly connected airports for every single airport of the costGraph
			sum = sum + directlyConnected(currentAirport).size();
		}
		
		//Return the sum of all the airports directly connected set sizes
		return sum;
	}

	@Override
	public int setDirectlyConnectedOrder() {
		
		//Variables
		Set <Airport> airports = costGraph.vertexSet();
		Set <Airport> directlyConnected;
		int originSize;
		int currentSize;
	
		//Iterate through all the airports
		for (Airport originAirport : airports) {
			//Get the set of airports directly connected to this origin one
			directlyConnected = originAirport.getDicrectlyConnected();
			//Get the size of the set of airports directly connected to this origin one
			originSize = originAirport.getDirectlyConnectedOrder();
			//Add the airport as a vertex to the DAG
			dco.addVertex(originAirport);
			
			//Iterate through the set of directly connected airports related to the origin one
			for(Airport currentAirport : directlyConnected) {
				
				//Get the size of the set of airports directly connected to the current airport
				currentSize = currentAirport.getDirectlyConnectedOrder();
				
				/* If the number of dc airports of the current airport is greater than the number 
				 * of dc airports of the origin one, then we add the current airport to the DGA and
				 * the flight from the origin airport to the current one (from smallest size, to biggest size)
				 */
				if (currentSize > originSize) {
					dco.addVertex(currentAirport);
					dco.addEdge(originAirport, currentAirport, costGraph.getEdge(originAirport, currentAirport));
				}

			}
	
		}
		
		//Returns the number of flights of the DAG
		return dco.edgeSet().size();

	}

	@Override
	public Set<Airport> getBetterConnectedInOrder(Airport airport) {
		
		Iterator <Airport> traverse = new BreadthFirstIterator <Airport, Flight> (dco,airport);
		Set <Airport> airports = new HashSet<>();
		
		/* Iterates/traverses through the DAG using breadth first search technique and adds every airport 
		 * reachable from the given airport that has strictly more direct connections
		 */
		while (traverse.hasNext()) {
			airports.add(traverse.next());
		}
		
		//Removes the given airport from the Set because we don't want it there
		airports.remove(airport);
		
		//Returns the set of airports reachable from the given airport that have strictly more direct connections
		return airports;
	}

	@Override
	public String leastCostMeetUp(String at1, String at2) throws FlyingPlannerException {
		//Create the cheapest journey between the 2 airports 
		Journey leastCostMeet = leastCost(at1,at2);
		//Calculate what the half would be
		int half = leastCostMeet.getStops().size()/2 - 1;
		//Return the stop/airport in the half
		return leastCostMeet.getStops().get(half);
	}

	@Override
	public String leastHopMeetUp(String at1, String at2) throws FlyingPlannerException {
		//Create the least hop journey between the 2 airports 
		Journey leastHopMeet = leastHop(at1,at2);
		//Calculate what the half would be
		int half = leastHopMeet.getStops().size()/2 - 1;
		//Return the stop/airport in the half
		return leastHopMeet.getStops().get(half);
	}

	
	//I tried to implement this one, but I couldn't find a way to do it and non of the lab helpers knew how to do it either
	@Override
	public String leastTimeMeetUp(String at1, String at2, String startTime) throws FlyingPlannerException {
		// TODO Auto-generated method stub
		return null;
	}


}
