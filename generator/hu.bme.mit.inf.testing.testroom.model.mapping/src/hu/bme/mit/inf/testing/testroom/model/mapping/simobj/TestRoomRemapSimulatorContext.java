package hu.bme.mit.inf.testing.testroom.model.mapping.simobj;

import java.util.ArrayList;
import java.util.List;

import extendedmircontext.Container;
import extendedmircontext.PhysicalEntity;
import extendedmircontext.Shelf;
import extendedmircontext.Wall;
import gazeboobject.File;
import gazeboobject.GazeboobjectFactory;
import gazeboobject.SimulatorObject;
import hu.bme.mit.inf.testing.testroom.model.mapping.tree.FloorNode;
import hu.bme.mit.inf.testing.testroom.model.mapping.tree.RoomTree;
import objectcatalogue.ObjectCatalogue;

public class TestRoomRemapSimulatorContext {
	// TODO: this should be a bidirectional map
	private BaseExtendedmircontextMapping mapping; 
	
	public void setupMapping(ObjectCatalogue catalogue) {
		mapping = new BaseExtendedmircontextMapping();
		mapping.initialize(catalogue);
	}
	
	public List<SimulatorObject> mapRoomTree(RoomTree rt) {
		List<SimulatorObject> res = new ArrayList<SimulatorObject>();
		for (FloorNode fn: rt.getExpansionField()) {
			res.addAll(collectSimulatorElementsOfFloor(fn));
		}
		return res;
	}
	
	public SimulatorObject mapFromCatalogue(PhysicalEntity pe) {
		SimulatorObject sm = null;
		
		
		return sm;
	}
	
	
	
	public List<SimulatorObject> collectSimulatorElementsOfFloor(FloorNode fn){
		List<SimulatorObject> res = new ArrayList<>();
		//res.add(mapFromCatalogue(fn.getFloor().getPhysicalobject()));
		SimulatorObject r = mapping.mapGenerateSimobj(
				fn.getFloor().getName(), fn.getX(), fn.getY(), 0.0f, 0.0f
		);
		res.add(r);
		
		// Collect phys object
		if (fn.getFloor().getPhysicalobject()!=null) {
			if (fn.getFloor().getPhysicalobject() instanceof Shelf) {
				Shelf sh = (Shelf)(fn.getFloor().getPhysicalobject());
				SimulatorObject s = mapping.mapGenerateSimobj(sh.getName(), 
						fn.getX(), 
						fn.getY(), 
						0.0f, 0.0f);
				float x = -1.5f;
				float y = -0.25f;
				int i = 0;
				for (Container co: sh.getContainer()) {
					x+= 0.5f;
					System.out.println(co.getName());
					SimulatorObject simobj = mapping.mapGenerateContainer(
							co.getName(), co.getLabel(),
							x+fn.getX(), y+fn.getY(), 
							0.5f, 0);					
					File f = GazeboobjectFactory.eINSTANCE.createFile();
					f.setPath("model://container_"+co.getLabel());
					simobj.setFile(f);
					res.add(simobj);
					i++;
					if (i % 5 == 0) {
						x = -1.5f;
						y += 0.5f;
					}
				}
			}
			else if (fn.getFloor().getPhysicalobject() instanceof Container) {
				SimulatorObject simobj = mapping.mapGenerateSimobj(
						fn.getFloor().getPhysicalobject().getName(), 
						fn.getX(), fn.getY(), 0.5f, 0);
				Container c = (Container)(fn.getFloor().getPhysicalobject());
				File f = GazeboobjectFactory.eINSTANCE.createFile();
				f.setPath("model://container_"+c.getLabel());
				simobj.setFile(f);
				res.add(simobj);
			}
			else {
				res.add(mapping.mapGenerateSimobj(
						fn.getFloor().getPhysicalobject().getName(), 
						fn.getX(), fn.getY(), 0.5f, 0));
			}
		}
		// Collect neighbors if they are walls
		if (fn.getFloor().getRight()!=null) {
			if (fn.getFloor().getRight() instanceof Wall) {
			res.add(mapping.mapGenerateSimobj(
					fn.getFloor().getRight().getName(), 
					fn.getX()+0.5f, fn.getY(), 0.5f, Math.PI/2.0));
			}
		}
		if (fn.getFloor().getLeft()!=null) {
			if (fn.getFloor().getLeft() instanceof Wall) {
			res.add(mapping.mapGenerateSimobj(
					fn.getFloor().getLeft().getName(), 
					fn.getX()-0.5f, fn.getY(), 0.5f, Math.PI/2.0));
			}
		}
		if (fn.getFloor().getTop()!=null) {
			if (fn.getFloor().getTop() instanceof Wall) {
			res.add(mapping.mapGenerateSimobj(
					fn.getFloor().getTop().getName(), 
					fn.getX(), fn.getY()+0.5f, 0.5f, 0.0));
			}
		}
		if (fn.getFloor().getBottom()!=null) {
			if (fn.getFloor().getBottom() instanceof Wall) {
			SimulatorObject r0 = mapping.mapGenerateSimobj(
					fn.getFloor().getBottom().getName(), 
					fn.getX(), fn.getY()-0.5f, 0.5f, 0.0);
			res.add(r0);
			}
		}
		
		return res;
	}

}
