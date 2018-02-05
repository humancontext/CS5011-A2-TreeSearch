import java.util.*;
import javax.management.InvalidAttributeValueException;

/**
 * Class Search2 with best-first and A* strategies implemented.
 * @author 
 * @version 1.0
 * @since 2017-10-26
 */

public class Search2 {
	static int memory = 0;
	static int numState = 0;
	/**
	 * The main method as an entrance of the program.
	 * @param args args[0] indicates the number of using map, args[1] indicates the searching strategy.
	 * @throws InvalidAttributeValueException
	 */
	public static void main(String[] args) throws InvalidAttributeValueException {
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
		switch (args[1]) {
			case "BestFirstM":
				step1 = bestFirst(map, 'I', 'B', 'M');
				step2 = bestFirst(map, 'B', 'G', 'M');
				break;
			case "BestFirstE":
				step1 = bestFirst(map, 'I', 'B', 'E');
				step2 = bestFirst(map, 'B', 'G', 'E');
				break;
			case "BestFirstCombine":
				step1 = bestFirst(map, 'I', 'B', 'C');
				step2 = bestFirst(map, 'B', 'G', 'C');
				break;
			case "A*":
				step1 = AStar(map, 'I', 'B');
				step2 = AStar(map, 'B', 'G');
				break;
			default:
				throw new InvalidAttributeValueException();
		}
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
		System.out.println("Path length: " + (step1.size() + step2.size() - 2));
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
	 * This method implements the breadth-first searching strategy.
	 * @param map
	 * @param origin point to start from
	 * @param destination point to go to
	 * @return result or failure(empty list)
	 */
	static ArrayList<int[]> bestFirst(char[][] map, char origin, char destination, char heu) {
		//int[] is the x&y for each step along the way.
		ArrayList<int[]> result = new ArrayList<>();
		
		//the frontiers, a array list without specific in&out order.
		ArrayList<Node> frontiers = new ArrayList<>();
		
		//the locations of the visited nodes.
		ArrayList<Node> explored = new ArrayList<>();
		
		//find the origin and destination.
		Integer[] locO = findLoc(map, origin);
		int yO = locO[1];
		int xO = locO[0];
		Integer[] locD = findLoc(map, destination);
		int yD = locD[1];
		int xD = locD[0];
		
		//deal with the origin.
		Node root = new Node(xO, yO);
		root.calManhattan(xD, yD);
		frontiers.add(root);
		explored.add(root);
		int count  = 0;
		
		//loop till the frontier queue is empty or destination arrived.
		do {
			//if the frontier is empty, return a empty array list marking failure.
			if (frontiers.isEmpty()) {
				return result;
			}
			
			System.out.println("Step: " + count);
			System.out.println("Frontier size: " + frontiers.size());
			//print current frontiers
			for(Node f: frontiers) {
				System.out.print("[" + f.getX() + ',' + f.getY() + "] ");
			}
			count++;
			System.out.println();
			System.out.println();
			
			//count the number of maximum frontiers stored in memory
			if(memory < frontiers.size()) {
				memory = frontiers.size();
			}
			numState += frontiers.size();
			
			//expand the first node on the top of the stack.
			Node t = frontiers.get(0);
			ArrayList<Node> children = findChildren(map, t, explored);
			explored.addAll(children);
			t.addChild(children);
			
			//put the children in the stack according to the order of Manhattan distance.
			for (Node child: children) {
				switch (heu) {
					case 'M':
						child.calManhattan(xD, yD);
						break;
					case 'E':
						child.calEuclidian(xD, yD);
						break;
					case 'C':
						child.calCombiningH(xD, yD);
						break;
				}
				int index = 0;
				while (index < frontiers.size() && frontiers.get(index).getHueristic() < child.getHueristic()) {
					index++;
				}
				frontiers.add(index, child);
			}
			
			//remove the expanded node from the stack.
			frontiers.remove(t);
			
			//check if the frontiers is arrived.
			for (Node f: frontiers) {
				if (f.getX() == xD && f.getY() == yD) {
					Node p = f;
					do{
						result.add(0, new int[]{p.getX(), p.getY()});
						p =p.getParent();
					}while(p != null);
					return result;
				}
			}
			
			
			
		}while (true);
	}
	
	/**
	 * This method implements the A* searching strategy.
	 * @param map
	 * @param origin
	 * @param destination
	 * @return the steps
	 */
	static ArrayList<int[]> AStar(char[][] map, char origin, char destination) {
		//int[] is the x&y for each step along the way.
		ArrayList<int[]> result = new ArrayList<>();
		
		//a collection of nodes to expand without specific in&out order.
		ArrayList<Node> frontiers = new ArrayList<>();
		
		//the locations of the visited nodes.
		ArrayList<Node> explored = new ArrayList<>();
		
		//find the origin and destination.
		Integer[] locO = findLoc(map, origin);
		int yO = locO[1];
		int xO = locO[0];
		Integer[] locD = findLoc(map, destination);
		int yD = locD[1];
		int xD = locD[0];
		
		//deal with the origin.
		Node root = new Node(xO, yO);
		root.calManhattan(xD, yD);
		frontiers.add(root);
		explored.add(root);
		int count = 0;
		
		//loop till the frontiers is empty or destination arrived.
		do {
			System.out.println("Step: " + count);
			System.out.println("Frontier size: " + frontiers.size());
			//print current frontiers
			for(Node f: frontiers) {
				System.out.print("[" + f.getX() + ',' + f.getY() + "] ");
			}
			count++;
			System.out.println();
			System.out.println();
			
			//count the number of maximum frontiers stored in memory
			if(memory < frontiers.size()) {
				memory = frontiers.size();
			}
			numState += frontiers.size();
			
			//expand the first node with the smallest total length.
			Node t = frontiers.get(0);
			ArrayList<Node> children = findChildren(map, t, explored);
			explored.addAll(children);
			t.addChild(children);
			
			//put the children in the stack according to a increasign order of total cost.
			for(Node child: children) {
				child.calManhattan(xD, yD);
				int index = 0;
				//calculating total cost: f(n) = g(n) + h(n).
				int childHeuristic = (int) (child.getDepth() + child.getHueristic());
				int thisHeuristic = (int) (frontiers.get(index).getDepth() + frontiers.get(index).getHueristic());
				while (index < frontiers.size() && thisHeuristic < childHeuristic){
					thisHeuristic = (int) (frontiers.get(index).getDepth() + frontiers.get(index).getHueristic());
					index++;
				}
				frontiers.add(index, child);
			}
			
			//remove the expanded node from frontiers.
			frontiers.remove(t);
			
			//check if the destination is arrived.
			for (Node f: frontiers) {
				if (f.getX() == xD && f.getY() == yD) {
					Node p = f;
					do{
						result.add(0, new int[]{p.getX(), p.getY()});
						p =p.getParent();
					}while(p != null);
					return result;
				}
			}
			
			//if the stack is empty, return failure.
			if (frontiers.isEmpty()) {
				return result;
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
