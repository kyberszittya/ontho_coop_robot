package hu.bme.mit.robotics.ontology.plangenerator;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class Shelf {
	private final Vector2 pos;
	private final int shelf_length;
	private final Map<String, Vector2> containers;
	private final Map<Integer, Vector2> containers_id;
	private final Map<OWLNamedIndividual, Vector2> containers_o;
	
	public Shelf(int shelf_length, Vector2 v) {
		containers = new HashMap<>();
		containers_id = new HashMap<>();
		containers_o = new HashMap<>();
		this.shelf_length = shelf_length;
		pos = v;
	}
	
	public void addContainerPos(String s, int id, OWLNamedIndividual o, Vector2 pos) {
		containers.put(s, pos);
		containers_id.put(id, pos);
		containers_o.put(o, pos);
	}
	
	public Vector2 getContainerPos(int i) {
		return containers_id.get(i);
	}
	
	public Vector2 getContainerPos(String s) {
		return containers.get(s);
	}
	
	public Vector2 getContainerPos(OWLNamedIndividual o) {
		return containers_o.get(o);
	}
	
	public Vector2 getShelfPos() {
		return pos;
	}
	
	public int getShelfLength() {
		return shelf_length;
	}
	
}
