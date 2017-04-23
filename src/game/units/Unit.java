package game.units;

import org.lwjgl.util.vector.Vector3f;

import de.geosearchef.matella.entities.Entity;
import game.Field;
import game.Game;
import lombok.Getter;
import lombok.Setter;
import rendering.Renderer;

public abstract class Unit extends Entity {

    private @Getter Alliance alliance;
    private Field field;
    // pos relative to current field's entity
    private @Getter @Setter Vector3f relativePosition = new Vector3f((float) (Math.random() - 0.5)/2, 0,
	    (float) (Math.random() - 0.5)/2);
    @Getter
    int health;

    public Unit(Alliance alliance, Field startingField) {
	super(null, new Vector3f(startingField.getPosX(), startingField.getHeight(), startingField.getPosY()),
		new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), false);

	this.setModel(Renderer.models.stream().filter(m -> m.getName().equals(getModelName())).findAny().get());

	this.alliance = alliance;
	this.field = startingField;
	this.field.units.add(this);
    }

    @Override
    public void update(float d) {

	// relative position
	relativePosition.x += getVelocity().x * d;
	relativePosition.y += getVelocity().y * d;
	relativePosition.z += getVelocity().z * d;

	this.setPosition(Vector3f.add(relativePosition, field.getEntity().getPosition(), null));
	this.getPosition().y = this.field.getHeight();

	// did field change?
	float x = (this.getPosition().x - this.field.getPosX());
	float y = (this.getPosition().z - this.field.getPosY());
	float distance = x * x + y * y;
	Field field = this.field;

	for (Field f : this.field.getNeighbors()) {
	    x = (this.getPosition().x - f.getPosX());
	    y = (this.getPosition().z - f.getPosY());
	    float distanceTemp = x * x + y * y;
	    if (distanceTemp < distance) {
		distance = distanceTemp;
		field = f;
	    }
	}

	// if field changed
	if (this.field != field) {

	    this.relativePosition = Vector3f
		    .add(this.relativePosition,
			    new Vector3f(this.field.getPosX() - field.getPosX(),
				    this.field.getHeight() - field.getHeight(), this.field.getPosY() - field.getPosY()),
			    null);

	    if (this.relativePosition.x > Game.MAP_SIZE_X)
		this.relativePosition.x -= Game.MAP_SIZE_X;
	    if (this.relativePosition.z > Game.MAP_SIZE_Y)
		this.relativePosition.z -= Game.MAP_SIZE_Y;

	    if (this.relativePosition.x < -Game.MAP_SIZE_X)
		this.relativePosition.x += Game.MAP_SIZE_X;
	    if (this.relativePosition.z < -Game.MAP_SIZE_Y)
		this.relativePosition.z += Game.MAP_SIZE_Y;

	    this.field.units.remove(this);
	    this.field = field;
	    this.field.units.add(this);
	    // System.out.println("(" + this.field.getRawPosX() + ", " +
	    // this.field.getRawPosY() + ")");

	}

    }

    public abstract String getModelName();

    public void damage(int damage) {
	this.health -= damage;
	if (health < 0) {
	    this.kill();
	}
    }

    public void kill() {
	game.Game.units.remove(this);
	this.field.units.remove(this);
    }
}
