package hu.bme.mit.inf.testing.artifact.mircontext.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.ObjectPropertySimplifier;

import extendedmircontext.Hall;
import extendedmircontext.Shelf;

public class CatalogueOntology {
	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private OWLDataFactory df;
	private OWLReasoner reasoner;
	private IRI ior;
	private PrefixManager pm;
	private OWLReasonerFactory rf;
	
	private int cnt_shelf = 0;
	
	public CatalogueOntology() {
		manager = OWLManager.createOWLOntologyManager();
		ior = IRI.create("http://www.semanticweb.org/john/ontologies/2018/3/robot-warehouse");
		rf = new StructuralReasonerFactory();
	}
	
	public OWLNamedIndividual createShelfIndividual(String name, OWLClass o) {
		OWLNamedIndividual co = df.getOWLNamedIndividual(name, pm);
		manager.addAxiom(ontology, 
				df.getOWLClassAssertionAxiom(o, co));
		OWLDataProperty dp_id = df.getOWLDataProperty("ID", pm);
		manager.addAxiom(ontology, df.getOWLDataPropertyAssertionAxiom(dp_id, co, cnt_shelf++));
		OWLDataProperty dp_length = df.getOWLDataProperty("ShelfLength", pm);
		manager.addAxiom(ontology, df.getOWLDataPropertyAssertionAxiom(dp_length, co, 5));
		return co;
	}
	
	public void addShelfRelation(OWLIndividual sh, OWLIndividual co) {
		OWLObjectProperty rel_isShelfOf = df.getOWLObjectProperty("isShelfOf", pm);
		OWLObjectProperty rel_shelvedon = df.getOWLObjectProperty("shelvedOn", pm);
		manager.addAxiom(ontology, df.getOWLObjectPropertyAssertionAxiom(rel_isShelfOf, sh, co));
		manager.addAxiom(ontology, df.getOWLObjectPropertyAssertionAxiom(rel_shelvedon, co, sh));
	}
	
	public OWLNamedIndividual createContainerIndividual(String name, Shelf sh, 
			OWLClass o, OWLNamedIndividual w, int i) {
		OWLNamedIndividual co = df.getOWLNamedIndividual(name, pm);
		manager.addAxiom(ontology, df.getOWLClassAssertionAxiom(o, co));
		OWLObjectProperty rel_hascargo = df.getOWLObjectProperty("hasCargoOf", pm);
		manager.addAxiom(ontology, 
				df.getOWLObjectPropertyAssertionAxiom(rel_hascargo, co, w));
		OWLDataProperty dp_id = df.getOWLDataProperty("ID", pm);
		manager.addAxiom(ontology, df.getOWLDataPropertyAssertionAxiom(dp_id, co, i));
		return co;
	}
	
	public void loadOntologyFromFile(String path) throws OWLOntologyCreationException {
		File f = new File(path);
		
		ontology = manager.loadOntology(IRI.create(f));
		pm = (PrefixDocumentFormat)manager.getOntologyFormat(ontology);
		//pm = new DefaultPrefixManager(null, null,	ior.toString());
		df = manager.getOWLDataFactory();
		reasoner = rf.createReasoner(ontology);
	}
	
	public void saveOntology(String path) throws OWLOntologyStorageException, FileNotFoundException {
		manager.saveOntology(ontology, new RDFXMLDocumentFormat(), 
				new FileOutputStream(path));
	}
	
	public void setObjectsDifferent(Set<OWLNamedIndividual> l) {
		OWLDifferentIndividualsAxiom diffInds =	df.getOWLDifferentIndividualsAxiom(l);
		manager.addAxiom(ontology, diffInds);
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
	
	public Map<String, OWLClass> getUtilityMap(){
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		OWLClass shelf = df.getOWLClass("#Shelf",pm);
		Map<String, OWLClass> res = new HashMap<>();
		res.put(shelf.getIRI().getShortForm(), shelf);
		OWLClass container = df.getOWLClass("#Container",pm);
		res.put(container.getIRI().getShortForm(), container);
		return res;
	}
	
	
	
	public Map<String, OWLNamedIndividual> getAllWareInstanceMap() {
		Map<String, OWLNamedIndividual> res = new HashMap<>();
		for (OWLClass w: getWareContainerList().values()) {
			NodeSet<OWLNamedIndividual> nodeset = reasoner.getInstances(w, true);
			for (OWLNamedIndividual o: nodeset.getFlattened()) {
				res.put(o.getIRI().getShortForm(), o);
			}
			//res.addAll(nodeset.getFlattened());
		}
		return res;
	}
	
	public Set<OWLNamedIndividual> getAllStorageRooms() {
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
	
		OWLClass sr = df.getOWLClass("#StorageRoom",pm);
		return reasoner.getInstances(sr, true).getFlattened();
	}
	
	
	
	public Set<OWLNamedIndividual> getAllShelves(){
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		OWLClass sr = df.getOWLClass("#Shelf",pm);
		return reasoner.getInstances(sr, true).getFlattened();
	}
	
	public String getLabel(OWLNamedIndividual o) {
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		OWLObjectProperty pe = df.getOWLObjectProperty("#hasCargo", pm);
		for (OWLNamedIndividual ow: reasoner.getObjectPropertyValues(o, pe).getFlattened()) {
			return ow.getIRI().getShortForm();
		}
		return "";
	}
	
	public boolean isPlacedOn(OWLNamedIndividual sh, OWLNamedIndividual o) {
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		OWLObjectProperty pe = df.getOWLObjectProperty("#shelvedOn", pm);
		if (reasoner.getObjectPropertyValues(o, pe).containsEntity(sh)) {
			return true;
		}
		return false;
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
	
	public boolean isShelfInRoom(OWLNamedIndividual sh, OWLNamedIndividual o) {
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		OWLObjectProperty pe = df.getOWLObjectProperty("#inRoom", pm);
		if (reasoner.getObjectPropertyValues(sh, pe).containsEntity(o)) {
			return true;
		}
		return false;
	}
	
	public int roomLength(OWLNamedIndividual o) {
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		OWLDataProperty dp = df.getOWLDataProperty("#Length", pm);
		for (OWLLiteral ol: reasoner.getDataPropertyValues(o, dp)) {
			return ol.parseInteger();
		}
		return 0; 
	}
	
	public int roomWidth(OWLNamedIndividual o) {
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		OWLDataProperty dp = df.getOWLDataProperty("#Width", pm);
		for (OWLLiteral ol: reasoner.getDataPropertyValues(o, dp)) {
			return ol.parseInteger();
		}
		return 0; 
	}
	
	
	
	public Set<OWLNamedIndividual> getAllContainers(){
		PrefixManager pm = new DefaultPrefixManager(null, null,	ior.toString());
		Set<OWLNamedIndividual> res = new HashSet<>();
		OWLClass sbox = df.getOWLClass("#Barrel",pm);
		res.addAll(reasoner.getInstances(sbox, true).getFlattened());
		OWLClass sbarrel = df.getOWLClass("#Box",pm);
		res.addAll(reasoner.getInstances(sbarrel, true).getFlattened());
		return res;
	}
	
	public Set<OWLNamedIndividual> getAllWareInstances() {
		Set<OWLNamedIndividual> res = new HashSet<>();
		for (OWLClass w: getWareContainerList().values()) {
			NodeSet<OWLNamedIndividual> nodeset = reasoner.getInstances(w, true);
			res.addAll(nodeset.getFlattened());
		}
		return res;
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
	
	
	
	public Set<OWLNamedIndividual> getStorageRooms(){
		OWLClass roomclass = df.getOWLClass("#StorageRoom",pm);
		NodeSet<OWLNamedIndividual> wares = reasoner.getInstances(roomclass, true);
		
		return wares.getFlattened();
	}
	
	public void validateOntology() {
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		if (!reasoner.isConsistent()) {
			throw new InconsistentOntologyException();
		}
	}
	
}
