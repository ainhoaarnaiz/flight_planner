package F28DA_CW2;

import java.util.Set;

public class Airport implements IAirportPartB, IAirportPartC {
	
	private String code;
	private String name;
	private Set<Airport> directlyConnected;
	private int directlyConnectedOrder;
	
	public Airport (String code, String name) {
		this.code = code;
		this.name = name;
		directlyConnected = null;
		directlyConnectedOrder = 0;
	}
	
	
	@Override
	/** Returns the airport code composing the Airport */
	public String getCode() {
		return code;
	}

	@Override
	/** Returns the airport name composing the Airport */
	public String getName() {
		return name;
	}

	
	//////////////////////////////////////////////////////PART C
	@Override
	/** instantiates directlyConnected to a set of airports directly connected to the Airport 
	 * and setDicrectlyConnectedOrder to directlyConnected's size*/
	public void setDicrectlyConnected(Set<Airport> directlyConnected) {
		this.directlyConnected = directlyConnected;
		setDicrectlyConnectedOrder (directlyConnected.size());
	}

	@Override
	/** returns a set of airports directly connected to the Airport*/
	public Set<Airport> getDicrectlyConnected() {
		return directlyConnected;
	}


	@Override
	/** sets directlyConnectedOrder to a int which is always going to 
	 * be the number of airports directly connected to the Airport*/
	public void setDicrectlyConnectedOrder(int order) {
		directlyConnectedOrder = order;

	}

	@Override
	/** returns directlyConnectedOrder, the number of airports 
	 * directly connected to the Airport*/
	public int getDirectlyConnectedOrder() {
		return directlyConnectedOrder;
	}

}
