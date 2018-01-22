package com.geofeedia.sdquerry.rtree.leaf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedMap;
import java.util.TreeMap;

import com.geofeedia.rtree.util.CollectionUtils;
import com.geofeedia.sdquerry.datatypes.Datum;

public class RStarSplitter implements Splitter{

	private class DatumComparator implements Comparator<Datum>{
		
		private int dim;
		DatumComparator(int dim){
			this.dim = dim;
		}

		@Override
		public int compare(Datum d1, Datum d2) {
			long diff = (d1.getCoordinates()[dim] - d2.getCoordinates()[dim]);
			if(diff>0){
				return 1;
			}else if(diff<0){
				return -1;
			}else{
				return 0;
			}
		}
		
	}
	
	@Override
	public List<List<Datum>> split(List<Datum> items, float minUtil) {
		if(items==null || items.size()<2){
			return null;
		}
		
		int m = (int) ((items.size()-1)*minUtil);
		List<List<Datum>> sortedItems = findSortedItems(items);
		List<List<List<MBB>>> mbbs = findMbbs(sortedItems, m);
		int axis = chooseSplitAxis(mbbs);
		List<List<Datum>> distro = chooseSplitIndex(sortedItems.get(axis),  m);
		return distro;
	}

	private List<List<Datum>> chooseSplitIndex(List<Datum> sortedItems,int m) {
		int M = sortedItems.size()-1;
		List<Datum> g1 = CollectionUtils.buildList(sortedItems, 0, m-1);
		List<Datum> g2 = CollectionUtils.buildList(sortedItems, m, M);
		SortedMap<Double, List<Integer>> indices = new TreeMap<Double, List<Integer>>();
		Map<Integer, List<MBB>> mbbs = new HashMap<Integer, List<MBB>>();
		for(int i=m;i<=M-m+1;i++){
			MBB m1 = new MBB(g1);
			MBB m2 = new MBB(g2);
			
			double overlap = MBB.overlap(m1, m2);
			if(indices.get(overlap)==null){
				indices.put(overlap, new ArrayList<Integer>());
			}
			indices.get(overlap).add(i);
			
			List<MBB> ms = new ArrayList<MBB>();
			ms.add(m1); ms.add(m2);
			mbbs.put(i, ms);
//			g2.add(g1.remove(g1.size()-1));
			g1.add(g2.remove(0));
		}
		
		List<Integer> candidates = indices.get(indices.firstKey());
		double minVol = Double.MAX_VALUE; 
		int minCandidate=-1;
		for(Integer candidate:candidates){
			List<MBB> ms = mbbs.get(candidate);
			double volume = ms.get(0).volume()+ms.get(1).volume();
			if(minVol>volume){
				minVol = volume;
				minCandidate = candidate;
			}
		}
		
		List<List<Datum>> result = new ArrayList<List<Datum>>();
		result.add(CollectionUtils.buildList(sortedItems, 0, minCandidate-1));
		result.add(CollectionUtils.buildList(sortedItems, minCandidate, M));
		return result;
	}

	private List<List<List<MBB>>> findMbbs(List<List<Datum>> sortedItems, int m ) {
		int M = sortedItems.get(0).size()-1;
		List<List<List<MBB>>> mbbs = new ArrayList<List<List<MBB>>>();
		
		for(List<Datum> list:sortedItems){
			// for each dimension
			List<List<MBB>> dimMbbs= new ArrayList<List<MBB>>();
			
			List<Datum> g1 = CollectionUtils.buildList(list, 0, m-1);
			List<Datum> g2 = CollectionUtils.buildList(list, m, M);
			for(int i=m;i<=M-m+1;i++){
				List<MBB> distroMbbs = new ArrayList<MBB>();
								
				distroMbbs.add(new MBB(g1));
				distroMbbs.add(new MBB(g2));
				
				dimMbbs.add(distroMbbs);
				
//				g2.add(g1.remove(g1.size()-1));
				g1.add(g2.remove(0));
			}
			mbbs.add(dimMbbs);
		}
		
		return mbbs;
	}

	private int chooseSplitAxis(List<List<List<MBB>>> mbbs) {
		long minSum = Long.MAX_VALUE; int minIndex = -1;
		for(int i=0;i<mbbs.size();i++){
			long sum = 0;
			for(List<MBB> distroMbbs:mbbs.get(i)){
				sum += (distroMbbs.get(0).margin()+distroMbbs.get(1).margin());
			}
			if(minSum>sum){
				minSum = sum;
				minIndex = i;
			}
		}
		return minIndex;
	}

	private List<List<Datum>> findSortedItems(List<Datum> items) {
		int numDimensions = items.iterator().next().getCoordinates().length;
		List<List<Datum>> sortedItems = new ArrayList<List<Datum>>();
		for(int i=0;i<numDimensions;i++){
			List<Datum> _items = new ArrayList<Datum>();
			_items.addAll(items);
			Collections.sort(_items, new DatumComparator(i));
			sortedItems.add(_items);
		}
		return sortedItems;
	}


}
