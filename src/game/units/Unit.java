package game.units;

import org.lwjgl.util.vector.Vector3f;

import de.geosearchef.matella.entities.Entity;
import de.geosearchef.matella.models.Model;
import game.Field;
import lombok.Getter;

public class Unit extends Entity {

	private static Model model;
	private @Getter int alliance;
	private @Getter int id;
	private Field field;

	public Unit(int id, Vector3f position, int alliance, Field startingField) {
		super(model, position, new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), false, id);
		this.id = id;
		this.alliance = alliance;
		this.field = startingField;
	}
	
	@Override
	public void update(float d){
		super.update(d);
	}

}
