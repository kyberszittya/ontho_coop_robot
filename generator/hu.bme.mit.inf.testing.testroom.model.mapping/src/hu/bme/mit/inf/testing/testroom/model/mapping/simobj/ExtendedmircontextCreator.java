package hu.bme.mit.inf.testing.testroom.model.mapping.simobj;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;

import gazeboobject.SimulatorObject;
import objectcatalogue.CatalogueItem;
import objectcatalogue.ObjectCatalogue;
import objectcatalogue.ObjectMatch;

public class ExtendedmircontextCreator {
	

	public static Map<String, SimulatorObject> createSimobjMapping(ObjectCatalogue catalogue) {
		Map<String, SimulatorObject> res = new HashMap<String, SimulatorObject>();
		
		for (CatalogueItem ci: catalogue.getCatalogueitem()) {
			for (ObjectMatch om: ci.getObjectmatch()) {
				res.put(om.getName_match(), ci.getSimulatorobject());				
			}
			
		}
		
		return res;
	}
	
	public static SimulatorObject mapWall(String name, float x, float y, float z,double yaw, SimulatorObject simulatorObject) {
		SimulatorObject simobj = EcoreUtil.copy(simulatorObject);
		simobj.setName(name);
		simobj.getPose().getPosition().setX(x);
		simobj.getPose().getPosition().setY(y);
		simobj.getPose().getPosition().setZ(z);
		
		double cy = Math.cos(yaw * 0.5);
		double sy = Math.sin(yaw * 0.5);
		double cr = 1;
		double sr = 0;
		double cp = 1;
		double sp = 0;
		
		simobj.getPose().getOrientation().setW((float)(cy * cr * cp + sy * sr * sp));
		simobj.getPose().getOrientation().setX((float)(cy * sr * cp - sy * cr * sp));
		simobj.getPose().getOrientation().setY((float)(cy * cr * sp + sy * sr * cp));
		simobj.getPose().getOrientation().setZ((float)(sy * cr * cp - cy * sr * sp));
		return simobj;
	}

	public static SimulatorObject mapFloor(String name, float x, float y, float z, double yaw, SimulatorObject simulatorObject) {
		
		SimulatorObject simobj = EcoreUtil.copy(simulatorObject);
		simobj.setName(name);
		
		simobj.getPose().getPosition().setX(x);
		simobj.getPose().getPosition().setY(y);
		simobj.getPose().getPosition().setZ(z);
		
		return simobj;
	}

	public static SimulatorObject mapBox(String name, float x, float y, float z,double yaw, SimulatorObject simulatorObject) {
		SimulatorObject simobj = EcoreUtil.copy(simulatorObject);
		simobj.setName(name);
		simobj.getPose().getPosition().setX(x);
		simobj.getPose().getPosition().setY(y);
		
		return simobj;
	}

	public static SimulatorObject mapBall(String name, float x, float y, float z,double yaw, SimulatorObject simulatorObject) {
		SimulatorObject simobj = EcoreUtil.copy(simulatorObject);
		simobj.setName(name);
		simobj.getPose().getPosition().setX(x);
		simobj.getPose().getPosition().setY(y);
		
		return simobj;
	}

	public static SimulatorObject mapTurtlebot3_waffle(String name, float x, float y, float z,double yaw, SimulatorObject simulatorObject) {
		SimulatorObject simobj = EcoreUtil.copy(simulatorObject);
		simobj.setName(name);
		simobj.getPose().getPosition().setX(x);
		simobj.getPose().getPosition().setY(y);
		
		return simobj;
	}

	public static SimulatorObject mapHuman(String name, float x, float y, float z,double yaw, SimulatorObject simulatorObject) {
		SimulatorObject simobj = EcoreUtil.copy(simulatorObject);
		simobj.setName(name);
		simobj.getPose().getPosition().setX(x);
		simobj.getPose().getPosition().setY(y);
		
		return simobj;
	}

	public static SimulatorObject mapTable(String name, float x, float y, float z,double yaw, SimulatorObject simulatorObject) {
		SimulatorObject simobj = EcoreUtil.copy(simulatorObject);
		simobj.setName(name);
		simobj.getPose().getPosition().setX(x);
		simobj.getPose().getPosition().setY(y);
		
		return simobj;
	}

	public static SimulatorObject mapBookshelf(String name, float x, float y, float z,double yaw, SimulatorObject simulatorObject) {
		SimulatorObject simobj = EcoreUtil.copy(simulatorObject);
		simobj.setName(name);
		simobj.getPose().getPosition().setX(x);
		simobj.getPose().getPosition().setY(y);
		
		return simobj;
	}

	public static SimulatorObject mapChair(String name, float x, float y, float z,double yaw, SimulatorObject simulatorObject) {
		SimulatorObject simobj = EcoreUtil.copy(simulatorObject);
		simobj.setName(name);
		simobj.getPose().getPosition().setX(x);
		simobj.getPose().getPosition().setY(y);
		
		return simobj;
	}
	
	public static SimulatorObject mapContainer(String name, String label, 
			float x, float y, float z, double yaw,
			SimulatorObject simulatorObject) {
		SimulatorObject simobj = EcoreUtil.copy(simulatorObject);
		simobj.setName(name);
		//simobj.set
		simobj.getPose().getPosition().setX(x);
		simobj.getPose().getPosition().setY(y);
		simobj.getPose().getPosition().setZ(z);
		return simobj;
	}

	public static SimulatorObject mapContainer(String name, float x, float y, float z, double yaw,
			SimulatorObject simulatorObject) {
		SimulatorObject simobj = EcoreUtil.copy(simulatorObject);
		simobj.setName(name);
		//simobj.set
		simobj.getPose().getPosition().setX(x);
		simobj.getPose().getPosition().setY(y);
		simobj.getPose().getPosition().setZ(z);
		return simobj;
	}

	public static SimulatorObject mapShelf(String name, float x, float y, float z, double yaw,
			SimulatorObject simulatorObject) {
		SimulatorObject simobj = EcoreUtil.copy(simulatorObject);
		simobj.setName(name);
		simobj.getPose().getPosition().setX(x);
		simobj.getPose().getPosition().setY(y);
		simobj.getPose().getPosition().setZ(z);
		
		double cy = Math.cos(yaw * 0.5);
		double sy = Math.sin(yaw * 0.5);
		double cr = 1;
		double sr = 0;
		double cp = 1;
		double sp = 0;
		
		simobj.getPose().getOrientation().setW((float)(cy * cr * cp + sy * sr * sp));
		simobj.getPose().getOrientation().setX((float)(cy * sr * cp - sy * cr * sp));
		simobj.getPose().getOrientation().setY((float)(cy * cr * sp + sy * sr * cp));
		simobj.getPose().getOrientation().setZ((float)(sy * cr * cp - cy * sr * sp));
		
		return simobj;
	}

	
	
}
