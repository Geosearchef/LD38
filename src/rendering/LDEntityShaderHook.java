package rendering;

import de.geosearchef.matella.entities.Entity;
import de.geosearchef.matella.models.Model;
import de.geosearchef.matella.shaders.hook.EntityShaderHook;
import input.Input;
import update.Updater;
import util.pathfinding.Path;

public class LDEntityShaderHook extends EntityShaderHook {
	
	public LDEntityShaderHook() {
		this.shaderProgram = new LDEntityShader();
	}
	
	
	@Override
	public void prepareTexturedModel(Model model) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareInstance(Entity entity) {
		if(Input.getMouseField() != null)
			((LDEntityShader)shaderProgram).loadHighlight(Input.getMouseField().getEntity() == entity);
		else
			((LDEntityShader)shaderProgram).loadHighlight(false);
		
		if(Input.wallBuildStart != null) {
			if(Input.wallBuildStart.getEntity() == entity || Input.wallBuildEnd.getEntity() == entity || (Input.wallBuildPath != null && Input.wallBuildPath.getFields().stream().anyMatch(f -> f.getEntity() == entity))) {
				((LDEntityShader)shaderProgram).loadHighlight(true);
			}
		}
	}

	@Override
	public void bindTextures() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initShader() {
		// TODO Auto-generated method stub
		
	}

}
