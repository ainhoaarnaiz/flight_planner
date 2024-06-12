package F28DA_CW2;

public class Flight implements IFlight {
	
	private String flightCode;
	private Airport to;
	private Airport from;
	private String fromGMTime;
	private String toGMTime;
	private int cost;
	
	public Flight (String flightCode, Airport from, String fromGMTime, Airport to, String toGMTime, int cost) {
		this.flightCode = flightCode;
		this.to = to;
		this.from = from;
		this.fromGMTime = fromGMTime;
		this.toGMTime = toGMTime;
		this.cost = cost;  
	}

	@Override
	/** Returns the flight code composing the Flight */
	public String getFlightCode() {
		return flightCode;
	}

	@Override
	/** Returns the arrival airport composing the Flight */
	public Airport getTo() {
		return to;
	}

	@Override
	/** Returns the departure airport composing the Flight */
	public Airport getFrom() {
		return from;
	}

	@Override
	/** Returns the departure time composing the Flight */
	public String getFromGMTime() {
		return fromGMTime;
	}

	@Override
	/** Returns the arrival time composing the Flight */
	public String getToGMTime() {
		return toGMTime;
	}

	@Override
	/** Returns the cost of the Flight */
	public int getCost() {
		return cost;
	}


}
