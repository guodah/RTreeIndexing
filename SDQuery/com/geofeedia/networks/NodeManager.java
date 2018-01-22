package com.geofeedia.networks;

public interface NodeManager {
	void addNode(String ip);
	String getNodeIp();
	void addLeaf(String host, String leafName);
}
