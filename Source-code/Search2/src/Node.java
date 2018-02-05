import java.util.ArrayList;
import java.util.List;

/**
 * Class Node
 * @author
 * @version 2.0
 * @since 2017-10-26
 */
public class Node {
	//X coordinate (0 = Northernmost).
	private int x;
	
	//Y coordinate (0 = Westernmost).
	private int y;
	
	//children nodes.
    private List<Node> children = new ArrayList<>();
    
    //parent nodes.
    private Node parent = null;
    
    //distance-based heuristic.
    private double heuristic;
    
    //depth.
    private int depth;

    /**
     * Constructor
     * @param x
     * @param y
     */
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Depth getter.
     * @return depth
     */
    public int getDepth() {
		return depth;
	}

    /**
     * Depth setter.
     * @param depth
     */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * Calculate Manhattan distance as heuristic.
	 * @param xD
	 * @param yD
	 */
	public void calManhattan(int xD, int yD) {
    	this.heuristic = Math.abs(x - xD) + Math.abs(y - yD);
    }
    
	/**
	 * Calculate Euclidian distance as heuristic.
	 * @param xD
	 * @param yD
	 */
	public void calEuclidian(int xD, int yD) {
		this.heuristic = Math.sqrt(Math.pow(xD - this.x, 2) + Math.pow(yD - this.y, 2));
	}
	
	/**
	 * 
	 * @param xD
	 * @param yD
	 */
	public void calCombiningH(int xD, int yD) {
		double h1 = Math.sqrt(Math.pow(xD - this.x, 2) + Math.pow(yD - this.y, 2));
		double h2 = Math.abs(x - xD) + Math.abs(y - yD);
		this.heuristic = Math.max(h1, h2);
	}
	
	/**
	 * Getter of Manhattan distance.
	 * @return Manhattan distance
	 */
    public double getHueristic() {
    	return heuristic;
    }

    /**
     * Getter of x-coordinate.
     * @return
     */
	public int getX() {
		return x;
	}
	
	/**
	 * Getter of y-coordinate.
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * Add a single child.
	 * @param child
	 */
    public void addChild(Node child) {
        child.setParent(this);
        this.children.add(child);
        child.setDepth(depth + 1);
    }
    
    /**
     * Override to add a list of children.
     * @param children
     */
    public void addChild(ArrayList<Node> children) {
    	for (Node child: children) {
    		this.addChild(child);
    	}
    }

    /**
     * Getter of children.
     * @return children
     */
    public List<Node> getChildren() {
        return children;
    }

    /**
     * Setter of parent.
     * @param parent
     */
    private void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Getter of parent.
     * @return parent.
     */
    public Node getParent() {
        return parent;
    }
}
