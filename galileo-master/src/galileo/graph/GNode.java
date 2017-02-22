package galileo.graph;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author sapmitra
 *
 */

public class GNode {
	
	int nodeNum;
	Boolean isLeaf;
	Boolean isRoot;
	List<String> blocks = null;
	GNode parent;
	List<GNode> children = null;
	String path;
	
	public GNode() {
		isLeaf = false;
		isRoot = false;
	}

	public Boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public List<String> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<String> dirs) {
		this.blocks = dirs;
	}
	
	public void addBlocks(String dir) {
		if(blocks == null) {
			blocks = new ArrayList<String>();
		}
		blocks.add(dir);
	}

	public List<GNode> getChildren() {
		return children;
	}

	public void setChildren(List<GNode> children) {
		this.children = children;
	}
	
	public void addChild(GNode child) {
		if(children == null) {
			children = new ArrayList<GNode>();
		}
		children.add(child);
	}
	

	public Boolean getIsRoot() {
		return isRoot;
	}

	public void setIsRoot(Boolean isParent) {
		this.isRoot = isParent;
	}

	public GNode getParent() {
		return parent;
	}

	public void setParent(GNode parent) {
		this.parent = parent;
	}

	public int getNodeNum() {
		return nodeNum;
	}

	public void setNodeNum(int nodeNum) {
		this.nodeNum = nodeNum;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public static GNode getNodeFromID(int id , List<GNode> nodes) {
		
		for(GNode g: nodes) {
			if(g.getNodeNum() == id) {
				return g;
			}
		}
		return null;
	}
	
	
	public int addNewPath(String[] elements,String blockName, int index, int num) {
		/*Whole Path matches but blockname does not*/
		if(index == elements.length && elements[index-1].equals(this.getPath())) {
			this.addBlocks(blockName);
			return -1;
		}
		List<GNode> childNodes = getChildren();
		
		if (childNodes != null && childNodes.size() > 0) {
			for (GNode g : childNodes) {
				if (elements[index].equals(g.getPath())) {
					return g.addNewPath(elements, blockName, index + 1, num);
				}
			}
		}
		/* If no matching child node if found in the previous if condition */
		GNode prevNode = this;
		for(int i = index; i < elements.length; i++) {
			GNode newNode = new GNode();
			num++;
			newNode.setIsLeaf(false);
			if(i == elements.length - 1) {
				newNode.setIsRoot(false);
				newNode.setIsLeaf(true);
				newNode.addBlocks(blockName);
			}
			newNode.setIsRoot(false);
			newNode.setPath(elements[i]);
			newNode.setNodeNum(num);
			
			prevNode.addChild(newNode);
			prevNode.setIsLeaf(false);
			newNode.setParent(prevNode);
			
			prevNode = newNode;
			
		}
		return num;
		
		
	}
	
	
	public TreeInfoString printSubTree() {
		
		String property = "";
		String relations = "";
		String currRelations = "";
		String blocks="";
		property = this.getNodeNum()+","+this.getPath();
		
		if(this.getBlocks() != null && this.getBlocks().size() > 0) {
			for(String blk : this.getBlocks()) {
				blocks+=blk+"&&&";
			}
			
			if(blocks.length() > 3) {
				blocks = "," + blocks;
				blocks = blocks.substring(0, blocks.length()-3);
			}
		}
		
		property+=blocks+"\n";
		
		//relations = this.getNodeNum()+"$";
		
		if(this.getChildren() !=null && this.getChildren().size() > 0) {
			for(GNode g: this.getChildren()) {
				TreeInfoString tempInfo =g.printSubTree();
				property += tempInfo.getProperties();
				relations +=tempInfo.getRelationship();
				currRelations+=g.getNodeNum()+",";
			}
		}
		
		if(currRelations.length() > 1) {
			currRelations = currRelations.substring(0,currRelations.length() - 1);
			relations+= getNodeNum()+"$"+currRelations+"\n";
		}
		
		TreeInfoString info = new TreeInfoString();
		
		info.setProperties(property);
		info.setRelationship(relations);
		
		return info;
	}
	
	
}
