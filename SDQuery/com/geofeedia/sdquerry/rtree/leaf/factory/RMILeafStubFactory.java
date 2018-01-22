package com.geofeedia.sdquerry.rtree.leaf.factory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Random;

import com.geofeedia.networks.RMIAgent;
import com.geofeedia.networks.RMIAgentInterface;
import com.geofeedia.rtree.util.IlegalArgumentsException;
import com.geofeedia.sdquerry.rtree.LeafType;
import com.geofeedia.sdquerry.rtree.Platform;
import com.geofeedia.sdquerry.rtree.leaf.LeafStub;
import com.geofeedia.sdquerry.rtree.leaf.RMILeaf;
import com.geofeedia.sdquerry.rtree.leaf.RMILeafInterface;
import com.geofeedia.sdquerry.rtree.leaf.RMILeafStub;
import com.geofeedia.sdquerry.rtree.leaf.RStarSplitter;
import com.geofeedia.sdquerry.rtree.leaf.Splitter;

public class RMILeafStubFactory extends JVMLeafStubFactory{
	private String path;
	public RMILeafStubFactory(List<String> args){
		if(args==null || args.size()!=1){
			throw new RuntimeException("Incorrect arguments for RMILeafStubFactory");
		}
		this.path = args.get(0);
	}
	
	@Override
	public LeafStub checkOutLeafStub(LeafType type, int dimension,
			int capacity, float minUtil) {
		if(!type.equals(LeafType.JVM)){
			return null;
		}
		
		try {
			String host =Platform.getRMIHost();
			String agentName = Platform.getRMIAgentName();
			System.out.println("Connecting to "+host);
			RMIAgentInterface rmiAgent = (RMIAgentInterface) 
						Naming.lookup("rmi://"+host+"/"+agentName);
						
			String leafName = rmiAgent.bind();
			RMILeafInterface leaf = (RMILeafInterface) Naming.lookup("rmi://"+host+"/"+leafName);
			
			Platform.addLeaf(host, leafName);
			
			LeafStub stub = new RMILeafStub(dimension, capacity, minUtil, leaf);
			notifyWatchers(stub);
			return stub;
		} catch (MalformedURLException | RemoteException | 
				NotBoundException e) {
			throw new RuntimeException("failed to build an RMI leaf", e);
		}
	}

	private String startRMILeaf(String url) 
			throws MalformedURLException, RemoteException, IlegalArgumentsException {
		
		Splitter splitter = new RStarSplitter();
		Random random = new Random();
		String name;
		boolean bound=false;
		do{
			name = "L"+random.nextInt(100000000);
			RMILeaf leaf = new RMILeaf(path+"/"+name, splitter);
			try{
				Naming.bind(url+"/"+name, leaf);
			}catch(AlreadyBoundException e){
				
			}
			bound = true;
		}while(!bound);
		return name;
	}

}
