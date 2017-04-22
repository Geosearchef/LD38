package game.units;

import org.lwjgl.util.vector.Vector3f;

import de.geosearchef.matella.entities.Entity;
import de.geosearchef.matella.models.Model;
import game.Field;
import lombok.Getter;
import lombok.Setter;
import rendering.Renderer;

public abstract class Unit extends Entity {

	private @Getter int alliance;
	private Field field;
	private @Getter @Setter Vector3f relativePosition = new Vector3f();//pos relative to current field's entity

	public Unit(Vector3f position, int alliance, Field startingField) {
		super(null, position, new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), false);

		this.setModel(Renderer.models.stream().filter(m -> m.getName().equals(getModelName())).findAny().get());

		this.alliance = alliance;
		this.field = startingField;
		this.field.units.add(this);
	}

	@Override
	public void update(float d) {
		
		relativePosition.x += getVelocity().x * d;
		relativePosition.y += getVelocity().y * d;
		relativePosition.z += getVelocity().z * d;
		
		this.setPosition(Vector3f.add(relativePosition, field.getEntity().getPosition(), null));
		
		
		//TODO: adapt to relative positions
		float distance = Math
				.abs(this.getPosition().x * this.getPosition().z - this.field.getPosX() * this.field.getPosY());
		Field field = this.field;

		for (Field f : this.field.getNeighbors()) {
			float distanceTemp = Math.abs(this.getPosition().x * this.getPosition().z - f.getPosX() * f.getPosY());
			if (distanceTemp > distance) {
				distance = distanceTemp;
				field = f;
			}
		}
		
		//TODO: only when switching field
		//TODO: when switching field adapt relative position
		this.field.units.remove(this);
		this.field = field;
		this.field.units.add(this);
		System.out.println("(" + this.field.getRawPosX() + ", " + this.field.getRawPosY() + ")");

		this.getPosition().y = this.field.getHeight();

	}

	public abstract String getModelName();
}
