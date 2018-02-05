import java.util.*;
import javax.management.InvalidAttributeValueException;

/**
 * Class Search3 with bidirection searching strategy implemented.
 * @author 
 * @version 1.0
 * @since 2017-10-26
 */

public class Search3_1 {
	static int memory = 0;
	static int numState = 0;
	/**
	 * The main method as an entrance of the program.
	 * @param args args[0] indicates the number of using map.
	 * @throws InvalidAttributeValueException
	 * @throws CloneNotSupportedException 
	 */
	public static void main(String[] args) throws InvalidAttributeValueException, CloneNotSupportedException {
		char[][] map;
		switch (args[0]) {
			case "map1": 
				map = map1;
				break;
			case "map2":
				map = map2;
 				break;
			case "map3":
				map = map3;
				break;
			case "map4":
				map = map4;
				break;
			case "map5":
				map = map5;
				break;
			case "map6":
				map = map6;
				break;
			default:
				throw new InvalidAttributeValueException();
		}
		//steps from I to B.
		ArrayList<int[]> step1 = new ArrayList<>();
		//steps from B to G.
		ArrayList<int[]> step2 = new ArrayList<>();
		//choosing strategy.
		long startTime = System.currentTimeMillis();
		step1 = bidirection(map, 'I', 'B');
		step2 = bidirection(map, 'B', 'G');
		long endTime = System.currentTimeMillis();

		//check if it fails.
		if (step1.isEmpty() || step2.isEmpty()) {
			System.out.println("Sorry, there is no possible route on this map.");
		}
		else {
			System.out.println("Original Map:");
			for (int i = 0; i < map.length; i++) {
				for (int j = 0; j < map[i].length; j++) {
					System.out.printf("%3c",map[i][j]);
				}
				System.out.println();
			}
			System.out.println("\nFrom I to B:");
			char[][] route1 = routeMap(map, step1);
			for (int i = 0; i < route1.length; i++) {
				for (int j = 0; j < route1[i].length; j++) {
					System.out.printf("%3c",route1[i][j]);
				}
				System.out.println();
			}
			System.out.println("\nFrom B to G:");
			char[][] route2 = routeMap(map, step2);
			for (int i = 0; i < route2.length; i++) {
				for (int j = 0; j < route2[i].length; j++) {
					System.out.printf("%3c",route2[i][j]);
				}
				System.out.println();
			}
		}
		long searchTime = (endTime - startTime);
		System.out.println("Path length: " + (step1.size() + step2.size() - 4));
		System.out.println("Number of visited states: " + numState);
		System.out.println("Searching time: " + searchTime + "(ms)");
		System.out.println("Maximum amount of frontiers stored: " + memory);
	}

	/**
	 * This method is used to find the location of the target character in the map.
	 * @param map
	 * @param target
	 * @return int[]
	 */
	static Integer[] findLoc(char[][] map, char target) {
		for (int i = 0; i < map.length; i ++) {
			for (int j = 0; j < map[i].length; j++) {
				if(map[i][j] == target) {
					return new Integer[]{i,j};
				}
			}
		}
		return new Integer[]{-1,-1};
	}

	/**
	 * This method implements the A* searching strategy.
	 * @param map
	 * @param origin
	 * @param destination
	 * @return the steps
	 * @throws CloneNotSupportedException 
	 */
	static ArrayList<int[]> bidirection(char[][] map, char origin, char destination) throws CloneNotSupportedException {
		//int[] is the x&y for each step along the way.
		ArrayList<int[]> result = new ArrayList<>();
		
		//a collection of nodes to expand without specific in&out order.
		ArrayList<Node> forwardFrontiers = new ArrayList<>();
		ArrayList<Node> backwardFrontiers = new ArrayList<>();
		
		//the locations of the visited nodes.
		ArrayList<Node> forwardExplored = new ArrayList<>();
		ArrayList<Node> backwardExplored = new ArrayList<>();
		
		//find the origin and destination.
		Integer[] locO = findLoc(map, origin);
		int yO = locO[1];
		int xO = locO[0];
		Integer[] locD = findLoc(map, destination);
		int yD = locD[1];
		int xD = locD[0];
		
		//deal with the origin and destination.
		Node head = new Node(xO, yO);
		forwardFrontiers.add(head);
		forwardExplored.add(head);
		Node tail = new Node(xD, yD);
		backwardFrontiers.add(tail);
		backwardExplored.add(tail);
		int count = 0;
		
		//loop till the frontier queue is empty or destination arrived.
		do {
			//if the frontier is empty, return a empty array list marking failure.
			if (forwardFrontiers.isEmpty() || backwardFrontiers.isEmpty()) {
				return result;
			}
			
			System.out.println("Step: " + count);
			System.out.println("Frontier size: " + (forwardFrontiers.size() + backwardFrontiers.size()));
			//print current frontiers
			System.out.println("Forward Frontiers");
			for(Node f: forwardFrontiers) {
				System.out.print("[" + f.getX() + ',' + f.getY() + "] ");
			}
			System.out.println();
			System.out.println("Backward Frontiers");
			for(Node f: backwardFrontiers) {
				System.out.print("[" + f.getX() + ',' + f.getY() + "] ");
			}
			count++;
			System.out.println();
			System.out.println();
			
			//count the number of maximum frontiers stored in memory
			if(memory < forwardFrontiers.size() + backwardFrontiers.size()) {
				memory = forwardFrontiers.size() + backwardFrontiers.size();
			}
			numState += forwardFrontiers.size() + backwardFrontiers.size();
			
			
			//expand the forward tree.
			int lenf = forwardFrontiers.size();
			for (int i = 0; i < lenf; i++) {
				Node t = forwardFrontiers.get(i);
				ArrayList<Node> children = findChildren(map, t, forwardExplored);
				forwardExplored.addAll(children);
				t.addChild(children);
				forwardFrontiers.addAll(children);
			}
			
			//remove the expanded forward-nodes from frontier.
			ArrayList<Node> lastFF = new ArrayList<>();
			for (int i = 0; i < lenf; i++) {
				lastFF.add(forwardFrontiers.get(0));
				forwardFrontiers.remove(0);
			}
			
			//expand the backward tree.
			int lenb = backwardFrontiers.size();
			for (int i = 0; i < lenb; i++) {
				Node t = backwardFrontiers.get(i);
				ArrayList<Node> children = findChildren(map, t, backwardExplored);
				backwardExplored.addAll(children);
				t.addChild(children);
				backwardFrontiers.addAll(children);
			}
			
			//remove the expanded backward-nodes from frontier.
			ArrayList<Node> lastBF = new ArrayList<>();
			for (int i = 0; i < lenb; i++) {
				lastBF.add(backwardFrontiers.get(0));
				backwardFrontiers.remove(0);
			}
			
			//check if the destination is arrived.
			ArrayList<Node> forwardNodes = new ArrayList<>();
			forwardNodes.addAll(forwardFrontiers);
			forwardNodes.addAll(lastFF);
			ArrayList<Node> backwardNodes = new ArrayList<>();
			backwardNodes.addAll(backwardFrontiers);
			backwardNodes.addAll(lastBF);
			
			for (Node nf: forwardNodes) {
				for (Node nb: backwardNodes) {
					if (nf.getX() == nb.getX() && nf.getY() == nb.getY()) {
						Node n = nf.clone();
						do {
							result.add(0, new int[]{n.getX(), n.getY()});
							n =n.getParent();
						} while(n != null);
						n = nb.clone();
						do {
							result.add(new int[]{n.getX(), n.getY()});
							n = n.getParent();
						} while(n != null);
						return result;
					}
				}
			}
		}while (true);
	}
		
	/**
	 * Method to check if the current node is already visited.
	 * @param explored a list of all the explored nodes
	 * @param x
	 * @param y
	 * @return boolean
	 */	
	static boolean checkLoop(ArrayList<Node> explored, int x, int y) {
		for (Node item: explored) {
			if (x == item.getX() && y == item.getY()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Method to generate the route map with given result.
	 * @param map
	 * @param result
	 * @return a 2D char array.
	 */
	static char[][] routeMap(char[][] map, ArrayList<int[]> result) {
		//copy the origin map.
		char[][] route = new char[map.length][map[0].length];
		for (int i = 0; i < route.length; i++) {
			for (int j = 0; j < route[0].length; j++) {
				route[i][j] = map[i][j];
			}
		}
		
		//replace the symbols by arrows along the route.
		for (int i = 0; i < result.size(); i++) {
			char current = route[result.get(i)[0]][result.get(i)[1]];
			if (current != 'B' && current != 'I' && current != 'G') {
				if(result.get(i)[0] == result.get(i+1)[0]) {
					if(result.get(i)[1] == result.get(i + 1)[1] + 1) {
						route[result.get(i)[0]][result.get(i)[1]] = '⇦';
					}
					if(result.get(i)[1] == result.get(i + 1)[1] - 1) {
						route[result.get(i)[0]][result.get(i)[1]] = '⇨';
					}
				}
				if(result.get(i)[1] == result.get(i + 1)[1]) {
					if(result.get(i)[0] == result.get(i + 1)[0] + 1) {
						route[result.get(i)[0]][result.get(i)[1]] = '⇧';
					}
					if(result.get(i)[0] == result.get(i + 1)[0] - 1) {
						route[result.get(i)[0]][result.get(i)[1]] = '⇩';
					}
				}
			}
		}
		return route;
	}
	
	
	/**
	 * Find all the valid children for a given node,
	 * the child must be inside the boundary of map, not obstacle('X') and unexplored.
	 * The priorities are south, east, north, west.
	 * @param map
	 * @param t the parent node
	 * @param explored
	 * @return a list of nodes
	 */
	static ArrayList<Node> findChildren(char[][] map, Node t, ArrayList<Node> explored) {
		ArrayList<Node> children = new ArrayList<>();
		int locX = t.getX();
		int locY = t.getY();
		if (locX < map.length - 1 && map[locX + 1][locY] != 'X' && checkLoop(explored, locX + 1, locY)) {
			Node thisNode = new Node(locX + 1, locY);
			children.add(thisNode);
		}
		if (locY < map[locX].length - 1 && map[locX][locY + 1] != 'X' && checkLoop(explored, locX, locY + 1)) {
			Node thisNode = new Node(locX, locY + 1);
			children.add(thisNode);
		}
		if (locX > 0 && map[locX - 1][locY] != 'X' && checkLoop(explored, locX - 1, locY)) {
			Node thisNode = new Node(locX - 1, locY);
			children.add(thisNode);
			
		}
		if (locY > 0 && map[locX][locY - 1] != 'X' && checkLoop(explored, locX, locY - 1)) {
			Node thisNode = new Node(locX, locY - 1);
			children.add(thisNode);
		}
		return children;
	}
	
	//storing all the maps.
	static char[][] map1 = new char [][] {
		{'I', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'X', 'O', 'O', 'X', 'O'},
		{'O', 'O', 'O', 'X', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'X', 'X', 'O', 'X', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'B', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'G'},
		};


	static char[][] map2 = new char [][] {
		{'O', 'O', 'X', 'O', 'O', 'O', 'O', 'O', 'O', 'X'},
		{'O', 'X', 'O', 'X', 'O', 'O', 'B', 'O', 'O', 'X'},
		{'O', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'X', 'X', 'X', 'X', 'O', 'O', 'O'},
		{'O', 'O', 'X', 'X', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'X', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'X', 'X', 'X', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'G', 'O', 'I', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
		};


	static char[][] map3 = new char [][] {
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'X', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'X', 'O', 'O', 'O'},
		{'O', 'I', 'O', 'O', 'O', 'X', 'X', 'O', 'B', 'O'},
		{'O', 'O', 'O', 'X', 'X', 'X', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'X', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'X', 'X', 'X', 'X', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'G', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
		};
		
	static char[][] map4 = new char [][] {
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'X'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'X', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'X', 'X', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'X', 'O', 'O', 'O'},
		{'O', 'I', 'O', 'O', 'O', 'X', 'X', 'O', 'B', 'O'},
		{'O', 'O', 'O', 'X', 'X', 'X', 'O', 'X', 'X', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'X', 'X', 'O', 'O', 'X'},
		{'O', 'O', 'X', 'X', 'X', 'X', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'G', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
		};
		
	static char[][] map5 = new char [][] {
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'B', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'X', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'X', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'X', 'X', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'X', 'X', 'X', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'X', 'X', 'O', 'O', 'X'},
		{'O', 'O', 'X', 'X', 'X', 'X', 'O', 'O', 'O', 'O'},
		{'O', 'X', 'O', 'O', 'G', 'O', 'X', 'O', 'I', 'O'},
		{'X', 'O', 'O', 'O', 'O', 'O', 'O', 'X', 'O', 'O'}
		};
	
	static char[][] map6 = new char [][] {
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'X'},
		{'O', 'O', 'B', 'O', 'O', 'X', 'X', 'O', 'O', 'O'},
		{'O', 'X', 'X', 'X', 'O', 'O', 'X', 'O', 'O', 'I'},
		{'O', 'X', 'X', 'X', 'O', 'O', 'X', 'X', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'X', 'X', 'X', 'X', 'O'},
		{'X', 'O', 'O', 'X', 'X', 'X', 'X', 'O', 'O', 'O'},
		{'X', 'X', 'O', 'O', 'O', 'X', 'X', 'O', 'X', 'X'},
		{'O', 'O', 'O', 'X', 'X', 'X', 'O', 'X', 'O', 'X'},
		{'O', 'X', 'X', 'X', 'G', 'O', 'X', 'X', 'O', 'X'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'X', 'O', 'O'}
		};
		

}
