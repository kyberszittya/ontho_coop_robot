package hu.bme.mit.robotics.ontology.plangenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Program {
	public static void main(String[] args) {
		PlanGenerator plangenerator = new PlanGenerator();
		Map<Integer, Vector2> coords_shelves = new HashMap<Integer, Vector2>();
		StorageRoom sr = new StorageRoom();
		try {
			plangenerator.loadOntologyFromFile("c:\\Users\\kyberszittya\\onthology_based_robotics\\ontho_coop_robot\\RobotOnthology_Generated.owl");
			plangenerator.validateOntology();
			plangenerator.validateOntologyObjectAssertions();
			OWLNamedIndividual sroom = plangenerator.getStorageRoom();
			Map<Integer, CubeDimensions> dims = plangenerator.getRoomDimensions();
			Set<OWLNamedIndividual> valid_instances = new HashSet<>(); 
			for (CubeDimensions d: dims.values()) {
				for (OWLNamedIndividual o: plangenerator.getAllShelves(sroom)) {
					Vector2 v = plangenerator.getShelfCoordinates(o, d);
					int id = plangenerator.getShelfId(o);
					int shelf_length = plangenerator.getShelfLength(o);
					coords_shelves.put(id, v);
					Shelf s = new Shelf(shelf_length, v);
					sr.addShelf(s, o, id, o.getIRI().getShortForm());
					List<OWLNamedIndividual> l_c = 
							plangenerator.getAllContainersFromShelf(o);
					for (OWLNamedIndividual c: l_c) {
						int c_id = plangenerator.getShelfId(c);
						String c_name = c.getIRI().getShortForm();
						Vector2 v_c = plangenerator.getContainerCoordinates(s, c);
						//System.out.println(c_name);
						s.addContainerPos(c_name, c_id, c, v_c);
						valid_instances.add(c);
					}
				}
			}
			
			String qs = "MobilePhone";
			try {
				PrintWriter pw = new PrintWriter(new File("C:\\MAPS\\goal.txt"));
			
				for (OWLNamedIndividual o: plangenerator.queryContainersOfWare(qs)) {
					if (valid_instances.contains(o)) {
						int shelfid = plangenerator.getShelfIdOfContainer(o);
						double x = 0.0;
						double y = 0.0;
						double o_z = Math.sqrt(2)/2.0;
						double o_w = Math.sqrt(2)/2.0;
						Vector2 shelfpos = sr.getShelf(shelfid).getShelfPos();
						Vector2 pos = sr.getShelf(shelfid).getContainerPos(o);
						if (pos.getY()>0.0) {
							y += 2.0;
							o_z = -o_z;
						}
						x += shelfpos.getX()+pos.getX();
						y += shelfpos.getY();
						System.out.println(o);
						GoalDescription g = new GoalDescription(x, y, 0.0, 0.0, 0.0, o_z, o_w);
						System.out.println(g);
						pw.println(g);
						
					}
				}
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
