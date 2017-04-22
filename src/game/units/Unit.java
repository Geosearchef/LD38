package game.units;

import org.lwjgl.util.vector.Vector3f;

import de.geosearchef.matella.entities.Entity;
import de.geosearchef.matella.models.Model;
import game.Field;
import lombok.Getter;
import rendering.Renderer;

public abstract class Unit extends Entity {

	private @Getter int alliance;
	private @Getter int id;
	private Field field;

	public Unit(Vector3f position, int alliance, Field startingField) {
		super(null, position, new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), false);

		this.setModel(Renderer.models.stream().filter(m -> m.getName().equals(getModelName())).findAny().get());

		this.id = id;
		this.alliance = alliance;
		this.field = startingField;
		this.field.units.add(this);
	}

	@Override
	public abstract void update(float d);

	public abstract String getModelName();
}
