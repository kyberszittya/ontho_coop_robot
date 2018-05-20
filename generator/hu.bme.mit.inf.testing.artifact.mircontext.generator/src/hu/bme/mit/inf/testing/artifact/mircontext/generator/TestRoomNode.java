package hu.bme.mit.inf.testing.artifact.mircontext.generator;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import extendedmircontext.TestRoom;

public class TestRoomNode {
	private final TestRoom t;
	private final int x;
	private final int y;
	private final TestRoomNode parent;
	private final OWLNamedIndividual o;
	
	public TestRoomNode(TestRoom t, int x, int y, OWLNamedIndividual o) {
		this.t = t;
		this.x = x;
		this.y = y;
		this.o = o;
		parent = null;
	}
	
	public TestRoomNode(TestRoom t, int x, int y, OWLNamedIndividual o, TestRoomNode parent) {
		this.t = t;
		this.x = x;
		this.y = y;
		this.o = o;
		this.parent = parent;
	}

	public TestRoom getT() {
		return t;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public OWLNamedIndividual getIndividual() {
		return o;
	}
	
}
