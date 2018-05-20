package hu.bme.mit.inf.testing.artifact.mircontext.generator;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class TestProgramOntology {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CatalogueOntology cat_onto = new CatalogueOntology();
		try {
			cat_onto.loadOntologyFromFile("c:\\Users\\kyberszittya\\onthology_based_robotics\\ontho_coop_robot\\RobotSemantic.owl");
			cat_onto.getWareContainerList();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
