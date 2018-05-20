package hu.bme.mit.robotics.ontology.plangenerator;

public class CubeDimensions {
	private final double width;
	private final double depth;
	private final double height;
	
	public double getWidth() {
		return width;
	}
	public double getDepth() {
		return depth;
	}
	public double getHeight() {
		return height;
	}
	public CubeDimensions(double width, double depth, double height) {
		super();
		this.width = width;
		this.depth = depth;
		this.height = height;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return width+" "+depth+" "+height+" ";
	}

}
