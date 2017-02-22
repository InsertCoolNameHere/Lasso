package galileo.graph;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import galileo.fs.GBlockInfo;



/**
 * 
 * @author sapmitra
 *
 */
public class GTree {
	
	private List<GNode> nodes ;
	
	private int numNodes=0;
	private GNode rootNode;
	private String fsName;
	private Map<String,List<GBlockInfo>> pathToBlockMap;
	
	
	
	public void addNewPath(String path, String block, int blockSize) {
		String[] elements = path.split(File.separator);
		int newNumNodes = rootNode.addNewPath(elements,block, 0, numNodes);
		if(newNumNodes == -1) {
			List<GBlockInfo> blks = pathToBlockMap.get(path);
			GBlockInfo gbl = new GBlockInfo();
			gbl.setBlockName(block);
			gbl.setBlockSize(blockSize);
			blks.add(gbl);
			pathToBlockMap.put(path,blks);
			
		} else if(newNumNodes > numNodes) {
			List<GBlockInfo> blks = new ArrayList<GBlockInfo>();
			GBlockInfo gbl = new GBlockInfo();
			gbl.setBlockName(block);
			gbl.setBlockSize(blockSize);
			blks.add(gbl);
			pathToBlockMap.put(path,blks);
			numNodes = newNumNodes;
		} /*else {
			List<String> blks = pathToBlockMap.get(path);
			blks.add(block);
			pathToBlockMap.put(path,blks);
		}*/
		
	}	
	
	
	
	
	public String logTree() {

		String op="";
		String op1 = fsName + "\n";
		String op2 = "<<\n";
		String op4 = "[[\n";
		String op3 = ">>\n";
		String op5 = "]]\n";
		
		TreeInfoString pathStr = null;
		
		GNode root = getRootNode();
		
		if(root !=null) {
			pathStr = root.printSubTree();
		}
		
		
		if(pathStr != null) {
			op = op1 + op2 + pathStr.getProperties() + op3 + op4 + pathStr.getRelationship() + op5 + "$$" + "\n";
			//System.out.println(op);
		}
		return op;
	}
		
	
	public static Map<String, GTree> populateFSTreesFromLog(File log) throws IOException {
		
		Map<String, GTree> fsToTreeMap = new HashMap<String, GTree>();
		FileInputStream fs = new FileInputStream(log);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		List<String> lines = new ArrayList<String>();
		String ops="";
		
		while((ops=br.readLine()) != null) {
			if(ops.length() > 0)
				lines.add(ops);
		}
		br.close();
		
		/* Extracting one tree at a time*/
		if(lines.size() > 0){
			while(lines != null && lines.indexOf("$$") > 0){
				List<String> treeInfo = new ArrayList<String>();
				treeInfo.addAll(lines.subList(0, lines.indexOf("$$")));
				
				if(treeInfo.size() > 0){
					GTree tree = extractTree(treeInfo);
					if(tree != null && tree.getFsName() != null) {
						fsToTreeMap.put(tree.getFsName(), tree);
					}
				}
				
				lines = lines.subList(lines.indexOf("$$")+1, lines.size());
				
				
			}
		}
		return fsToTreeMap;
		
	}
	
	private static GTree extractTree(List<String> treeInfo) {
		
		GTree tree = null;
		if(treeInfo.indexOf("<<")>0 && treeInfo.indexOf("[[")>0 && treeInfo.indexOf(">>")>treeInfo.indexOf("<<") && treeInfo.indexOf("]]")>treeInfo.indexOf("[[")) {
			
			tree = new GTree();
			
			String fsName = treeInfo.get(0);
			
			tree.setFsName(fsName);
			
			List<String> nodeInfos = treeInfo.subList(treeInfo.indexOf("<<")+1, treeInfo.indexOf(">>"));
			List<String> inheritanceInfo = treeInfo.subList(treeInfo.indexOf("[[")+1, treeInfo.indexOf("]]"));
			
			if(nodeInfos.size() > 0) {
				List<GNode> gnodes = extractNodeInfo(nodeInfos);
				
				if(inheritanceInfo.size() > 0) {
					
					for(String in : inheritanceInfo) {
						
						String[] data = in.split("\\$");
						if(data.length == 2) {
							int parentID = Integer.valueOf(data[0]);
							GNode parent = GNode.getNodeFromID(parentID, gnodes);
							
							String[] children = data[1].split(",");
							
							for(String c : children) {
								GNode child = GNode.getNodeFromID(Integer.valueOf(c), gnodes);
								child.setParent(parent);
								parent.addChild(child);
							}
						}
						
					}
				}
				if(gnodes.size() > 0) {
					tree.setNodes(gnodes);
					gnodes.get(0).setIsRoot(true);
					tree.setRootNode(gnodes.get(0));
				}
				
				
			}
			
		}
		return tree;
		
	}
	
	private static List<GNode> extractNodeInfo(List<String> nodeInfos) {
		List<GNode> nodes = new ArrayList<GNode>();
		for(String line : nodeInfos) {
			if(line != null && line.length() >0) {
				String[] infos = line.split(",");
				if(infos.length >= 2) {
					GNode g = new GNode();
					g.setNodeNum(Integer.valueOf(infos[0].trim()));
					g.setPath(infos[1].trim());
					
					if(infos.length == 3) {
						String temp = infos[2].trim();
						String[] blks = temp.split("&&&");
						
						g.setBlocks(Arrays.asList(blks));
						g.setIsLeaf(true);
					}
					
					nodes.add(g);
				}
			}
		}
		return nodes;
	}

	public List<GNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<GNode> nodes) {
		this.nodes = nodes;
	}
	
	public void addNode(GNode node) {
		if(this.nodes == null) {
			this.nodes= new ArrayList<GNode>(); 
		}
		this.nodes.add(node);
	}

	public GNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(GNode parent) {
		this.rootNode = parent;
	}

	public String getFsName() {
		return fsName;
	}

	public void setFsName(String fsName) {
		this.fsName = fsName;
	}



	public Map<String, List<GBlockInfo>> getPathToBlockMap() {
		return pathToBlockMap;
	}



	public void setPathToBlockMap(Map<String, List<GBlockInfo>> pathToBlockMap) {
		this.pathToBlockMap = pathToBlockMap;
	}



	public int getNumNodes() {
		return numNodes;
	}



	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
