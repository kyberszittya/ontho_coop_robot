package hu.bme.mit.inf.testing.testroom.model.mapping.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import extendedmircontext.BuildingBlock;
import extendedmircontext.Floor;

public class RoomTree {
	private final FloorNode rootnode;
	
	private final int offsetX;
	private final int offsetY;
	
	public RoomTree(Floor rootfloor, int ofx, int ofy) {
		this.offsetX = ofx;
		this.offsetY = ofy;
		this.rootnode = new FloorNode(rootfloor, ofx, ofy);
		
	}
	
	public void addToFringe(int dx, int dy, BuildingBlock b, FloorNode parent, LinkedList<FloorNode> fringe, Set<Floor> visited) {
		if (b!=null) {
			if (b instanceof Floor && !visited.contains(b))
			{
				Floor f = (Floor)b;
				visited.add(f);
				FloorNode fn = new FloorNode(f, parent.getX()+dx, parent.getY()+dy, parent);
				parent.addOutGoingNode(fn);
				fringe.push(fn);
			}
		}
	}
	
	public void traverse() {
		LinkedList<FloorNode> fringe = new LinkedList<>();
		Set<Floor> visited = new HashSet<>();
		fringe.add(rootnode);
		
		while(!fringe.isEmpty()) {
			FloorNode f = fringe.pop();
			Floor cfloor = f.getFloor();
			// Left floor
			addToFringe(-1, 0, cfloor.getLeft(), f, fringe, visited);
			// Right floor
			addToFringe(1, 0, cfloor.getRight(), f, fringe, visited);
			// Top floor
			addToFringe(0, 1, cfloor.getTop(), f, fringe, visited);
			// Bottom floor
			addToFringe(0, -1, cfloor.getBottom(), f, fringe, visited);
			
		}
	}

	public FloorNode getRootnode() {
		return rootnode;
	}
	
	public List<FloorNode> getExpansionField(){
		List<FloorNode> expansionfield = new ArrayList<FloorNode>();
		
		LinkedList<FloorNode> fringe = new LinkedList<>();
		fringe.add(rootnode);
		while(!fringe.isEmpty()) {
			FloorNode fn = fringe.pop();
			expansionfield.add(fn);
			for(FloorNode o: fn.getOutgoingNodes()) {
				fringe.push(o);
			}
		}
		
		return expansionfield;
	}
}
