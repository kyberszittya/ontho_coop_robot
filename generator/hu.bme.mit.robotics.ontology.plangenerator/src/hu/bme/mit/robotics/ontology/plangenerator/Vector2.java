package hu.bme.mit.robotics.ontology.plangenerator;

public class Vector2 {
	private final double x;
	private final double y;
	
	public Vector2(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return x+" "+y;
	}
	

}
