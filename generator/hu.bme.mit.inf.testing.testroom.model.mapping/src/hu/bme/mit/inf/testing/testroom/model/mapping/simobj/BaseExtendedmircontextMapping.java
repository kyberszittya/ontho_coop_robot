package hu.bme.mit.inf.testing.testroom.model.mapping.simobj;

import java.util.Map;

import gazeboobject.SimulatorObject;
import objectcatalogue.ObjectCatalogue;

public class BaseExtendedmircontextMapping {
    private Map<String, SimulatorObject> simobjmapping;
    
    public void initialize(ObjectCatalogue catalogue){
        simobjmapping = ExtendedmircontextCreator.createSimobjMapping(catalogue);
    }
    
    public SimulatorObject mapGenerateContainer(String name,
    		String label,
    		float x, float y, float z, double yaw){
        SimulatorObject r = null;
        r = ExtendedmircontextCreator.mapContainer(name, x, y, z, yaw, simobjmapping.get("container"));
        
        return r;
    }
    
    public SimulatorObject mapGenerateSimobj(String name, float x, float y, float z, double yaw){
        SimulatorObject r = null;
        
        String name_fragment = name.split("_")[0];
        switch(name_fragment){
            case "wall":
                r = ExtendedmircontextCreator.mapWall(name, x, y, z, yaw, simobjmapping.get(name_fragment));
                break;
            case "floor":
                r = ExtendedmircontextCreator.mapFloor(name, x, y, z, yaw, simobjmapping.get(name_fragment));
                break;
            case "box":
            case "cube":
                r = ExtendedmircontextCreator.mapBox(name, x, y, z, yaw, simobjmapping.get(name_fragment));
                break;
            case "ball":
            case "sphere":
                r = ExtendedmircontextCreator.mapBall(name, x, y, z, yaw, simobjmapping.get(name_fragment));
                break;
            case "turtlebot3_waffle":
                r = ExtendedmircontextCreator.mapTurtlebot3_waffle(name, x, y, z, yaw, simobjmapping.get(name_fragment));
                break;
            case "human":
                r = ExtendedmircontextCreator.mapHuman(name, x, y, z, yaw, simobjmapping.get(name_fragment));
                break;
            case "table":
                r = ExtendedmircontextCreator.mapTable(name, x, y, z, yaw, simobjmapping.get(name_fragment));
                break;
            case "bookshelf":
                r = ExtendedmircontextCreator.mapBookshelf(name, x, y, z, yaw, simobjmapping.get(name_fragment));
                break;
            case "chair":
                r = ExtendedmircontextCreator.mapChair(name, x, y, z, yaw, simobjmapping.get(name_fragment));
                break;
            case "container":
                r = ExtendedmircontextCreator.mapContainer(name, x, y, z, yaw, simobjmapping.get(name_fragment));
                break;
            case "shelf":
                r = ExtendedmircontextCreator.mapShelf(name, x, y, z, yaw, simobjmapping.get(name_fragment));
                break;
        }
        
        return r;
    }
    
}


