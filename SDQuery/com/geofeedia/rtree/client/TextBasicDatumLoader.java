package com.geofeedia.rtree.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import com.geofeedia.rtree.util.CollectionUtils;
import com.geofeedia.rtree.util.CommandParser;
import com.geofeedia.rtree.util.TimeUtil;
import com.geofeedia.sdquerry.datatypes.BasicDatumBuilder;
import com.geofeedia.sdquerry.datatypes.Datum;
import com.geofeedia.sdquerry.rtree.BasicRTreeDirectory;
import com.geofeedia.sdquerry.rtree.BinaryRTree;
import com.geofeedia.sdquerry.rtree.LeafType;
import com.geofeedia.sdquerry.rtree.Platform;
import com.geofeedia.sdquerry.rtree.RTreeDirectory;
import com.geofeedia.sdquerry.rtree.leaf.LeafStub;
import com.geofeedia.sdquerry.rtree.leaf.MBB;

public class TextBasicDatumLoader implements TextDatumLoader{
	private static final int ID = 0;
	private static final int SOURCE = 1;
	private static final int DESCRIPTION = 2;
	private static final int TIME = 3;
	private static final int LATITUDE = 5;
	private static final int LONGITUDE = 4;
	
	@Override
	public Datum loadDatum(String text) {
		StringTokenizer st = new StringTokenizer(text, "\t");
		int field=0;
		BasicDatumBuilder builder = new BasicDatumBuilder();
		builder.createNewDatum();
		while(st.hasMoreTokens()){
			String token = st.nextToken();
			switch(field){
			case TIME:
				long time = TimeUtil.parse(token).getTime();
				builder.setTime((time-Platform.getBaseTime())/1000);
				break;
			case LATITUDE:
				double latitude = Double.parseDouble(token);
				builder.setLatitude(Double.valueOf(latitude*Platform.LATITUDE_SCALE_FACTOR).longValue());
				break;
			case LONGITUDE:
				double longitude = Double.parseDouble(token);
				builder.setLongitude(Double.valueOf(longitude*Platform.LONGITUDE_SCALE_FACTOR).longValue());
				break;
			case ID:
				builder.setId(Long.parseLong(token));
				break;
			}
			field++;
		}
		return builder.build();
	}
	
	public static void main(String args[]) throws IOException{

		
		Map<String, String> arguments = CommandParser.parse(args);
		if(arguments==null){
			CommandParser.showUsage();
			System.exit(1);
		}

		List<Datum> data = loadData(arguments);
		RTreeDirectory directory = new BasicRTreeDirectory(LeafType.LOCAL);
		Platform.setDirectory(directory);
		BinaryRTree rtree = new BinaryRTree(LeafType.LOCAL);
		directory.setRTree(rtree);

		int numOfBatches = 100;
/*		int batchSize = data.size()/numOfBatches;
		for(int i=0;i<numOfBatches;i++){
			List<Datum> subList = CollectionUtils.buildList(data, 
					i*batchSize, ((i+1)*batchSize-1)>=data.size()?
									data.size():
									((i+1)*batchSize-1)
			);
			directory.insert(subList);
		}
*/
		List<List<Datum>> rows = CollectionUtils.intervalSample(data, numOfBatches);
		int count=0;
		for(List<Datum> row:rows){
			System.out.println("Batch "+(count++));
			directory.insert(row);
		}
/*		
		System.out.println("Data loaded in RAM");
		BinaryRTree tree = storeData(data, arguments);
		tree.check();
		System.out.println("Average volume overlap is "+tree.measureOverlap());
*/		
/*		
		long [] lows = new long[]{Long.MIN_VALUE,Long.MIN_VALUE,Long.MIN_VALUE};
		long [] highs = new long[]{Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE};
		MBB mbb = new MBB(highs, lows);
		Collection<Datum> treeData = tree.search(mbb);
		
		for(Datum datum:treeData){
			if(!data.contains(datum)){
				throw new RuntimeException("data not found");
			}
		}
		
		System.out.printf("(%d,%d)\n", data.size(),treeData.size());
		
		LeafStub stub = tree.findLeafStubContains(11536186L);
		System.out.println(stub.getPathFromRoot());
*/		
		rtree.check();
		validateSearch(rtree, data);
		
	}

	private static void validateSearch(BinaryRTree tree, List<Datum> data) {
		MBB mbb = getCenter(data);
		
		List<MBB> cubes = split3d(mbb, 10);
		int count=0;
		for(MBB cube:cubes){
			validateSearch(cube, tree, data);
			System.out.println((count++)+" validated");
			if(count==927){
				System.out.println();
			}
		}
	}

	private static MBB getCenter(List<Datum> data) {
		double mean[] = new double[3];
		
		for(int i=0;i<data.size();i++){
			long coords[] = data.get(i).getCoordinates();
			mean[0]+=coords[0];
			mean[1]+=coords[1];
			mean[2]+=coords[2];
		}
		mean[0]/=data.size();
		mean[1]/=data.size();
		mean[2]/=data.size();
		
		double std[] = new double[3];
		for(int i=0;i<data.size();i++){
			long coords[] = data.get(i).getCoordinates();
			std[0]+=Math.pow(coords[0]-mean[0],2);
			std[1]+=Math.pow(coords[1]-mean[1],2);
			std[2]+=Math.pow(coords[2]-mean[2],2);
		}
		
		std[0]= Math.sqrt(std[0]);
		std[1]= Math.sqrt(std[1]);
		std[2]= Math.sqrt(std[2]);
		
		long lows[] = new long[]{(long)(mean[0]-3*std[0]),
				(long)(mean[1]-3*std[1]),
				(long)(mean[2]-3*std[2])};
		long highs[] = new long[]{(long)(mean[0]+3*std[0]),
				(long)(mean[1]+3*std[1]),
				(long)(mean[2]+3*std[2])};
		
		return new MBB(highs, lows);
	}

	private static void validateSearch(MBB cube, BinaryRTree tree,
			List<Datum> data) {
		Set<Datum> listData = search(data, cube);
		Collection<Datum> treeData = tree.search(cube);
		System.out.println("Reaching to "+tree.searchForLeaves(cube).size()+" leaves");
		
		
		if(treeData.size()!=listData.size()){
			throw new RuntimeException("Searches return different sizes.");
		}
		
		for(Datum datum:treeData){
			if(!listData.contains(datum)){
				throw new RuntimeException("The searches return different data.");
			}
		}
		if(treeData.size()!=0){
			System.out.println("Found "+treeData.size()+" data.");
		}
	}

	private static Set<Datum> search(List<Datum> data, MBB cube) {
		Set<Datum> result = new HashSet<Datum>();
		int count=0;
		for(Datum datum:data){
			if(cube.include(datum)){
				result.add(datum);
			}
			if(count%2000==0){
//				System.out.println(count);
			}
			count++;
		}
		return result;
	}

	private static List<MBB> split3d(MBB mbb, int num) {
			
		long [] mbbHighs = mbb.getHighCorner();
		long [] mbbLows = mbb.getLowCorner();
		
		long steps[] = new long[mbb.getDimension()];
		for(int i=0;i<mbb.getDimension();i++){
			steps[i] = (mbbHighs[i]-mbbLows[i])/num;
			if(steps[i]==0){
				return null;
			}
		}
		
		List<MBB> cubes = new ArrayList<MBB>();
		long lows[] = new long[mbb.getDimension()];
		long highs[] = new long[mbb.getDimension()];
		for(int i=0;i<num;i++){
			for(int j=0;j<num;j++){
				for(int k=0;k<num;k++){
					lows[0] = mbbLows[0] + steps[0]*i;
					lows[1] = mbbLows[1] + steps[1]*j;
					lows[2] = mbbLows[2] + steps[2]*k;

					highs[0] = mbbLows[0] + steps[0]*(i+1);
					highs[1] = mbbLows[1] + steps[1]*(j+1);
					highs[2] = mbbLows[2] + steps[2]*(k+1);
					
					cubes.add(new MBB(highs, lows));
				}
			}
		}
		return cubes;
	}

	private static BinaryRTree storeData(List<Datum> data,
			Map<String, String> arguments) {
		LeafType type = LeafType.LOCAL;
		if(arguments.containsKey("mode") && arguments.get("mode").equals("jvm")){
			type = LeafType.JVM;
			Platform.startNodeManager(arguments.get("ips_file"));
		}
		
		
		BinaryRTree tree = new BinaryRTree(type);
		int count = 0;
		for(int i=0;i<data.size();i++){
			Datum datum = data.get(i);
			tree.insert(datum);
			count++;
			if(count%1000==0){
				System.out.printf("%d k items inserted\n", count/1000);
				if(count/1000==29){
					System.out.println();
				}
//				System.out.println("Average volume overlap is "+tree.measureOverlap());		
			}
		}
	//	tree.check();
	//	System.out.println(tree.getMbb());
		
		return tree;
	}

	private static List<Datum> loadData(Map<String, String> arguments) 
			throws FileNotFoundException {
		String filePath = arguments.get("data");
		String encode = arguments.containsKey("data_encode")?arguments.get("data_encode"):"ascii";
		Scanner scan = new Scanner(new FileInputStream(filePath),encode);
		ArrayList<Datum> data = new ArrayList<Datum>();
		TextBasicDatumLoader loader = new TextBasicDatumLoader();
		while(scan.hasNextLine()){
			data.add(loader.loadDatum(scan.nextLine()));
		}
		return data;
	}
	
}
