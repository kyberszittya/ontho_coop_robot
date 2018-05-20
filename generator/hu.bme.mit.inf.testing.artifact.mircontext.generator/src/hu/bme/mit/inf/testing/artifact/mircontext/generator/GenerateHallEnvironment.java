package hu.bme.mit.inf.testing.artifact.mircontext.generator;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.w3c.dom.Document;

import extendedmircontext.Container;
import extendedmircontext.ExtendedmircontextFactory;
import extendedmircontext.ExtendedmircontextPackage;
import extendedmircontext.Floor;
import extendedmircontext.Hall;
import extendedmircontext.PhysicalEntity;
import extendedmircontext.Shelf;
import extendedmircontext.TestRoom;
import extendedmircontext.Wall;
import gazeboobject.SimulatorObject;
import hu.bme.mit.inf.testing.testroom.model.mapping.serializer.urdf.SdfMapper;
import hu.bme.mit.inf.testing.testroom.model.mapping.simobj.TestRoomRemapSimulatorContext;
import hu.bme.mit.inf.testing.testroom.model.mapping.tree.RoomTree;
import objectcatalogue.ObjectCatalogue;
import objectcatalogue.ObjectcataloguePackage;

public class GenerateHallEnvironment {
	
	
	public static void setupFloor(Floor f, String name) {
		f.setName(name);
		f.setStatic(true);
	}
	
	public static void setupWall(Wall f, String name) {
		f.setName(name);
		f.setStatic(true);
	}
	
	public static int SHELF_LENGTH = 5;
	
	public static void addContainersToShelf(TestRoom t, Shelf sh) {
		for(int i = 0; i < SHELF_LENGTH; i++) {
			for(int j = 0; j < 2; j++) {
				Container c = ExtendedmircontextFactory.eINSTANCE.createContainer();
				c.setName("container_"+i+"_"+j+"_"+sh.getName());
				sh.getContainer().add(c);				
				t.getPhysicalobjects().add(c);
			}
		}
	}
	
	public static Shelf addShelfToFloor(Floor f, OWLNamedIndividual o) {
		Shelf sh = ExtendedmircontextFactory.eINSTANCE.createShelf();		
		sh.setName(o.getIRI().getShortForm());
		f.setPhysicalobject(sh);
		return sh;
	}
	
	public static void populateWithShelfes(TestRoom t, Floor[][] floors, int height, int width) {
		for (int y = 1; y < height-1; y+=2) {
			for (int x = SHELF_LENGTH/2; x < width-1; x+=SHELF_LENGTH) {
				Shelf sh = ExtendedmircontextFactory.eINSTANCE.createShelf();
				sh.setName("shelf_"+y+"_"+x);
				addContainersToShelf(t, sh);
				floors[x][y].setPhysicalobject(sh);
				t.getPhysicalobjects().add(sh);
			}
		}
		
	}
	
	// No shelves
	public static void generateRectangleHallRoomNoShelves(TestRoom targetroom, int width, int height) {
		Floor[][] floors = new Floor[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Floor f = ExtendedmircontextFactory.eINSTANCE.createFloor();
				targetroom.getBuildingblocks().add(f);
				floors[x][y] = f;
				
				setupFloor(f, "floor_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Floor f = floors[x][y];
				if (x > 0) {
					f.setLeft(floors[x-1][y]);
				}
				else {
					Wall w = ExtendedmircontextFactory.eINSTANCE.createWall();
					setupWall(w, "wall_left_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
					f.setLeft(w);
				}
				if (x < width-1) {
					f.setRight(floors[x+1][y]);					
				}
				else {
					Wall w = ExtendedmircontextFactory.eINSTANCE.createWall();
					setupWall(w, "wall_right_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
					f.setRight(w);
				}
				if (y > 0) {
					f.setBottom(floors[x][y-1]);
				}
				else {
					Wall w = ExtendedmircontextFactory.eINSTANCE.createWall();
					setupWall(w, "wall_bottom_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
					f.setBottom(w);
				}
				if (y < height-1) {
					f.setTop(floors[x][y+1]);
				}
				else {
					Wall w = ExtendedmircontextFactory.eINSTANCE.createWall();
					setupWall(w, "wall_top_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
					f.setTop(w);
				}
				
			}
		}
		
	}
	
	// All units are in meters
		public static void generateRectangleHallRoom(TestRoom targetroom, int width, int height) {
			Floor[][] floors = new Floor[width][height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					Floor f = ExtendedmircontextFactory.eINSTANCE.createFloor();
					targetroom.getBuildingblocks().add(f);
					floors[x][y] = f;
					
					setupFloor(f, "floor_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
				}
			}
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					Floor f = floors[x][y];
					if (x > 0) {
						f.setLeft(floors[x-1][y]);
					}
					else {
						Wall w = ExtendedmircontextFactory.eINSTANCE.createWall();
						setupWall(w, "wall_left_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
						f.setLeft(w);
					}
					if (x < width-1) {
						f.setRight(floors[x+1][y]);					
					}
					else {
						Wall w = ExtendedmircontextFactory.eINSTANCE.createWall();
						setupWall(w, "wall_right_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
						f.setRight(w);
					}
					if (y > 0) {
						f.setBottom(floors[x][y-1]);
					}
					else {
						Wall w = ExtendedmircontextFactory.eINSTANCE.createWall();
						setupWall(w, "wall_bottom_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
						f.setBottom(w);
					}
					if (y < height-1) {
						f.setTop(floors[x][y+1]);
					}
					else {
						Wall w = ExtendedmircontextFactory.eINSTANCE.createWall();
						setupWall(w, "wall_top_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
						f.setTop(w);
					}
					
				}
			}
			populateWithShelfes(targetroom, floors, height, width);
		}
	
	// All units are in meters
	public static void generateRectangleHallRoom(TestRoom targetroom, 
			int width, int height, 
			Set<OWLNamedIndividual> shelves,
			CatalogueOntology cat_onto) {
		Floor[][] floors = new Floor[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Floor f = ExtendedmircontextFactory.eINSTANCE.createFloor();
				targetroom.getBuildingblocks().add(f);
				floors[x][y] = f;
				
				setupFloor(f, "floor_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Floor f = floors[x][y];
				if (x > 0) {
					f.setLeft(floors[x-1][y]);
				}
				else {
					Wall w = ExtendedmircontextFactory.eINSTANCE.createWall();
					setupWall(w, "wall_left_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
					f.setLeft(w);
				}
				if (x < width-1) {
					f.setRight(floors[x+1][y]);					
				}
				else {
					Wall w = ExtendedmircontextFactory.eINSTANCE.createWall();
					setupWall(w, "wall_right_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
					f.setRight(w);
				}
				if (y > 0) {
					f.setBottom(floors[x][y-1]);
				}
				else {
					Wall w = ExtendedmircontextFactory.eINSTANCE.createWall();
					setupWall(w, "wall_bottom_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
					f.setBottom(w);
				}
				if (y < height-1) {
					f.setTop(floors[x][y+1]);
				}
				else {
					Wall w = ExtendedmircontextFactory.eINSTANCE.createWall();
					setupWall(w, "wall_top_"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+targetroom.getName());
					f.setTop(w);
				}
				
			}
		}
		//populateWithShelfes(targetroom, floors, height, width);
		int s_x = 1;
		int s_y = 1;
		for (OWLNamedIndividual o: shelves) {			
			Shelf sh = addShelfToFloor(floors[s_x][s_y], o);
			//addContainersToShelf(o);
			s_y += 2; 
			if (s_y > height) {
				s_y = 1;
				s_x += SHELF_LENGTH;
			}
			OWLNamedIndividual[] container_array = new OWLNamedIndividual[10];	
			for (OWLNamedIndividual os: cat_onto.getAllContainers()) {				
				if (cat_onto.isPlacedOn(o, os)) {
					container_array[cat_onto.getShelfId(os)]= os;
				}
			}
			for (OWLNamedIndividual os: Arrays.asList(container_array)) {
				Container c = ExtendedmircontextFactory.eINSTANCE.createContainer();
				c.setName(os.getIRI().getShortForm());
				c.setLabel(cat_onto.getLabel(os));
				sh.getContainer().add(c);
			}
		}
		
	}
	
	//public static List<OWLNa> 
	
	public static TestRoom convertIndividualToTestRoom(OWLNamedIndividual o, 
			int width, int length, Set<OWLNamedIndividual> shelves, 
			CatalogueOntology cat_onto) {
		TestRoom t = ExtendedmircontextFactory.eINSTANCE.createTestRoom();
		t.setName(o.getIRI().getShortForm());
		generateRectangleHallRoom(t, width, length, shelves, cat_onto);
		return t;	
	}
	
	public static TestRoom convertIndividualToTestRoom(OWLNamedIndividual o, int width, int length) {
		TestRoom t = ExtendedmircontextFactory.eINSTANCE.createTestRoom();
		t.setName(o.getIRI().getShortForm());
		generateRectangleHallRoom(t, width, length);
		return t;	
	}
	
	public static Hall generateHall(String nameprefix, int width, int height, int roomcnt) {
		Hall h = ExtendedmircontextFactory.eINSTANCE.createHall();
		h.setName(nameprefix);
		TestRoom t_ref = null;
		for (int i = 0; i < roomcnt; i++) {
			TestRoom t = ExtendedmircontextFactory.eINSTANCE.createTestRoom();
			if (t_ref==null) {
				t_ref = t;
			}else {
				t_ref.setSouth(t);
			}
			t.setName(nameprefix+"_"+i);
			generateRectangleHallRoom(t, width, height);
			h.getTestroom().add(t);
		}
		
		return h;
	}
	
	public static void main(String[] args) {
		ResourceSet resset = new ResourceSetImpl();
		resset.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				Resource.Factory.Registry.DEFAULT_EXTENSION,
				new XMIResourceFactoryImpl());
		resset.getPackageRegistry().put(
				ObjectcataloguePackage.eNS_URI, 
				ObjectcataloguePackage.eINSTANCE);
		resset.getPackageRegistry().put(
				ExtendedmircontextPackage.eNS_URI,				
				ExtendedmircontextPackage.eINSTANCE);
		CatalogueOntology cat_onto = new CatalogueOntology();
		//Map<String, OWLClass> containers = new HashMap<>();
		Map<String, OWLNamedIndividual> containers = new HashMap<>();
		Map<String, OWLClass> containers_util = new HashMap<>();
		try {
			cat_onto.loadOntologyFromFile("c:\\Users\\kyberszittya\\onthology_based_robotics\\ontho_coop_robot\\RobotOnthology_Generated.owl");
			/*
			for (OWLNamedIndividual o: cat_onto.getAllWareInstances()) {
				System.out.println(o); 
			}
			*/
			containers = cat_onto.getAllWareInstanceMap();
			containers_util = cat_onto.getUtilityMap();
		} catch (OWLOntologyCreationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		URI catalogueURI = URI.createFileURI("C:\\Users\\kyberszittya\\rei-workspace\\hu.bme.mit.inf.testing.gazebo.catalogue\\GazeboCatalogue.objectcatalogue");
		Resource res = resset.getResource(catalogueURI, true);
		ObjectCatalogue catalogue = null;
		for (EObject eo: res.getContents()) {
			if (eo instanceof ObjectCatalogue) {
				catalogue = (ObjectCatalogue)eo;
				System.out.println("Loaded catalogue file: " + catalogueURI);
				break;
			}
		}
		
		DocumentBuilder dfac;
		try {
			dfac = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
						
			Set<OWLNamedIndividual> storagerooms = cat_onto.getStorageRooms();
			for (OWLNamedIndividual o: storagerooms) {
				System.out.println(o);
			}
			
			//Hall h = generateHall("testhall", 10, 10, 1);
			Document doc = dfac.newDocument();
			
			SdfMapper mapper = new SdfMapper(doc);
			List<String> labels = mapper.initializeContainerNameList(containers.keySet());
			Random r = new Random();
			
			LinkedList<TestRoomNode> fringeRoom = new LinkedList<>();
			List<OWLNamedIndividual> rooms = new ArrayList<>(cat_onto.getAllStorageRooms());
			OWLNamedIndividual or = rooms.get(0);
			Set<OWLNamedIndividual> shelves = new HashSet<OWLNamedIndividual>();
			for (OWLNamedIndividual o: cat_onto.getAllShelves()) {
				if (cat_onto.isShelfInRoom(o, or)) {
					shelves.add(o);
				}
			}
			fringeRoom.add(new TestRoomNode(
					convertIndividualToTestRoom(or, 
							cat_onto.roomWidth(or), cat_onto.roomLength(or),
							shelves, cat_onto), 
					0,0, or)
			);
			List<OWLNamedIndividual> wares = new ArrayList<>();
			wares.addAll(cat_onto.getAllWareInstances());
			
			while(!fringeRoom.isEmpty()) {
				
				TestRoomNode tnode = fringeRoom.pop();
				Floor f = (Floor)(tnode.getT().getBuildingblocks().get(0));
				RoomTree rt = new RoomTree(f, tnode.getX(), tnode.getY());
				for (PhysicalEntity p: tnode.getT().getPhysicalobjects()) {
					if (p instanceof Container) {
						((Container) p).setLabel(labels.get(r.nextInt(labels.size())));
					}
				}
				
				rt.traverse();
				TestRoomRemapSimulatorContext mapping = new TestRoomRemapSimulatorContext();
				mapping.setupMapping(catalogue);
				
				for (SimulatorObject so: mapping.mapRoomTree(rt)) {
					mapper.appendNewSimobjToTree(so);
				}
				
				/*
				Set<OWLNamedIndividual> diff_objects = new HashSet<>();
				
				for (PhysicalEntity p: tnode.getT().getPhysicalobjects()) {
					if (p instanceof Shelf) {
						
						OWLNamedIndividual sh = cat_onto.createShelfIndividual(
								p.getName(), 
								containers_util.get("Shelf"));
						diff_objects.add(sh);
						int cnt_container = 0;
						for (Container co: ((Shelf) p).getContainer()) {
							OWLNamedIndividual sh_co = cat_onto.createContainerIndividual(
								co.getName(), (Shelf)p, containers_util.get("Container"),
								containers.get(co.getLabel()), cnt_container);
							cat_onto.addShelfRelation(sh, sh_co);
							diff_objects.add(sh_co);
							cnt_container++;
						}
					}
				}
				cat_onto.setObjectsDifferent(diff_objects);
				*/
				
			}
			
			mapper.writeToPath("C:/MAPS/test.sdf");
			System.out.println("Saved SDF environment description for Gazebo");
			cat_onto.saveOntology("C:/MAPS/instance.owl");
			System.out.println("Saved generated ontology with instances");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
