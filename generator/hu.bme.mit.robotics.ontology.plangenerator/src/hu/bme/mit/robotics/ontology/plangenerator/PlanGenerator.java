package hu.bme.mit.robotics.ontology.plangenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

public class PlanGenerator {
	private OWLOntology ontology;
	private OWLOntologyManager manager;
	private OWLDataFactory df;
	private OWLReasonerFactory rf;
	private OWLReasoner reasoner;
	private IRI ior;
	private PrefixManager pm;
	private StorageRoom sr;
	
	public PlanGenerator() {
		manager = OWLManager.createOWLOntologyManager();
		ior = IRI.create("http://www.semanticweb.org/john/ontologies/2018/3/robot-warehouse");
		rf = new StructuralReasonerFactory();
		sr = new StorageRoom();
	}
	
	public OWLNamedIndividual getStorageRoom(){
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		
		OWLClass sc = df.getOWLClass("#StorageRoom", pm);
		for (OWLNamedIndividual o: reasoner.getInstances(sc, true).getFlattened()) {
			if (o.getIRI().getShortForm().equals("Berlin_Storage_StorageRoom_1")) {
				return o;
			}
		}
		return null;
	}
	
	public Set<OWLClass> getLeafNodes(OWLClass o){
		Set<OWLClass> res = new HashSet<>();
		LinkedList<OWLClass> fringe = new LinkedList<>();
		Set<OWLClass> visited = new HashSet<>();
		fringe.add(o);
		while(!fringe.isEmpty()) {
			OWLClass o1 = fringe.pop();
			Set<OWLClass> subclss = reasoner.getSubClasses(o1, true).getFlattened();
			subclss.removeIf(it -> it.isBuiltIn());
			if (subclss.size()>0) {
				for (OWLClass n: subclss) {
					if (!visited.contains(n)) {
						visited.add(n);
						fringe.add(n);
					}
				}
			}
			else {
				res.add(o1);
			}
		}
		
		return res;
	}
	
	public Set<OWLNamedIndividual> getAllShelves(OWLNamedIndividual o){
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		OWLObjectProperty op_inRoom = df.getOWLObjectProperty("#inRoom",pm);
		
		OWLClass shelves = df.getOWLClass("#Shelf", pm);
		Set<OWLNamedIndividual> res = new HashSet<>();
		for(OWLNamedIndividual ow : reasoner.getInstances(shelves,true).getFlattened()) {
			
			if (reasoner.getObjectPropertyValues(ow, op_inRoom).containsEntity(o)) {
				res.add(ow);
			}
		}
		//NodeSet<OWLNamedIndividual> nset = reasoner.getInstances(shelves, true);
		
		//return nset.getFlattened();
		return res;
	}
	
	public Set<OWLNamedIndividual> getAllShelves(){
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		
		OWLClass shelves = df.getOWLClass("#Shelf", pm);
		NodeSet<OWLNamedIndividual> nset = reasoner.getInstances(shelves, true);
		
		return nset.getFlattened();
	}
	
	public Set<OWLNamedIndividual> getAllStorageRooms(){
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		
		OWLClass storagerooms = df.getOWLClass("#StorageRoom", pm);
		NodeSet<OWLNamedIndividual> nset = 
				reasoner.getInstances(storagerooms, true);
		
		return nset.getFlattened();
	}
	
	public Map<Integer,CubeDimensions> getRoomDimensions() {
		Map<Integer, CubeDimensions> dimensions = new HashMap<>();
		Set<OWLNamedIndividual> storage_rooms = getAllStorageRooms();
		for (OWLNamedIndividual o: storage_rooms) {
			Set<OWLDataPropertyAssertionAxiom> dps = 
					ontology.getDataPropertyAssertionAxioms(o);
			double x = 0.0; 
			double y = 0.0;
			int id = 0;
					
			for (OWLDataPropertyAssertionAxiom d: dps) {
				OWLDataPropertyExpression ds = d.getProperty();
				for (OWLDataProperty dp: ds.getDataPropertiesInSignature()) {
					if (dp.getIRI().getShortForm().equals("Length")) {				
						for (OWLLiteral l: reasoner.getDataPropertyValues(o, dp)) {
							x = l.parseInteger();
						}					
					}
					if (dp.getIRI().getShortForm().equals("Width")) {				
						for (OWLLiteral l: reasoner.getDataPropertyValues(o, dp)) {
							y = l.parseInteger();
						}					
					}
					if (dp.getIRI().getShortForm().equals("ID")) {				
						for (OWLLiteral l: reasoner.getDataPropertyValues(o, dp)) {
							//id = l.parseInteger();
							id = 0;
						}
						break;
					}
				}
			}
			dimensions.put(id, new CubeDimensions(x, y, 0.0));
			
		}
		return dimensions;
	}
	
	public Map<String, OWLClass> getWareContainerList() {
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		OWLClass wares = df.getOWLClass("#Wares",pm);
		Set<OWLClass> subclss = getLeafNodes(wares);
		Map<String, OWLClass> res = new HashMap<>();
		for (OWLClass o: subclss) {
			res.put(o.getIRI().getShortForm(), o);
		}
		return res;
	}
	
	public List<OWLNamedIndividual> getAllContainersFromShelf(OWLNamedIndividual o){
		List<OWLNamedIndividual> containers = new ArrayList<>();
		Set<OWLObjectPropertyAssertionAxiom> obsp = 
				ontology.getObjectPropertyAssertionAxioms(o);
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		OWLObjectProperty op = df.getOWLObjectProperty("#isShelfOf",pm);
		for (OWLNamedIndividual ow: reasoner.getObjectPropertyValues(o, op).getFlattened()) {
			containers.add(ow);
		}
		OWLObjectProperty op_shelvedon = df.getOWLObjectProperty("#shelvedOn",pm);
		if (containers.size()==0) {
			for (OWLNamedIndividual ow: getAllContainers()) {
				if (reasoner.getObjectPropertyValues(ow, op_shelvedon).containsEntity(o)) {
					containers.add(ow);
				}
			}
		}
		return containers;
	}
	
	public int getShelfLength(OWLNamedIndividual o) {
		Set<OWLDataPropertyAssertionAxiom> dps = 
				ontology.getDataPropertyAssertionAxioms(o);
		int length = 0;
		for (OWLDataPropertyAssertionAxiom d: dps) {
			OWLDataPropertyExpression ds = d.getProperty();
			for (OWLDataProperty dp: ds.getDataPropertiesInSignature()) {
				if (dp.getIRI().getShortForm().equals("ShelfLength")) {				
					for (OWLLiteral l: reasoner.getDataPropertyValues(o, dp)) {
						length = l.parseInteger();
						return length;
					}
				}
			}
		}
		return 5;
	}
	
	public int getShelfId(OWLNamedIndividual o) {
		Set<OWLDataPropertyAssertionAxiom> dps = 
				ontology.getDataPropertyAssertionAxioms(o);
		int id = 0;
		for (OWLDataPropertyAssertionAxiom d: dps) {
			OWLDataPropertyExpression ds = d.getProperty();
			for (OWLDataProperty dp: ds.getDataPropertiesInSignature()) {
				if (dp.getIRI().getShortForm().equals("ObjectPosition")) {				
					for (OWLLiteral l: reasoner.getDataPropertyValues(o, dp)) {
						id = l.parseInteger();
						return id;
					}
				}
			}
		}
		return 0;
	}
	
	public Vector2 getContainerCoordinates(Shelf sh, OWLNamedIndividual c){
		Set<OWLDataPropertyAssertionAxiom> dps = 
				ontology.getDataPropertyAssertionAxioms(c);
		double x = 0.0;
		double y = 0.0;
		int id = 0;
		for (OWLDataPropertyAssertionAxiom d: dps) {
			OWLDataPropertyExpression ds = d.getProperty();
			for (OWLDataProperty dp: ds.getDataPropertiesInSignature()) {				
				if (dp.getIRI().getShortForm().equals("ObjectPosition")) {				
					for (OWLLiteral l: reasoner.getDataPropertyValues(c, dp)) {
						id = l.parseInteger();
					}
				}
			}
		}
		x = (double)(id%sh.getShelfLength())*0.5;
		y = ((double)(id / sh.getShelfLength())-0.5); 
		Vector2 coords = new Vector2(x, y);
		return coords;
	}
	
	public Vector2 getShelfCoordinates(OWLNamedIndividual o, CubeDimensions dim){
		Set<OWLDataPropertyAssertionAxiom> dps = 
				ontology.getDataPropertyAssertionAxioms(o);
		double x = 0.0;
		double y = 0.0;
		int id = 0;
		int shelflength = 0;
		for (OWLDataPropertyAssertionAxiom d: dps) {
			OWLDataPropertyExpression ds = d.getProperty();
			for (OWLDataProperty dp: ds.getDataPropertiesInSignature()) {
				if (dp.getIRI().getShortForm().equals("ObjectPosition")) {				
					for (OWLLiteral l: reasoner.getDataPropertyValues(o, dp)) {
						id = l.parseInteger();
					}
				}
				else if (dp.getIRI().getShortForm().equals("ShelfLength")) {				
					for (OWLLiteral l: reasoner.getDataPropertyValues(o, dp)) {
						shelflength = l.parseInteger();
					}
				}
			}			
		}
		int shelfdepth = ((int)dim.getDepth()/2);
		x = (double)(id/shelfdepth*shelflength)+shelflength/2.0;
		y = (double)((id-1) % shelfdepth)*2; 
		Vector2 coords = new Vector2(x, y);
		return coords;
	}
	
	public void getDataProperties(OWLNamedIndividual o) {
		Set<OWLDataPropertyAssertionAxiom> dps = 
				ontology.getDataPropertyAssertionAxioms(o);
		for (OWLDataPropertyAssertionAxiom d: dps) {
			OWLDataPropertyExpression ds = d.getProperty();
			for (OWLDataProperty dp: ds.getDataPropertiesInSignature()) {
				for (OWLLiteral l: reasoner.getDataPropertyValues(o, dp)) {
					System.out.println(l.parseInteger());
				}
			}
		}
	}
	
	public void validateOntologyObjectAssertions() {
		reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_ASSERTIONS);
		reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_HIERARCHY);
		if (!reasoner.isConsistent()) {
			throw new InconsistentOntologyException();
		}
	}
	
	public void validateOntology() {
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		if (!reasoner.isConsistent()) {
			throw new InconsistentOntologyException();
		}
	}
	
	public Set<OWLNamedIndividual> getAllWareInstances() {
		Set<OWLNamedIndividual> res = new HashSet<>();
		for (OWLClass w: getWareContainerList().values()) {
			NodeSet<OWLNamedIndividual> nodeset = reasoner.getInstances(w, true);
			res.addAll(nodeset.getFlattened());
		}
		return res;
	} 
	
	public void loadOntologyFromFile(String path) throws OWLOntologyCreationException {
		File f = new File(path);
		
		ontology = manager.loadOntology(IRI.create(f));
		pm = (PrefixDocumentFormat)manager.getOntologyFormat(ontology);
		//pm = new DefaultPrefixManager(null, null,	ior.toString());
		df = manager.getOWLDataFactory();
		reasoner = rf.createReasoner(ontology);
	}
	
	public Set<OWLNamedIndividual> getAllContainers(){
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		Set<OWLNamedIndividual> res = new HashSet<>();
		OWLClass bac = df.getOWLClass("#Barrel",pm);
		res.addAll(reasoner.getInstances(bac, true).getFlattened());
		OWLClass bc = df.getOWLClass("#Box",pm);
		res.addAll(reasoner.getInstances(bc, true).getFlattened());
		return res;
	}
	
	public Set<OWLNamedIndividual> queryContainersOfWare(String name){
		PrefixManager pm = 
				new DefaultPrefixManager(null, null,	ior.toString());
		Set<OWLNamedIndividual> res = new HashSet<>();
		OWLObjectProperty op = df.getOWLObjectProperty("#hasCargo",pm);
		for (OWLNamedIndividual c: getAllContainers()) {
			for (OWLNamedIndividual o: queryWareTypes(name)) {
				if (reasoner.getObjectPropertyValues(c, op).containsEntity(o)) {
					//System.out.println(c+" "+o);
					res.add(c);
				}
			}
		}
		return res;
	}
	
	public int getShelfIdOfContainer(OWLNamedIndividual o){
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_HIERARCHY);
		
		OWLObjectProperty op = df.getOWLObjectProperty("#shelvedOn",pm);
		for (OWLNamedIndividual os: reasoner.getObjectPropertyValues(o, op).getFlattened()) {
			return getShelfId(os);
			
		}
		return 0;
	}
	
	public Set<OWLNamedIndividual> queryWareTypes(String name){
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		OWLClass cla = df.getOWLClass("#"+name, pm);
		
		Set<OWLNamedIndividual> res = new HashSet<>();
		
		LinkedList<OWLClass> fringe = new LinkedList<>();
		Set<OWLClass> visited = new HashSet<>();
		fringe.add(cla);
		while(!fringe.isEmpty()) {
			OWLClass o1 = fringe.pop();
			Set<OWLClass> subclss = reasoner.getSubClasses(o1, true).getFlattened();
			subclss.removeIf(it -> it.isBuiltIn());
			if (subclss.size()>0) {
				for (OWLClass n: subclss) {
					if (!visited.contains(n)) {
						visited.add(n);
						fringe.add(n);
					}
				}
			}
			else {
				res.addAll(reasoner.getInstances(o1, true).getFlattened());
			}
		}
		
		return res;
	}
}
