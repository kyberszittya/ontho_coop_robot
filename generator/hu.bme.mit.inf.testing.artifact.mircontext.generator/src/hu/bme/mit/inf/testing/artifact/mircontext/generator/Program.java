package hu.bme.mit.inf.testing.artifact.mircontext.generator;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import MonitorConfiguration.MonitorConfigurationPackage;
import extendedmircontext.ExtendedmircontextPackage;
import extendedmircontext.TestRoom;
import hu.bme.mit.inf.testing.robotics.mission.description.missiondescription.MissiondescriptionPackage;
import objectcatalogue.ObjectCatalogue;
import objectcatalogue.ObjectcataloguePackage;

public class Program {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DifficultyEstimator de = new DifficultyEstimator();
		String dir = "c:/MAPS/";
		Collection<File> files = FileUtils.listFiles(
				new File(dir), 
				new String[]{"mircontext"}, true);
		try {
			ResourceSet resSet = new ResourceSetImpl();
			resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
					new XMIResourceFactoryImpl());
			resSet.getPackageRegistry().put(ExtendedmircontextPackage.eNS_URI, 
					ExtendedmircontextPackage.eINSTANCE);
			resSet.getPackageRegistry().put(MissiondescriptionPackage.eNS_URI,					
					MissiondescriptionPackage.eINSTANCE);
			resSet.getPackageRegistry().put(MonitorConfigurationPackage.eNS_URI, 
					MonitorConfigurationPackage.eINSTANCE);
			resSet.getPackageRegistry().put(ObjectcataloguePackage.eNS_URI, 
					ObjectcataloguePackage.eINSTANCE);
			
			ArtifactGenerator a = new ArtifactGenerator(resSet,
					"c:/Users/kyberszittya/rei-workspace/hu.bme.mit.inf.testing.gazebo.catalogue/GazeboCatalogue.objectcatalogue",
					"C:/Users/kyberszittya/rei-workspace/hu.bme.mit.inf.testing.gazebo.catalogue/World/DefaultWorld.sdfstructure"
					);
			File log_csv = new File(dir+"difficulty.csv");
			PrintWriter pw = new PrintWriter(log_csv);
			pw.println("env_name;cnt;area;object_count;floor_count;anim_object_count;global_density;local_density;difficulty");
			int c = 0;			
			for (File f: files) {
				TestRoom t;
				t = CommonUtils.LoadTestroomFromFile(resSet, f.getAbsolutePath());
				t.setName(t.getName()+f.getName());
				MappingResult map_res = a.generateSDF(f.getAbsolutePath(), f.getAbsolutePath(), f.getName());
				t = map_res.getTestroom();
				
				StringBuilder sb = new StringBuilder();
				sb.append(f.getName()+t.getName());
				sb.append(";");
				sb.append(c++);
				sb.append(";");
				sb.append(de.estimateArea(t));
				sb.append(";");
				sb.append(de.ObjectCount(t));
				sb.append(";");
				sb.append(de.FloorCount(t));
				sb.append(";");
				sb.append(de.AnimatedObjectCount(t));
				sb.append(";");
				sb.append(de.estimateGlobalDensity(t));
				sb.append(";");
				sb.append(de.estimateLocalDensity(t));
				sb.append(";");
				sb.append(de.estimateDifficulty(t));
				pw.println(sb.toString());
				ObjectCatalogue catalog = CommonUtils.LoadObjectCatalogue(resSet, 
						"c:/Users/kyberszittya/rei-workspace/hu.bme.mit.inf.testing.gazebo.catalogue/GazeboCatalogue.objectcatalogue"
					);
				a.generateCollectorConfiguration(t, CommonUtils.LoadConfigurationFromFile(resSet,
						"C:\\Users\\kyberszittya\\rei-workspace\\hu.bme.mit.inf.testing.artifact.mircontext.generator\\configuration\\Default.xmi"),
						catalog, map_res.getDyn_objects(), map_res.getCat_map(),
						f.getAbsolutePath());
				System.out.println(sb.toString());
				System.out.println(f.getName());
			}
			pw.close();
		}catch(IOException | ArtifactLoadError | ArtifactGenerationError e) {
			e.printStackTrace();
		} catch (CatalogueLoadError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}