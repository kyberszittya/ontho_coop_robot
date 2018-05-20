package hu.bme.mit.inf.testing.artifact.mircontext.generator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import MonitorConfiguration.Configuration;
import extendedmircontext.Floor;
import extendedmircontext.PhysicalEntity;
import extendedmircontext.Robot;
import extendedmircontext.TestRoom;
import hu.bme.mit.inf.mircontext.model.generator.simulatordescription.FloorState;
import hu.bme.mit.inf.mircontext.model.generator.simulatordescription.MapMircontext2SDF;
import hu.bme.mit.inf.robotics.util.geometry.Quaternion;
import hu.bme.mit.inf.robotics.util.geometry.RotationConversions;
import objectcatalogue.ObjectCatalogue;

public class CommonUtils {
	
	public static void listRobots(TestRoom t) {
		for (PhysicalEntity p: t.getPhysicalobjects()) {
			if (p instanceof Robot) {
				System.out.println(p.getName());
			}
		}
	}
	
	public static ObjectCatalogue LoadObjectCatalogue(ResourceSet resSet, String filename) throws CatalogueLoadError {
		ObjectCatalogue catalog = null;
		Resource res_obj_catalog = resSet.getResource(URI.createFileURI(filename), true);
		EcoreUtil.resolveAll(res_obj_catalog);
		for (EObject e: res_obj_catalog.getContents()) {
			if (e instanceof ObjectCatalogue) {
				catalog = (ObjectCatalogue)e;
				return catalog;
			}
		}
		throw new CatalogueLoadError();
	}
	
	public static TestRoom LoadTestroomFromFile(ResourceSet resSet,	String filename) throws ArtifactLoadError{
		TestRoom t ;
		Resource res_room = resSet.getResource(URI.createFileURI(filename), true);
		for (EObject e:	res_room.getContents()) {
			if (e instanceof TestRoom) {
				t = (TestRoom)e;
				System.out.println(t.getName());
				return t;
			}
		}
		throw new ArtifactLoadError();
	}
	
	public static Quaternion getPossibleOrientation(Floor f, MapMircontext2SDF mr2) {
		FloorState ref = mr2.getCoordState(f);
		FloorState ref_n = ref.getParent();
		System.out.println(ref.getParent());
		Quaternion res = null;
		if (ref_n != null) {
			double t = Math.atan2((double)(ref.getY()-ref_n.getY()), 
					(double)(ref.getX() - ref_n.getX()));
			res = RotationConversions.yawToQuaternion(t);
		}
		
		
		return res;
	}
	
	public static Configuration LoadConfigurationFromFile(ResourceSet resSet, String filename) throws ArtifactLoadError{
		Configuration conf;
		Resource res_conf = resSet.getResource(URI.createFileURI(filename), true);
		for (EObject e:	res_conf.getContents()) {
			if (e instanceof Configuration) {
				conf = (Configuration)e;
				System.out.println(conf.getName());
				return conf;
			}
		}
		throw new ArtifactLoadError();
	}
}
