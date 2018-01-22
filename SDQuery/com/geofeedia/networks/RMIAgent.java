package com.geofeedia.networks;


import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.Random;

import com.geofeedia.rtree.util.IlegalArgumentsException;
import com.geofeedia.rtree.util.LocalFileSystemUtil;

import com.geofeedia.sdquerry.rtree.leaf.RMILeaf;
import com.geofeedia.sdquerry.rtree.leaf.RStarSplitter;

public class RMIAgent extends UnicastRemoteObject implements RMIAgentInterface{
	private String path;
	public RMIAgent(String path) throws RemoteException{
		this.path = path;
	}
	
	public String bind() throws RemoteException{
		String name = getName();
		try {
			RMILeaf rmiLeaf = new RMILeaf(path+"/"+name, new RStarSplitter());
			Naming.rebind("rmi://localhost/"+name, rmiLeaf);
			return name;
		} catch (IlegalArgumentsException|MalformedURLException e) {
			throw new RuntimeException("Unable to create an RMI leaf", e);
		} 
	}

	private String getName() {
		Random random = new Random();
		String name;
		do{
			name = "L"+random.nextInt(100000000);
		}while(LocalFileSystemUtil.find(path, name));
		return name;
	}
	
	public static void main(String args[]) throws IOException, NotBoundException {
		System.setSecurityManager(new RMISecurityManager());
		
		Properties properties = new Properties();
		properties.load(RMIAgent.class.getResourceAsStream("rmi.properties"));
		String path = properties.getProperty("rmi_leaves_path");
//		System.out.println(System.getProperty("java.security.policy"));
		RMIAgent agent = new RMIAgent(path);
		String name = properties.getProperty("rmi_agent_name");
		Naming.rebind("rmi://localhost/"+name, agent);
//		RMIAgentInterface agent_ver2 = (RMIAgent) Naming.lookup("rmi://localhost/"+name);
//		System.out.println("bound and found");
	}
}
