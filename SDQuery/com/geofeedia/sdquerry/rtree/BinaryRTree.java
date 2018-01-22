package com.geofeedia.sdquerry.rtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import com.geofeedia.sdquerry.datatypes.Datum;
import com.geofeedia.sdquerry.rtree.leaf.GeoMBB;
import com.geofeedia.sdquerry.rtree.leaf.LeafStub;
import com.geofeedia.sdquerry.rtree.leaf.MBB;

/**
 * This BinaryRTree is the core of the directory. There are two kinds of
 * nodes: regular BinaryRTreeNode and LeafStub. BinaryRTreeNode is for 
 * the internal node. LeafStub is for the leaf nodes, hiding the details how 
 * leaves are implemented.
 * @author Dahai Guo
 *
 */
public class BinaryRTree {
	private BinaryRTreeNode root;
	private LeafType leafType;
	private Balancer balancer;
	
	public void insert(Datum datum){
		// if root is a leaf, insert datum to it
		// otherwise, insert to the son without overlapping and
		//    less enlargement
		
		BinaryRTreeNode p = root;
		
		while(p.getLeft()!=null && p.getRight()!=null){
			p.getMbb().grow(datum);
			
			MBB left = p.getLeft().getMbb();
			MBB right = p.getRight().getMbb();
			if(left.include(datum)){
				p = p.getLeft();
			}else if(right.include(datum)){
				p = p.getRight();
			}else if(left.enlargement(datum)>right.enlargement(datum)){
				p = p.getRight();
			}else{
				p = p.getLeft();
			}
		}		
		
		LeafStub brother = ((LeafStub)p).insert(datum);
		if(brother==null){
			return;
		}else{
			BinaryRTreeNode parent = p.getParent();
			
			BinaryRTreeNode sub = BinaryRTreeNode.createParent((LeafStub)p, brother);
			sub.setLeft(p);
			sub.setRight(brother);
						
			if(parent==null){
				root = sub;
			}else{
				if(p==parent.getLeft()){
					parent.setLeft(sub);
				}else{
					parent.setRight(sub);
				}
			}
			balancer.balance(brother);
		}
	}
	
	/**
	 * Searches for overlapping leaves
	 * 
	 * @param range
	 * @return
	 */
	public Collection<LeafStub> searchForLeaves(MBB range){
		return searchForLeaves(range, root);
	}
	
	private Collection<LeafStub> searchForLeaves(MBB range,
			BinaryRTreeNode root) {
		Collection<LeafStub> result = new ArrayList<LeafStub>();
		if(root==null || !root.getMbb().overlap(range)){
			return result;
		}
		
		if(root.getMbb().overlap(range)){
			if(root.getLeft()==null && root.getRight()==null){
				result.add((LeafStub)root);
			}else{
				if(root.getLeft().getMbb().overlap(range)){
					result.addAll(searchForLeaves(range, root.getLeft()));
				}
				if(root.getRight().getMbb().overlap(range)){
					result.addAll(searchForLeaves(range, root.getRight()));
				}				
			}
		}
		return result;
	}

	public LeafStub findLeafStubContains(long id){
		return findLeafStubContains(root, id);
	}
	
	private LeafStub findLeafStubContains(BinaryRTreeNode root, long id) {
		
		if(root.getLeft()==null && root.getRight()==null){
			Collection<Datum> data = ((LeafStub)root).loadAll();
			for(Datum datum:data){
				if(datum.getId()==id){
					return (LeafStub)root;
				}
			}
			return null;
		}
		
		LeafStub left = findLeafStubContains(root.getLeft(), id);
		LeafStub right = findLeafStubContains(root.getRight(), id);
		
		if(left!=null){
			return left;
		}else{
			return right;
		}
	}

	public Collection<Datum> search(MBB range){
		Collection<LeafStub> leaves = searchForLeaves(range);
		Collection<Datum> data = new ArrayList<Datum>();
		for(LeafStub leaf:leaves){
			data.addAll(leaf.search(range));
		}
		return data;
	}

	public BinaryRTree(LeafType type){
		leafType = type;
		root = Platform.checkOutLeafStub(type);
		this.balancer = new BasicBalancer();
	}
	
	public BinaryRTree(LeafType type, int leafCapacity){
		leafType = type;
		root = Platform.checkOutLeafStub(type, leafCapacity);
		this.balancer = new BasicBalancer();
	}
	
	/**
	 * 0. the tree is a strictly binary tree
	 * 1. each node's height is correct
	 * 2. each node's balance is in good range
	 * 3. each node's mbb is the union of the children
	 * 4. the data in each leaf are in the mbb
	 */
	public void check(){
		checkHelp(root);
	}

	private int checkHelp(BinaryRTreeNode root) {

		if(root==null){
			return -1;
		}
		
		if(root.getLeft()==null && root.getRight()!=null ||
				root.getLeft()!=null && root.getRight()==null){
			throw new IllegalStateException("The tree is not strictly binary.");
		}
		
		
		checkLeaf(root);
		checkMbb(root);
		return checkHeights(root);
	}

	private void checkMbb(BinaryRTreeNode root) {
		
		if(root.getLeft()==null || root.getRight()==null){
			return;
		}
		
		if(!root.getMbb().include(root.getLeft().getMbb()) ||
				!root.getMbb().include(root.getRight().getMbb())){
			throw new IllegalStateException("A tree node's mbb does not include its children's.");
		}else if(!root.getMbb().equals(MBB.union(
				root.getLeft().getMbb(), root.getRight().getMbb()))){
			throw new IllegalStateException("A tree node's mbb is not the union of its children.");
		}
	}

	private int checkHeights(BinaryRTreeNode root) {
		
		int leftHeight = checkHelp(root.getLeft());
		int rightHeight = checkHelp(root.getRight());
		
		if(root.getHeight()!=Math.max(leftHeight, rightHeight)+1){
			throw new IllegalStateException("A tree node has incorrect height.");
		}
		
		if(Math.abs(leftHeight-rightHeight)>1){
			throw new IllegalStateException("A tree node is unbalanced.");
		}
		return root.getHeight();
	}

	private void checkLeaf(BinaryRTreeNode root) {

		if(root.getLeft()!=null || root.getRight()!=null){
			return;
		}
		List<Datum> data = ((LeafStub)root).loadAll();
		
		if(data.size()!=((LeafStub)root).getSize()){
			throw new IllegalStateException(String.format(
					"Leaf's number of data is wrong. actual (%d) vs counter(%d)",
					data.size(), ((LeafStub)root).getSize()));
		}else if(data.size()>Platform.getLeafCapacity()){
			throw new IllegalStateException("Leaf overflown.");
		}else if(root.getParent()!=null && 
				data.size()<Platform.getLeafCapacity()*Platform.getMinUtilization()){
			throw new IllegalStateException("Leaf underflown.");
		}
		
		if(!root.getMbb().include(data)){
			throw new IllegalStateException("Leaf data not included in the mbb.");
		}
	}

	public double measureOverlap(){
		Stack<Double> stack = new Stack<Double>(); 
		measureOverlapHelp(root,stack);
		
		double mean = 0;
		for(Double overlap:stack){
			mean += overlap;
		}
		return mean/stack.size();
	}

	private void measureOverlapHelp(BinaryRTreeNode root, Stack<Double> stack) {
		if(root.getLeft()==null && root.getRight()==null){
			return;
		}
		
		double overlap = MBB.overlap(root.getLeft().getMbb(), root.getRight().getMbb())*1.0;
		stack.push(overlap/(root.getLeft().getMbb().volume()*1.0+root.getRight().getMbb().volume()*1.0));
		measureOverlapHelp(root.getLeft(), stack);
		measureOverlapHelp(root.getRight(), stack);
	}

	public MBB getMbb() {
		return root.getMbb();
	}
}
