package hu.bme.mit.inf.testing.artifact.mircontext.generator;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import extendedmircontext.ExtendedmircontextFactory;
import extendedmircontext.Floor;
import extendedmircontext.Human;
import extendedmircontext.Mission;
import extendedmircontext.Robot;
import extendedmircontext.TestRoom;
import hu.bme.mit.inf.mircontext.model.generator.simulatordescription.MapMircontext2SDF;
import objectcatalogue.ObjectCatalogue;


public class ArtifactGenerator {
	private ResourceSet resSet;
	private Resource catalogue;
	private Resource worldRes;
	private MapMircontext2SDF mr2;
	private static DocumentBuilder doc_builder;
	private static TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private static final int human_cnt= 1;
	
	DataCollectorConfiguration dc;
	
	private HashSet<Floor> global_fringe;
	
	public ArtifactGenerator(ResourceSet resSet, String cataloguefile_path, String defaultworld_path) {
		this.resSet = resSet;
		
		global_fringe = new HashSet<Floor>();
		mr2 = new MapMircontext2SDF();
		catalogue = 
				mr2.loadCatalogueResource(cataloguefile_path);
		worldRes =
				mr2.loadWorldResource(defaultworld_path);
		
		try {
			doc_builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			dc = new DataCollectorConfiguration(doc_builder, transformerFactory);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void AddRobot(TestRoom t) {
		int robot_cnt = 0;
		for (Mission m: t.getMissions()) {
			Robot r = ExtendedmircontextFactory.eINSTANCE.createRobot();
			r.setRobot_type("turtlebot3_waffle");
			//r.setFloor((Floor)m.getStart());
			if (robot_cnt==0) {
				r.setName("waffle");				
			}else {
				r.setName("waffle"+robot_cnt);
			}
			
			m.setRobot(r);
			t.getPhysicalobjects().add(r);
			System.out.println("Added new robot: "+r.getName());
		}
	}
	
	
	
	public void generateCollectorConfiguration(TestRoom t, 
			MonitorConfiguration.Configuration monconf, ObjectCatalogue catalog,
			List<String> dyn_objs, Map<String, String> catalog_mapping,
			String outputfilename) {
		try {
			Transformer transformer = transformerFactory.newTransformer();			
			Document launch_doc = DataCollectorConfiguration.generateDataCollectorConfiguration(
					t, monconf, catalog, dyn_objs, catalog_mapping);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			StreamResult result_launch = new StreamResult(new File(outputfilename+".datacollector.xml"));
			transformer.transform(new DOMSource(launch_doc), result_launch);
		}catch(TransformerException te) {
			te.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void generateLaunch(MapMircontext2SDF mr2, String outputfilename) {
		try {
			Transformer transformer = transformerFactory.newTransformer();
			mr2.constructLaunchTree();
			Document launch_doc = mr2.getDocLaunch();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			StreamResult result_launch = new StreamResult(new File(outputfilename+".launch"));
			transformer.transform(new DOMSource(launch_doc), result_launch);
		}catch(TransformerException te) {
			te.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public TestRoom getTestRoom(Resource res) {
		TestRoom t;
		for (EObject o: res.getContents()) {
			if (o instanceof TestRoom) {
				t = (TestRoom)o;
				return t;
			}
		}
		return null;
	}
	
	public void addAnimatedObject(TestRoom t) {
		for (int i = 0; i < human_cnt; i++) {
			Human h = GenerateSimulationArtifact.addHuman(global_fringe, t);
			if (h!= null) {
				t.getPhysicalobjects().add(h);
			}
		}
	}	
	
	
	public static void generateXMLConfig(
			Document mission_document, String outputfilename, String extension) throws TransformerException{
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(mission_document);
		
		StreamResult result = new StreamResult(new File(outputfilename+extension));
		transformer.transform(source, result);	
	}
	
	
	
	public MappingResult generateSDF(String filename, String outputfilename, String prefix) throws ArtifactGenerationError{
		try {
			mr2 = new MapMircontext2SDF();
			Resource res_room = resSet.getResource(
					URI.createFileURI(filename), true);
			
			AddRobot(getTestRoom(res_room));
			CommonUtils.listRobots(getTestRoom(res_room));
			
			
			mr2.setupEnvironmentModel(res_room);
			addAnimatedObject(getTestRoom(res_room));
			Element worldElem = mr2.constructWorld(worldRes);
			mr2.constructSDFTree(catalogue, worldElem);
			Document sdf_doc = mr2.getDoc();
			generateXMLConfig(sdf_doc, outputfilename, ".sdf");
			generateLaunch(mr2, outputfilename);
			Document mission_document = GenerateMissionArtifact.generateMission(
					resSet, doc_builder, mr2, getTestRoom(res_room), outputfilename, prefix);
			generateXMLConfig(mission_document, outputfilename, ".xml");
			
			return new MappingResult(getTestRoom(res_room), mr2.getDyn_objects(), mr2.getCatalogue_mapping());
		} catch(TransformerException te) {
			te.printStackTrace();
		}
		throw new ArtifactGenerationError();
	}
}