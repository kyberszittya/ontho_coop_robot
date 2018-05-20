package hu.bme.mit.inf.testing.artifact.mircontext.generator;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import extendedmircontext.BuildingBlock;
import extendedmircontext.ExtendedmircontextPackage;
import extendedmircontext.Floor;
import extendedmircontext.PhysicalEntity;
import extendedmircontext.Robot;
import extendedmircontext.TestRoom;

public class DifficultyEstimator {
	ResourceSet resSet;
	Resource res_room;
	TestRoom t;
	
	public DifficultyEstimator() {
		resSet = new ResourceSetImpl();
		resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
				new XMIResourceFactoryImpl());
		resSet.getPackageRegistry().put(ExtendedmircontextPackage.eNS_URI, ExtendedmircontextPackage.eINSTANCE);			
	}
	
	public int ObjectCount(TestRoom t) {
		return t.getPhysicalobjects().size();
	}
	
	public int FloorCount(TestRoom t) {
		int floor_cnt = 0;
		for (BuildingBlock b: t.getBuildingblocks()) {
			if (b instanceof Floor) {
				floor_cnt++;
			}
		}
		return floor_cnt;
	}
	
	public int AnimatedObjectCount(TestRoom t) {
		int anim_count = 0;
		for (PhysicalEntity p: t.getPhysicalobjects()) {
			if (p instanceof extendedmircontext.Object) {
				
				if (((extendedmircontext.Object) p).getTrajectorymotion()!=null) {
					anim_count++;
				}
			}
		}
		return anim_count;
	}
	
	public double estimateArea(TestRoom t) {
		double area = 0.0;
		for (BuildingBlock b: t.getBuildingblocks()) {
			if (b instanceof Floor) {
				area += 1.0;
			}
		}
		return area;
	}
	
	public boolean isFloorOccupied(BuildingBlock b) {
		if (b!=null) {
			if (b instanceof Floor) {
				if (((Floor) b).getPhysicalobject()!=null) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void ListRobots(TestRoom t) {
		for (PhysicalEntity p: t.getPhysicalobjects()) {
			if (p instanceof Robot) {
				System.out.println(p.getName());
			}
		}
	}
	
	public double estimateLocalDensity(TestRoom t) {
		int count_floor = 0;
		double sum_local_density = 0.0;
		for (BuildingBlock b: t.getBuildingblocks()) {
			if (b instanceof Floor) {
				count_floor++;
				if (((Floor) b).getPhysicalobject()!=null) {
					int count_physobjects = 0;
					if (isFloorOccupied(((Floor) b).getLeft())) {
						count_physobjects++;
					}
					if (isFloorOccupied(((Floor) b).getTop())) {
						count_physobjects++;
					}
					if (isFloorOccupied(((Floor) b).getBottom())) {
						count_physobjects++;
					}
					if (isFloorOccupied(((Floor) b).getRight())) {
						count_physobjects++;
					}
					if (((Floor) b).getPhysicalobject()!=null) {
						count_physobjects++;
					}
					sum_local_density += 0.2*count_physobjects;
				}
			}
			
		}
		return sum_local_density/count_floor;
	}	
	
	
	public double estimateGlobalDensity(TestRoom t) {
		int count_floor = 0;
		int count_physobjects = 0;
		for (BuildingBlock b: t.getBuildingblocks()) {
			if (b instanceof Floor) {
				count_floor++;
				if (((Floor) b).getPhysicalobject()!=null) {
					count_physobjects++;
				}
			}
			
		}
		return (((double)count_physobjects/(double)count_floor));
	}
	
	public double estimateDifficulty(TestRoom t) {
		double area = estimateArea(t);
		double k_paths = 3.0;
		double k_animation = 10.0;
		int n_paths = 1;
		int n_animated = AnimatedObjectCount(t);
		double global_density = estimateGlobalDensity(t);
		double local_density = estimateLocalDensity(t);
		return (k_animation*(n_animated+1))*Math.sqrt(area)*(global_density*local_density)*(k_paths/n_paths);
	}
}
