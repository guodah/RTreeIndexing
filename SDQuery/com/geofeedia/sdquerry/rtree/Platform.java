package com.geofeedia.sdquerry.rtree;

import java.io.FileInputStream;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.geofeedia.networks.NodeManager;
import com.geofeedia.sdquerry.rtree.leaf.DataConnection;
import com.geofeedia.sdquerry.rtree.leaf.LeafStub;
import com.geofeedia.sdquerry.rtree.leaf.factory.LeafStubFactory;

public class Platform {
	
	public static final int LATITUDE_SCALE_FACTOR = 10000;
	public static final int LONGITUDE_SCALE_FACTOR = 10000; 

	
	private static SystemUtil system;
	private static RTreeDirectory directory;
	private static final float leafMinUtil;
	private static final int leafCapacity;
	private static final int dimensions;
	private static final long baseTime;
	private static Properties properties;
	private static String rmiAgentName;
	private static final int tempLeafCapacity;
	
	private static NodeManager nodeManager;
	
//	private static 
	private static final Map<LeafType, Class<?extends DataConnection>> dataConns;
	static{
		properties = new Properties();
		try {
			properties.load(
					Platform.class.getResourceAsStream("platform.properties"));
		} catch (IOException e) {
			throw new RuntimeException("Unable to find platform.properties");
		}
		
		String directoryClass = properties.getProperty("directory");
		leafMinUtil = Float.parseFloat(properties.getProperty("leaf_min_util"));
		leafCapacity = Integer.parseInt(properties.getProperty("leaf_capacity"));
		dimensions = Integer.parseInt(properties.getProperty("dimensions"));
		baseTime = Long.parseLong(properties.getProperty("base_time"));
//		leafType = LeafType.valueOf(properties.getProperty("leaf_type"));
		dataConns = new HashMap<LeafType, Class<?extends DataConnection>>();
		rmiAgentName = properties.getProperty("rmi_agent_name");
		tempLeafCapacity = Integer.parseInt(properties.getProperty("temp_rtree_leaf_capacity"));
		try {
			String dataConn = properties.getProperty("local_leaf_data_conn");
			dataConns.put(LeafType.LOCAL, (Class<? extends DataConnection>) Class.forName(dataConn));
			dataConn = properties.getProperty("jvm_leaf_data_conn");
			dataConns.put(LeafType.JVM, (Class<? extends DataConnection>) Class.forName(dataConn));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		try {
			system = createSystemUtils(properties);
			directory = (RTreeDirectory) Class.forName(directoryClass).
					getConstructor(LeafType.class).newInstance(LeafType.LOCAL);
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | IllegalArgumentException | InvocationTargetException 
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static LeafStub checkOutLeafStub(LeafType type){
		return system.checkOutLeafStub(type, dimensions, leafCapacity, leafMinUtil);
	}
	
	public static String getRMIAgentName(){
		return rmiAgentName;
	}
	
	private static SystemUtil createSystemUtils(Properties properties) 
			throws NoSuchMethodException, SecurityException, ClassNotFoundException, 
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			
		String localLeafFactoryClass = properties.getProperty("local_leaf_factory");
		String localLeafFactoryArgs[] = properties.getProperty("local_leaf_factory_args").split(",");
		
		LeafStubFactory localLeafFactory = (LeafStubFactory) Class.forName(localLeafFactoryClass).getConstructor(List.class).newInstance(
				Arrays.asList(localLeafFactoryArgs));

		String jvmLeafFactoryClass = properties.getProperty("jvm_leaf_factory");
		String jvmLeafFactoryArgs[] = properties.getProperty("jvm_leaf_factory_args").split(",");
		LeafStubFactory jvmLeafFactory = (LeafStubFactory) Class.forName(jvmLeafFactoryClass).getConstructor(List.class).newInstance(
				Arrays.asList(jvmLeafFactoryArgs));

		String systemClass = properties.getProperty("system");
		system = (SystemUtil) Class.forName(systemClass).getConstructor().newInstance();
		system.addLeafStubFactory(LeafType.LOCAL, localLeafFactory);
		system.addLeafStubFactory(LeafType.JVM, jvmLeafFactory);
		return system;
	}
/*
	private static SystemUtil createSystemUtils(LeafType type) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, 
				InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		SystemUtil system = null;
		switch(type){
		case LOCAL:
			String localLeavesPath = properties.getProperty("local_leaves_path");
			String systemClass = properties.getProperty("system");
			system = (SystemUtil) Class.forName(systemClass).getConstructor(String.class).newInstance(localLeavesPath);
			break;
		case JVM:
			throw new RuntimeException("unimplemented");
		}
		return system;		
	}
*/
	
	public static void startNodeManager(String ips_file){
		String nodeManagerClass = properties.getProperty("node_manager");
		try {
			Method creator = Class.forName(nodeManagerClass).getMethod("newInstance", InputStream.class);
			nodeManager = (NodeManager) creator.invoke(null, new FileInputStream(ips_file));
		} catch (NoSuchMethodException | SecurityException
				| ClassNotFoundException | IllegalAccessException | 
				IllegalArgumentException | InvocationTargetException | 
				FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static int getTempLeafCapacity(){
		return tempLeafCapacity;
	}
	
	public static float getMinUtilization() {
		return leafMinUtil;
	}
	
	public static int getLeafCapacity(){
		return leafCapacity;
	}
	
	public static int getDimensions(){
		return dimensions;
	}
	
	public static long getBaseTime(){
		return baseTime;
	}
	
	public static void setDirectory(RTreeDirectory directory){
		system.setDirectory(directory);
	}
	
	public static DataConnection getDataConn(LeafType type, String path){
		Class<? extends DataConnection> dataConnClass = dataConns.get(type);
		try {
			Constructor<? extends DataConnection> constructor = dataConnClass.getConstructor(String.class);
			DataConnection dataConn = constructor.newInstance(path);
			return dataConn;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	public static String getRMIHost(){
//		return "rmi://localhost";
//		return "rmi://ec2-54-68-253-125.us-west-2.compute.amazonaws.com";
		return nodeManager.getNodeIp();
	}

	public static void addLeaf(String host, String leafName) {
		nodeManager.addLeaf(host, leafName);
	}

	public static LeafStub checkOutLeafStub(LeafType type, int leafCapacity) {
		if(leafCapacity<=0){
			throw new RuntimeException("leafCapacity<=0 in Platform.checkOutLeafStub(LeafType type, int leafCapacity");
		}
		return system.checkOutLeafStub(type, dimensions, leafCapacity, leafMinUtil);
	}
}
