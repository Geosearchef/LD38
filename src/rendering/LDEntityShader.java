package rendering;

import de.geosearchef.matella.entities.EntityShader;

public class LDEntityShader extends EntityShader {
	
	private static final String VERTEX_FILE = "/shaders/LDentity.vert";
	private static final String FRAGMENT_FILE = "/shaders/LDentity.frag";
	
	
	private int location_highlight;
	
	public LDEntityShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		
		location_highlight = super.getUniformLocation("highlight");
	}
	
	public void loadHighlight(boolean highlight) {
		super.loadInt(location_highlight, highlight ? 1 : 0);
	}
}
