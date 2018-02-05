import java.util.ArrayList;
import java.util.List;

/**
 * Class Node
 * @author
 * @version 2.0
 * @since 2017-10-26
 */
public class Node implements Cloneable{
	//X coordinate (0 = Northernmost).
	private int x;
	
	//Y coordinate (0 = Westernmost).
	private int y;
	
	//children nodes.
    private List<Node> children = new ArrayList<>();
    
    //parent nodes.
    private Node parent = null;
    
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
    /**
     * Implement cloneable.
     * @return Node 
     */
    public Node clone() throws CloneNotSupportedException {
        return (Node) super.clone();
    }
}
