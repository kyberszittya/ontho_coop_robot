package hu.bme.mit.robotics.ontology.plangenerator;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class Container {
	private final Shelf sh;
	private final OWLNamedIndividual o;
	
	public Container(Shelf s, OWLNamedIndividual o) {
		sh = s;
		this.o = o;
	}

}
