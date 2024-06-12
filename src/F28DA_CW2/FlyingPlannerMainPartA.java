package F28DA_CW2;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import java.util.Scanner;

public class FlyingPlannerMainPartA {

	public static void main(String[] args) {
		
		//CREATE AND POUPLATE GRAPH
		
		//Create a SimpleWeightedGraph where the weight is going to be the cost of the flight
        Graph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        //cities
        String edi = "edinburgh";
        String hea = "heathrow";
        String dub = "dubai";
        String kua = "kuala lumpur";
        String syd = "sydney";
      
        //add the vertices (airports)
        g.addVertex(edi);
        g.addVertex(hea);
        g.addVertex(dub);
        g.addVertex(kua);
        g.addVertex(syd);

        //add edges to create a flying circuit       
        g.addEdge(edi, hea);
        g.setEdgeWeight(edi, hea, 80);
        g.addEdge(hea, dub);
        g.setEdgeWeight(hea, dub, 130);
        g.addEdge(hea, syd);
        g.setEdgeWeight(hea, syd, 570);
        g.addEdge(dub, kua);
        g.setEdgeWeight(dub, kua, 170);
        g.addEdge(dub, edi);
        g.setEdgeWeight(dub, edi, 190);
        g.addEdge(kua, syd);
        g.setEdgeWeight(kua, syd, 150);
        
        
        //CREATE A JOURNEY
        
        //Variables
        String departure = "";
        String arrival = "";
        
        //Print all the available airports
        System.out.println("The following airports are used:" + g.vertexSet());
        
        //Ask for a departure city and an arrival city
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter the start airport");
        departure = input.nextLine().toLowerCase();
        System.out.println("Please enter the destination airport");
        arrival = input.nextLine().toLowerCase();
        input.close();
        
        //Search for the shortest path depending on the edge weight for the given departure and arrival (the cheapest way)
        DijkstraShortestPath<String, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<String, DefaultWeightedEdge> (g);
        System.out.println("The cheapest path is: ");
        System.out.println(shortestPath.getPath(departure, arrival));
        System.out.println("The cost of the cheapest path = £" + shortestPath.getPathWeight(departure, arrival));
	}

}
