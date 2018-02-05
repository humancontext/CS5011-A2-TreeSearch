import java.util.*;

/**
 * Class Search3_3 with 4-robot searching implemented.
 * @author 
 * @version 1.0
 * @since 2017-10-29
 */
public class Search3_3 {
	
	/**
	 * The main method as an entrance of the program.
	 * @param not used
	 */
	public static void main(String[] args) {
		ArrayList<int[][]> result = fourRobot(map);
		if (result.isEmpty()) {
			System.out.println("Sorry, there is no possible route on this map.");
		}
		else {
			int count = 1;
			for (int[][] step: result) {
				System.out.println("Step: " + count);
				tempMap(map, step);
				count++;
			}
		}
	}
	
	/**
	 * This method initialize the location of I, B and G on the map, and invoke the findPath method.
	 * @param map
	 * @return the answer: a 3D list.
	 */
	static ArrayList<int[][]> fourRobot(char[][] map) {
		//int[][] is the x&y for each step and each robot along the way.
		ArrayList<int[][]> result = new ArrayList<>();
		
		//find the locations of four robots
		int[][] locI = new int[4][2];
		int n = 0;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j] == 'I') {
					locI[n][0] = i;
					locI[n][1] = j;
					n++;
				}
			}
		}

		//find the locations of four victims
		int[][] locB = new int[4][2];
		n = 0;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j] == 'B') {
					locB[n][0] = i;
					locB[n][1] = j;
					n++;
				}
			}
		}

		//find the locations of four safe places
		int[][] locG = new int[4][2];
		n = 0;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j] == 'G') {
					locG[n][0] = i;
					locG[n][1] = j;
					n++;
				}
			}
		}
		result = findPath(map, locI, locB);
		result.addAll(findPath(map, locB, locG));
		return result;
	}
	
	/**
	 * Find the 4-point path between the origin and the destination from the map.
	 * @param map
	 * @param origin
	 * @param destination
	 * @return the answer.
	 */
	static ArrayList<int[][]> findPath(char[][] map, int[][] origin, int[][] destination) {
		ArrayList<int[][]> result = new ArrayList<>();
		
		//a collection of nodes to expand without specific in&out order.
		ArrayList<Node[]> frontiers = new ArrayList<>();
		
		//the locations of the visited nodes.
		ArrayList<Node[]> explored = new ArrayList<>();
		
		//deal with the origin.
		Node[] root = new Node[4];
		for (int i = 0; i < 4; i++) {
			root[i] = new Node(origin[i][0], origin[i][1]);
		}
		frontiers.add(root);
		explored.add(root);
		
		//loop till the frontiers is empty or destination arrived.
		do {
			//check if the frontiers list is empty.
			if (frontiers.isEmpty()) {
				return result;
			}
			
			int len = frontiers.size();
			//expand all the nodes in states in frontier list.
			for (int j = 0; j < len; j++) {
				Node[] t = frontiers.get(j);
				for (int i = 0; i < 4; i++) {
					ArrayList<Node> children = findChildren(map, t[i], explored, i);
					t[i].addChild(children);
				}
				
				//for each combination of the 4 expanded children, judge if they are valid.
				for (int i1 = 0; i1 < t[0].getChildren().size(); i1++) {
					for (int i2 = 0; i2 < t[1].getChildren().size(); i2++) {
						for (int i3 = 0; i3 < t[2].getChildren().size(); i3++) {
							for (int i4 = 0; i4 < t[3].getChildren().size(); i4++) {
								Node[] state = new Node[]{t[0].getChildren().get(i1), t[1].getChildren().get(i2), t[2].getChildren().get(i3), t[3].getChildren().get(i4)};
								//add valid combination to the explored and frontiers list.
								if (checkOverlap(state) && checkTogether(state) && checkLoop(explored, state)) {
									explored.add(state);
									frontiers.add(state);
								}
							}
						}
					}
				}
			}
			
			//remove the expanded nodes from the frontier list.
			for (int i = 0; i < len; i++) {
				frontiers.remove(0);
			}
			
			//check if the destination is arrived.
			for (Node[] state: frontiers) {
				if(checkArrive(destination, state)) {
					Node s0 = state[0];
					Node s1 = state[1];
					Node s2 = state[2];
					Node s3 = state[3];
					while (s0.getParent() != null) {
						int[][] step = new int[4][2];
						step[0][0] = s0.getX();
						step[0][1] = s0.getY();
						step[1][0] = s1.getX();
						step[1][1] = s1.getY();
						step[2][0] = s2.getX();
						step[2][1] = s2.getY();
						step[3][0] = s3.getX();
						step[3][1] = s3.getY();
						s0 = s0.getParent();
						s1 = s1.getParent();
						s2 = s2.getParent();
						s3 = s3.getParent();
						result.add(0,step);
					}
					return result;
				}
			}
			

		} while (true);
	}
				
	/**
	 * Method to check if the current state is already visited.
	 * @param explored is a list of all the explored states
	 * @param state is the current state
	 * @return boolean
	 */	
	static boolean checkLoop(ArrayList<Node[]> explored, Node[] state) {
		for (Node[] exp: explored) {
			int sum = 0;
			for (Node n1: exp) {
				for (Node n2: state) {
					if (n1.getX() == n2.getX() && n1.getY() == n2.getY()) {
						sum++;
					}
				}
			}
			if (sum == 4) {
				return false;
			}
		}
		return true;
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
	static ArrayList<Node> findChildren(char[][] map, Node t, ArrayList<Node[]> explored, int num) {
		ArrayList<Node> children = new ArrayList<>();
		int locX = t.getX();
		int locY = t.getY();
		if (locX < map.length - 1 && map[locX + 1][locY] != 'X') {
			Node thisNode = new Node(locX + 1, locY);
			children.add(thisNode);
		}
		if (locY < map[locX].length - 1 && map[locX][locY + 1] != 'X') {
			Node thisNode = new Node(locX, locY + 1);
			children.add(thisNode);
		}
		if (locX > 0 && map[locX - 1][locY] != 'X') {
			Node thisNode = new Node(locX - 1, locY);
			children.add(thisNode);
			
		}
		if (locY > 0 && map[locX][locY - 1] != 'X') {
			Node thisNode = new Node(locX, locY - 1);
			children.add(thisNode);
		}
		return children;
	}
	
	/**
	 * Check if the expanded nodes are overlapped.
	 * @param n is a list of the nodes.
	 * @return boolean
	 */
	static boolean checkOverlap(Node[] n) {
		for (int i = 0; i < n.length - 1; i++) {
			for (int j = i + 1; j < n.length; j++) {
				if (n[i].getX() == n[j].getX() && n[i].getY() == n[j].getY()) {
					return false;
				}
			}
		}
		return true;	
	}
	
	/**
	 * Check if the robots stay together.
	 * @param n is a list of the nodes
	 * @return boolean
	 */
	static boolean checkTogether(Node[] n) {
		int sum = 0;
		for (int i = 0; i < n.length; i++) {
			for (int j = 0; j < n.length; j++) {
				if (n[i].getX() == n[j].getX()) {
					if (n[i].getY() == n[j].getY() + 1 || n[i].getY() == n[j].getY() - 1) {
						sum++;
					}
				}
				if (n[i].getY() == n[j].getY()) {
					if (n[i].getX() == n[j].getX() + 1 || n[i].getX() == n[j].getX() - 1) {
						sum++;
					}
				}
			}
		}
		if (sum > 5) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Check if the goal is arrived.
	 * @param d the locations of the goal.
	 * @param state is the current state.
	 * @return boolean
	 */
	static boolean checkArrive(int[][] d, Node[] state) {
		int sum = 0;
		for (Node n: state) {
			for (int[] loc: d) {
				if (n.getX() == loc[0] && n.getY() == loc[1]) {
					sum++;
				}
			}
		}
		if (sum == 4) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Drawing the map with current state.
	 * @param map
	 * @param step
	 * @return
	 */
	static char[][] tempMap(char[][] map, int[][] step) {
		//copy the origin map.
		char[][] temp = new char[map.length][map[0].length];
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp[0].length; j++) {
				temp[i][j] = map[i][j];
			}
		}
		for (int[] n: step) {
			temp[n[0]][n[1]] = 'V';
		}
		temp[step[0][0]][step[0][1]] = '1';
		temp[step[1][0]][step[1][1]] = '2';
		temp[step[2][0]][step[2][1]] = '3';
		temp[step[3][0]][step[3][1]] = '4';
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp[0].length; j++) {
				System.out.printf("%3c",temp[i][j]);
			}
			System.out.println();
		}
		System.out.println("==============================");
		return temp;
	}
	
	//storing map.
	static char[][] map = new char [][] {
		{'I', 'I', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'I', 'I', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'X', 'X', 'X', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'X', 'O', 'O', 'O', 'O', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'B', 'B', 'O', 'O', 'O'},
		{'X', 'X', 'O', 'O', 'O', 'B', 'B', 'O', 'O', 'X'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'X', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'X', 'X', 'O', 'O'},
		{'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'G', 'G'},
		{'O', 'O', 'O', 'O', 'X', 'O', 'O', 'O', 'G', 'G'}
		};
}
