package F28DA_CW2;

import java.util.List;

public class Journey implements IJourneyPartB<Airport, Flight>, IJourneyPartC<Airport, Flight> {
	
	private int cost;
	private int hops;
	private int airTime;
	private int connectTime;
	private int totalTime;
	private List<String> stops;
	private List<String> flights;
	
	public Journey (int cost, int hops, int airTime, List<String> stops, List<String> flights, int connectTime, int totalTime) {
		this.cost = cost;
		this.hops = hops;
		this.airTime = airTime;
		this.stops = stops;
		this.flights = flights;
		this.connectTime = connectTime;
		this.totalTime = totalTime;
		
	}
	

	@Override
	public List<String> getStops() {
		return stops;
	}

	@Override
	public List<String> getFlights() {
		return flights;
	}

	@Override
	public int totalHop() {
		return hops;
	}

	@Override
	public int totalCost() {
		return cost;
	}

	@Override
	public int airTime() {
		return airTime;
	}

	@Override
	public int connectingTime() {
		return connectTime;
	}

	@Override
	public int totalTime() {
		return totalTime;
	}

}
