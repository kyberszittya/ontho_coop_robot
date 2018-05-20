package hu.bme.mit.inf.testing.artifact.mircontext.generator;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import extendedmircontext.BuildingBlock;
import extendedmircontext.ExtendedmircontextFactory;
import extendedmircontext.Floor;
import extendedmircontext.Human;
import extendedmircontext.TestRoom;
import extendedmircontext.TrajectoryMotion;
import extendedmircontext.Waypoint;

public class GenerateSimulationArtifact {
	
	private static final int max_waypoints = 4;
	
	
	
	public static boolean freeRadius(HashSet<Floor> global_fringe, Floor f, int radius) {
		HashSet<Floor> fringe = new HashSet<Floor>();
		RadiiState currentFloor;
		LinkedList<RadiiState> open = new LinkedList<>();
		open.push(new RadiiState(0, f, null));
		int maxDepth = 0;
		while (open.size()!=0) {
			currentFloor = open.pop();
			fringe.add(currentFloor.state);
			if (currentFloor.depth > radius) {
				continue;
			}
			if (global_fringe.contains(currentFloor.state)) {
				return false;
			}
			maxDepth = maxDepth < currentFloor.depth ? currentFloor.depth: maxDepth;
			if (currentFloor.state.getTop() instanceof Floor) {
				open.push(new RadiiState(
						currentFloor.depth+1,
						(Floor)currentFloor.state.getTop(),
						currentFloor)
				);
			}
			if (currentFloor.state.getBottom() instanceof Floor) {
				open.push(new RadiiState(
						currentFloor.depth+1,
						(Floor)currentFloor.state.getBottom(),
						currentFloor)
				);
			}
			if (currentFloor.state.getLeft() instanceof Floor) {
				open.push(new RadiiState(
						currentFloor.depth+1,
						(Floor)currentFloor.state.getLeft(),
						currentFloor)
				);
			}
			if (currentFloor.state.getLeft() instanceof Floor) {
				open.push(new RadiiState(
						currentFloor.depth+1,
						(Floor)currentFloor.state.getLeft(),
						currentFloor)
				);
			}
		}	
		if (maxDepth < radius) {
			return false;
		}
		if (fringe.size() < (int)(radius * radius*Math.PI)) {
			return false;
		}
		
		
		for (Floor s: fringe) {
			if (s.getPhysicalobject()!=null) {
				return false;
			}
		}
		global_fringe.addAll(fringe);
		return true;
	}
	
	public static TrajectoryMotion setTrajectory(HashSet<Floor> global_fringe, float t) {
		TrajectoryMotion tr = ExtendedmircontextFactory.eINSTANCE.createTrajectoryMotion();
		tr.setTotalTime(t);
		
		float waypoint_prob = 0.4f;
		Random random = new Random();
		int all_waypoints = 0;
		for (Floor s: global_fringe) {
			if (s.getPhysicalobject()!=null) {
				continue;
			}
			else if (random.nextFloat() < waypoint_prob) {
				Waypoint w = ExtendedmircontextFactory.eINSTANCE.createWaypoint();
				w.setFloor(s);
				tr.getWaypoints().add(w);
				all_waypoints++;
			}
			if ( all_waypoints > max_waypoints) {
				break;
			}
		}
		return tr;
	}
	
	public static Human addHuman(HashSet<Floor> global_fringe, TestRoom t) {
		
		for (BuildingBlock b: t.getBuildingblocks()) {
			if (b instanceof Floor) {
				if (freeRadius(global_fringe, (Floor)b, 2)) {
					Human h = ExtendedmircontextFactory.eINSTANCE.createHuman();
					h.setName("vori");
					
					h.setTrajectorymotion(setTrajectory(global_fringe, 120.0f));
					t.getPhysicalobjects().add(h);
					h.setFloor((Floor)b);
					((Floor) b).setPhysicalobject(h);
					
					return h;
				}
			}
		}
		return null;
	}
}
