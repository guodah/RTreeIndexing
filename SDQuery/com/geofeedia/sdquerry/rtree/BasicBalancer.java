package com.geofeedia.sdquerry.rtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.geofeedia.sdquerry.rtree.leaf.LeafStub;
import com.geofeedia.sdquerry.rtree.leaf.MBB;

public class BasicBalancer implements Balancer{

	private List<BinaryRTreeNode> internalNodes;
	private List<BinaryRTreeNode> subtrees;
	
	@Override
	public void balance(LeafStub leaf) {
		traverse(leaf);
		rotate();
	}
	
	
	private void rotate() {
		if(internalNodes.size()!=3 || subtrees.size()!=4){
			return;
		}
		
		orderSubtrees();
		reconstruct();
	}


	private void reconstruct() {
		BinaryRTreeNode p1 = internalNodes.get(0);
		BinaryRTreeNode p2 = internalNodes.get(1);
		BinaryRTreeNode p3 = internalNodes.get(2);
		
		if(p1.getLeft()==p2){
			p1.setRight(p3);
		}else{
			p1.setLeft(p3);
		}
		
		p2.setLeft(subtrees.get(0));
		p2.setRight(subtrees.get(1));
		p2.setMBB(MBB.union(subtrees.get(0).getMbb(), subtrees.get(1).getMbb()));
		p2.findHeight();
		
		p3.setLeft(subtrees.get(2));
		p3.setRight(subtrees.get(3));
		p3.setMBB(MBB.union(subtrees.get(2).getMbb(), subtrees.get(3).getMbb()));
		p3.findHeight();
		
		p1.setMBB(MBB.union(p2.getMbb(), p3.getMbb()));
		p1.findHeight();
	}


	private void orderSubtrees() {
		BinaryRTreeNode a = subtrees.get(0);
		BinaryRTreeNode b = subtrees.get(1);
		BinaryRTreeNode c = subtrees.get(2);
		BinaryRTreeNode d = subtrees.get(3);
		
		subtrees.clear();
		
		double option1 = MBB.overlap(MBB.union(a.getMbb(), b.getMbb()),
				MBB.union(c.getMbb(), d.getMbb()));
		double option2 = MBB.overlap(MBB.union(a.getMbb(), c.getMbb()),
				MBB.union(b.getMbb(), d.getMbb()));
		double option3 = MBB.overlap(MBB.union(a.getMbb(), d.getMbb()),
				MBB.union(c.getMbb(), b.getMbb()));
		
		if(option1<option2 && option1<option3){
			subtrees.add(a); subtrees.add(b);
			subtrees.add(c); subtrees.add(d);
		}else if(option2<option1 && option2<option3){
			subtrees.add(a); subtrees.add(c);
			subtrees.add(b); subtrees.add(d);
		}else{
			subtrees.add(a); subtrees.add(d);
			subtrees.add(c); subtrees.add(b);
		}
	}


	private void traverse(LeafStub leaf) {
		internalNodes.clear();
		subtrees.clear();
		
		Stack<BinaryRTreeNode> st = new Stack<BinaryRTreeNode>();
		BinaryRTreeNode p = leaf.getParent();
		p.findHeight();
		st.push(p);
		while(p.getParent()!=null){
			
			p.getParent().findHeight();
			st.push(p.getParent());
			
			if(!p.getParent().isBalanced()){
				break;
			}
			
			p = p.getParent();
		}
		
		if(st.isEmpty() || st.peek().isBalanced()){
			return;
		}
		
		for(int i=0;i<3;i++){
			internalNodes.add(st.pop());
		}
		
		p = internalNodes.get(0);
		BinaryRTreeNode q = internalNodes.get(1);
		BinaryRTreeNode hold = internalNodes.get(2);
		
		if(p.getBalance()>0){
			subtrees.add(p.getRight());
		}else{
			subtrees.add(p.getLeft());
		}
		
		if(q.getBalance()>0){
			subtrees.add(q.getRight());
		}else{
			subtrees.add(q.getLeft());
		}
		
		subtrees.add(hold.getLeft());
		subtrees.add(hold.getRight());
	}


	public BasicBalancer(){
		internalNodes = new ArrayList<BinaryRTreeNode>();
		subtrees = new ArrayList<BinaryRTreeNode>();
	}
	
}
