//COP3503C-20Summer C001 - Assignment 2 - Part 1 - Dijkstra's Algorithm 
//Torres Amanda 
//References too-
//Dijkstra's Shortest Path Algorithm | Graph Theory - https://www.youtube.com/watch?v=pSqmAO-m7Lk&t=490s
//How to use Dijkstra's Algorithm with Code - https://www.youtube.com/watch?v=d6ZFqjH63vo

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

class Dijkstra{

	class Graph{ //class for Graph 
		int vertices;
		int adjMatrix[][];
		
		public Graph(int num) { //create graph
			this.vertices = num; //set number of vertices 
			adjMatrix = new int[num][num]; //create a new Adjacency Matrix with num * num
		}
		
		public void addEdge(int from, int to, int weight) { //add a new edge
			adjMatrix[from][to] = weight; //set to weight
			adjMatrix[to][from] = weight; //since edges are doubly connected must set [to][from] = weight 
		}
		
		public void printMatrix() { //function to print matrix (check if matrix properly created)
			for(int i = 1; i<vertices; i++) {
				for(int j = 1; j<vertices; j++) {
					System.out.printf("%d ",adjMatrix[i][j]);
				}
				System.out.printf("\n");
			}
		}
		
		public int[][] getMatrix(){ //return the matrix if needed
			return adjMatrix;
		}
		
	}
	
	public int getMinVal(boolean visited[], int dist[]) { //function to get the minimum index
		int max = Integer.MAX_VALUE; //set max to MAX_VAL
		int minInd = -1; //temp set to -1 
		for(int i = 0; i < dist.length; i++) {
			if(!visited[i] && dist[i] <= max) { //check that we havent visited yet and that dist <= max 
				max = dist[i];
				minInd = i; //set the min index to i since we need to return it
			}
		}
		return minInd; //return the minimum index 
	}
	
	public void dijkstra(int adjMat[][], int source) {
		boolean visited[] = new boolean[adjMat.length]; //keep track of which nodes have been visited
		int dist[] = new int[adjMat.length]; //keep track of the distance
		int parent[] = new int[adjMat.length]; //keep track of the parent
	
		Arrays.fill(visited, false); //set all values in the visited array as false initially 
		Arrays.fill(dist, Integer.MAX_VALUE); //set the dist to MAX_VALUE
		Arrays.fill(parent, -1); //set all parents to -1
		dist[source] = 0; //since we are at the source, distance from itself is 0
		
		for(int i = 0; i < adjMat.length; i++) { //repeat for all vertices 
			int minVertex = getMinVal(visited,dist); //get min index
			visited[minVertex] = true; //set that index to true
			
			for(int j = 0; j < adjMat.length; j++) { //check the neighbors for shorter distance 
				if(!visited[j] && adjMat[minVertex][j] != 0 && dist[minVertex]!= Integer.MAX_VALUE &&dist[minVertex] + adjMat[minVertex][j] < dist[j]) {
						parent[j] = minVertex; //change the parent 
						dist[j] = dist[minVertex] + adjMat[minVertex][j]; //update the distance 
					}
				}
			}
		dist[source] = -1; //set dist of source to -1 to match expected output for source 
		//printDij(source,dist,parent); //print output to screen, not needed
		
		try { //try & catch to print the output to a file 
			printDijToFile(source,dist,parent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printDij(int source, int distance[],int parent[]) { //function to print output of Dijkstra 
		System.out.println(distance.length-1);
		for (int i = 1; i <distance.length ; i++) {
            System.out.println(i + " " + distance[i] + " " + parent[i]);
			}	
	}
	
	public void printDijToFile(int source, int distance[],int parent[]) throws IOException { //function to print the output to a file
		FileWriter outFile = new FileWriter("cop3503-asn2-output-Torres-Amanda.txt");
		outFile.write(distance.length-1 + "\n");
		for (int i = 1; i <distance.length ; i++) {
            outFile.write(i + " " + distance[i] + " " + parent[i] + "\n");
			}
		outFile.close();
	}
		 
}

public class dijkstraMain {
	public static void main(String[] args) throws FileNotFoundException 
	{ 
		File inpFile = new File("cop3503-asn2-input.txt"); //create a new file
		Scanner newScan = new Scanner(inpFile); //create new scanner 
		int numVert = newScan.nextInt(); //get # vertexes, edges and source
		int soVert = newScan.nextInt();
		int numEdges = newScan.nextInt();
		
		Dijkstra newDijkstra = new Dijkstra();
		Dijkstra.Graph adjMatrix = newDijkstra.new Graph(++numVert);
		
		while(newScan.hasNextInt()) { //while theres integers, add edges to the graph
			adjMatrix.addEdge(newScan.nextInt(), newScan.nextInt(), newScan.nextInt());	
		}
		newDijkstra.dijkstra(adjMatrix.getMatrix(), soVert);
		newScan.close();
	} 
}
