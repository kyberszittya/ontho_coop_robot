package hu.bme.mit.robotics.ontology.plangenerator;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class StorageRoom {
	private final Map<OWLNamedIndividual, Shelf> shelves_owl;
	private final Map<Integer, Shelf> shelves_id;
	private final Map<String, Shelf> shelves_name;
	
	public StorageRoom() {
		shelves_owl = new HashMap<>();
		shelves_id = new HashMap<>();
		shelves_name = new HashMap<>();
	}
	
	public void addShelf(Shelf c, OWLNamedIndividual o, int i, String name) {
		shelves_owl.put(o, c);
		shelves_id.put(i, c);
		shelves_name.put(name, c);
	}
	
	public Shelf getShelf(int id) {
		return shelves_id.get(id);
	}
	
}
