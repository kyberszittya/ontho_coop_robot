package hu.bme.mit.inf.testing.testroom.model.mapping.tree;

import java.util.ArrayList;
import java.util.List;

import extendedmircontext.Floor;

public class FloorNode {
	private final Floor floor;
	private final int x;
	private final int y;
	private final FloorNode parent;
	
	private List<FloorNode> outnodes;
	
	public FloorNode(Floor floor) {
		this.floor = floor;
		this.x = 0;
		this.y = 0;
		this.parent = null;
		outnodes = new ArrayList<>();
	}
	
	public FloorNode(Floor floor, int x, int y) {
		this.floor = floor;
		this.x = x;
		this.y = y;
		this.parent = null;
		outnodes = new ArrayList<>();
	}
	
	public FloorNode(Floor floor, int x, int y, FloorNode parent) {
		this.floor = floor;
		this.x = x;
		this.y = y;
		this.parent = parent;
		outnodes = new ArrayList<>();
	}
	
	public void addOutGoingNode(FloorNode node)  {
		outnodes.add(node);
	}
	
	public List<FloorNode> getOutgoingNodes(){
		return outnodes;
	}
	
	public boolean isLeafNode() {
		return outnodes.size()==0;
	}

	public Floor getFloor() {
		return floor;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public FloorNode getParent() {
		return parent;
	}	
}
