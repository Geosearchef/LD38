package rendering;

import de.geosearchef.matella.entities.Entity;
import de.geosearchef.matella.models.Model;
import de.geosearchef.matella.shaders.hook.EntityShaderHook;
import update.Updater;

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
		if(Updater.getMouseField() != null)
			((LDEntityShader)shaderProgram).loadHighlight(Updater.getMouseField().getEntity() == entity);
		else
			((LDEntityShader)shaderProgram).loadHighlight(false);
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
