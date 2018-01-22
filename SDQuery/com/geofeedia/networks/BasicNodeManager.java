package com.geofeedia.networks;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

// the choose the node with the fewest leaves
public class BasicNodeManager implements NodeManager{

	private Map<String, NodeStatus> status;
	
	private BasicNodeManager(){
		status = new HashMap<String, NodeStatus>();
	}
	
	@Override
	public void addNode(String ip) {
		if(status.get(ip)==null){
			status.put(ip, new NodeStatus());
		}
	}

	@Override
	public String getNodeIp() {
		if(status.size()==0){
			return null;
		}
		Set<String> ips = status.keySet();
		String result=null;
		int minNumLeaves = Integer.MAX_VALUE;
		for(String ip:ips){
			if(minNumLeaves>status.get(ip).getNumLeaves()){
				minNumLeaves = status.get(ip).getNumLeaves();
				result = ip;
			}
		}
		return result;
	}

	public static BasicNodeManager newInstance(InputStream stream){
		BasicNodeManager manager = new BasicNodeManager();
		
		Scanner scan = new Scanner(stream);
		while(scan.hasNext()){
			String ip = scan.nextLine();
			manager.addNode(ip);
		}
		scan.close();
		return manager;
	}

	@Override
	public void addLeaf(String host, String leafName) {
		status.get(host).addLeaf(leafName);
	}
}
