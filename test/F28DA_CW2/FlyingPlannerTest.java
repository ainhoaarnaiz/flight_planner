package F28DA_CW2;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class FlyingPlannerTest {

	FlyingPlanner fi;

	@Before
	public void initialize() {
		fi = new FlyingPlanner();
		try {
			fi.populate(new FlightsReader());
		} catch (FileNotFoundException | FlyingPlannerException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void leastHopTimeTest() {
		try {
			Journey i = fi.leastHop("PDX", "JFK");
			assertEquals(262, i.airTime());
			assertEquals(0, i.connectingTime());
			assertEquals(262, i.totalTime());
		} catch (FlyingPlannerException e) {
			fail();
		}
	}
	
	@Test
	public void getAirportTest() {
		assertEquals("Edinburgh", fi.airport("EDI").getName());
		assertEquals("Barcelona", fi.airport("BCN").getName());
	}
	
	@Test
	public void getFlightTest() {
		assertEquals(109, fi.flight("DL2958").getCost());
		assertEquals(90, fi.flight("NH0621").getCost());
	}
	
	@Test
	public void leastCostandHopTest() {
		FlyingPlanner fp = new FlyingPlanner();
		HashSet<String[]> airports = new HashSet<String[]>();
		String[] a1= {"EDI","Edinburgh","Edinburgh Airport"}; airports.add(a1);
		String[] a2= {"DUB","Dublin","Dublin Airport"}; airports.add(a2);
		String[] a3= {"NYC","New York","New York City Airport"}; airports.add(a3);
		HashSet<String[]>  flights = new HashSet<String[]>();
		String[] f1= {"HCVNS2","EDI","1200","DUB","1305","500"}; flights.add(f1);
		String[] f2= {"HCVNS3","DUB","1340","EDI","1445","500"}; flights.add(f2);
		String[] f3= {"LDFHD5","DUB","1600","NYC","2235","50"}; flights.add(f3);
		String[] f4= {"KSLDO3","NYC","2300","EDI","0237","50"}; flights.add(f4);
		fp.populate(airports, flights);
		try {
			
			Journey lc = fp.leastCost("DUB", "EDI");
			assertEquals(100,lc.totalCost());
			assertEquals(2,lc.totalHop());
			assertEquals(637,lc.totalTime());
			Journey lh = fp.leastHop("DUB", "EDI");
			assertEquals(1,lh.totalHop());
			assertEquals(500,lh.totalCost());
			assertEquals(65,lh.totalTime());
			
		} catch (FlyingPlannerException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	@Test
	public void betterConnectedInOrderTest() {
		fi.setDirectlyConnected();
		fi.setDirectlyConnectedOrder();
		Airport edi = fi.airport("NYC");
		Set<Airport> betterConnected = fi.getBetterConnectedInOrder(edi);
		assertEquals(1118, betterConnected.size());
	}

}
