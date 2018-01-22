package com.geofeedia.networks;

import java.util.HashMap;
import java.util.Map;

public class NodeStatus {
	private Map<String, Long> leaves;
	public NodeStatus(){
		leaves = new HashMap<String, Long>();
	}
	
	public void increaseLeafSize(String name, long addition){
		long original = 0;
		if(leaves.get(name)!=null){
			original = leaves.get(name);
		}
		
		leaves.put(name, original+addition);
	}
	
	public long getLeafSize(String name){
		if(leaves.get(name)!=null){
			return leaves.get(name);
		}else{
			return 0;
		}
	}
	
	public int getNumLeaves(){
		return leaves.size();
	}

	public void addLeaf(String leafName) {
		leaves.put(leafName, 0L);
	}
	
}
