//COP3503C-20Summer C001 - Assignment 3 - Part 1 - Bellman Ford / Floyd Warshall
//Torres Amanda 
//References-
//4.4 Bellman Ford Algorithm - Single Source Shortest Path - https://www.youtube.com/watch?v=FtN3BYH2Zes
//4.2 All Pairs Shortest Path (Floyd-Warshall) - https://www.youtube.com/watch?v=oNI0rf2P9gE

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

class Algorithms{

	class Graph{ //class for Graph 
		int vertices;
		int adjMatrix[][];
		
		public Graph(int num) { //create graph
			this.vertices = num; //set number of vertices 
			adjMatrix = new int[num+1][num+1]; //create a new Adjacency Matrix with num * num
		}
		
		public void addEdge(int from, int to, int weight) { //add a new edge
			adjMatrix[from][to] = weight; //set to weight
			adjMatrix[to][from] = weight; //since edges are doubly connected must set [to][from] = weight 
		}
		
		public void printMatrix() { //function to print matrix (check if matrix properly created)
			for(int i = 1; i < adjMatrix.length; i++) {
				for(int j = 1; j < adjMatrix.length; j++) {
					System.out.printf("%d ",adjMatrix[i][j]);
				}
				System.out.printf("\n");
			}
		}
		
		public int[][] getMatrix(){ //return the matrix if needed
			return adjMatrix;
		}
	}
	
	
///////Bellman Ford Algorithm////////////////////////////////////////////////////////////////////////////////////////////
	public void bellmanFord(int graph[][], int source) throws IOException {
		int newGraph[][] = new int[graph.length][graph.length];
		copy(graph,newGraph); //copy graph to new matrix
		
		int dist[] = new int[graph.length]; //need distance array to keep values
		int parent[] = new int[graph.length]; //need parent array
		Arrays.fill(dist, (Integer.MAX_VALUE/2));  //fill distance array with MAX_VALUE/2 to prevent overflow and parent with -1
		Arrays.fill(parent, -1); 
		dist[source] = 0; //set both distance and parent of source to 0; 
		parent[source] = 0;
		
		for(int i = 1; i < graph.length-1; i++) {
			for(int u = 1; u < graph.length; u++) {
				for(int v = 1; v < graph.length; v++) {
					if(newGraph[u][v] != (Integer.MAX_VALUE/2)) {
						if(dist[u] + newGraph[u][v] < dist[v]) {
							dist[v] = dist[u] + newGraph[u][v];
							parent[v] = u;
						}
					}
					
				}
			}
		}
		printBellToFile(dist,parent); //print output to file
	}
	
	public void printBellToFile(int distance[], int parent[]) throws IOException { //function to print the Bellman Ford output to a file
		FileWriter outFile = new FileWriter("cop3503-asn3-output-torres-amanda-bf.txt");
		outFile.write(distance.length - 1 + "\n");
		for (int i = 1; i < distance.length ; i++) {
			outFile.write(i + " " + distance[i] + " " + parent[i] + "\n");
		}
		outFile.close();
	}
	
	
///////Floyd Warshall Algorithm////////////////////////////////////////////////////////////////////////////////////////////
	public void floydWarshall(int adjMatrix[][]) throws IOException {
		int matrix[][] = new int[adjMatrix.length][adjMatrix.length];
		copy(adjMatrix,matrix); //copy from adjMatrix to matrix

		for(int k = 1; k < matrix.length; k++) {
			for(int i = 1; i < matrix.length; i++) {
				for(int j = 1; j < matrix.length; j++) {
			        matrix[i][j] = Math.min(matrix[i][j], matrix[i][k] + matrix[k][j]);
				}
			}
		}
		printFloydToFile(matrix);
	}
	
	private void copy(int from[][], int to[][]) { //makes a copy of an array
		int max = (Integer.MAX_VALUE / 2); //temp max value, /2 so no overflow 
		for(int i = 0; i < from.length; i++) {
			for(int j = 0; j < from.length; j++) {
				if(i == j) { //if a diagonal, set to 0
					to[i][j] = from[i][j];
				}
				else if(from[i][j] == 0) { //if 0, path doesnt exist so set to temp val max
					to[i][j] = max;
				}
				else { //else keep value as is and copy to new matrix
					to[i][j] = from[i][j];
				}
			}
		}
	}
		
	public void printFloydToFile(int adjMat[][]) throws IOException { //function to print the Floyd output to a file
		FileWriter outFile = new FileWriter("cop3503-asn3-output-torres-amanda-fw.txt ");
		outFile.write(adjMat.length - 1 + "\n");
		for (int i = 1; i < adjMat.length ; i++) {
			for(int j = 1; j < adjMat.length; j++) {
				outFile.write(adjMat[i][j] + " ");
			}
			outFile.write("\n");
		}
		outFile.close();
	}
	
}


public class bellFloyd{
	public static void main(String[] args) throws IOException 
	{ 
		File inpFile = new File("cop3503-asn3-input.txt"); //create a new file
		Scanner newScan = new Scanner(inpFile); //create new scanner 
		int numVert = newScan.nextInt(); //get # vertexes, edges and source
		int soVert = newScan.nextInt();
		int numEdges = newScan.nextInt();
		
		Algorithms newAlgo = new Algorithms();
		Algorithms.Graph adjMatrix = newAlgo.new Graph(numVert);

		while(newScan.hasNextInt()) { //while theres integers, add edges to the graph
			adjMatrix.addEdge(newScan.nextInt(), newScan.nextInt(), newScan.nextInt());	
		}
		newScan.close();
		
		newAlgo.floydWarshall(adjMatrix.getMatrix());
		newAlgo.bellmanFord(adjMatrix.getMatrix(), soVert);
		
	} 
}
