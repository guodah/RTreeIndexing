package com.geofeedia.sdquerry.rtree;

import java.io.Serializable;
import java.util.Stack;

import com.geofeedia.sdquerry.rtree.leaf.LeafStub;
import com.geofeedia.sdquerry.rtree.leaf.MBB;

public class BinaryRTreeNode implements Serializable{
	private BinaryRTreeNode parent;
	private MBB mbb;
	private int height;
	
	public BinaryRTreeNode(){
		
	}
	
	public int getBalance(){
		if(left==null && right==null){
			return 0;
		}else{
			return left.getHeight()-right.getHeight();
		}
	}
	
	public int getHeight(){
		return height;
	}
	public BinaryRTreeNode getParent() {
		return parent;
	}
	public void setParent(BinaryRTreeNode parent) {
		this.parent = parent;
	}
	private BinaryRTreeNode left;
	public BinaryRTreeNode getLeft() {
		return left;
	}
	public void setLeft(BinaryRTreeNode left) {
		this.left = left;
		if(left!=null){
			left.setParent(this);
		}
	}
	public BinaryRTreeNode getRight() {
		return right;
	}
	public void setRight(BinaryRTreeNode right) {
		this.right = right;
		if(right!=null){
			right.setParent(this);
		}
	}
	private BinaryRTreeNode right;
	protected int dimension;

	public MBB getMbb(){
		return mbb;
	}
	
	public void growMbb(MBB mbb){
		mbb.grow(mbb);
	}
	
	protected BinaryRTreeNode(int dimension){
		parent = null;
		mbb = new MBB(dimension);
		height = 0;
	}
	
	public static BinaryRTreeNode createParent(LeafStub p,LeafStub q) {
		BinaryRTreeNode result = new BinaryRTreeNode(p.getMbb().getDimension());
		result.mbb = MBB.union(p.getMbb(), q.getMbb());
		return result;
	}
		
	public boolean isBalanced() {
		if(left==null && right==null){
			return true;
		}
		int delta = left.getHeight() - right.getHeight();
		return delta>=-1 && delta<=1;
	}
	
	public void setMBB(MBB mbb) {
		this.mbb = mbb; 
	}
	public void findHeight() {
		height = Math.max(left.getHeight(), right.getHeight())+1;
	}
	
	public String getPathFromRoot(){
		if(getParent()==null){
			return "root";
		}
		
		String path = "";
		BinaryRTreeNode p = this;
		
		while(p.getParent()!=null){
			path = ((p==p.getParent().getLeft())?"left->":"right->")+path;
			p=p.getParent();
		}
		return path;
	}
}
