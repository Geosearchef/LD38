package game.units;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.geosearchef.matella.entities.Entity;
import game.Field;
import game.Game;
import game.units.tasks.AICalculation;
import game.units.tasks.Task;
import lombok.Getter;
import lombok.Setter;
import rendering.Renderer;

public abstract class Unit extends Entity {

	private @Getter Alliance alliance;
	private @Getter Field field;
	// pos relative to current field's entity
	private @Getter @Setter Vector3f relativePosition = new Vector3f((float) (Math.random() - 0.5) / 2, 0, (float) (Math.random() - 0.5) / 2);
	protected @Getter float health;
	private @Getter @Setter Task task;
	private @Setter CompletableFuture<Task> aiCalculation;
	protected @Getter float damage = 0;

	public Unit(Alliance alliance, Field startingField) {
		super(null, new Vector3f(startingField.getPosX(), startingField.getHeight(), startingField.getPosY()), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), false);

		this.setModel(Renderer.models.stream().filter(m -> m.getName().equals(getModelName())).findAny().get());

		this.alliance = alliance;
		this.field = startingField;
		this.field.units.add(this);
		this.setColor(this.alliance.getColor());
	}

	@Override
	public void update(float d) {

		// relative position
		relativePosition.x += getVelocity().x * d;
		relativePosition.y += getVelocity().y * d;
		relativePosition.z += getVelocity().z * d;

		if (this.relativePosition.x > Game.MAP_SIZE_X)
			this.relativePosition.x -= Game.MAP_SIZE_X;
		if (this.relativePosition.z > Game.MAP_SIZE_Y)
			this.relativePosition.z -= Game.MAP_SIZE_Y;

		if (this.relativePosition.x < -Game.MAP_SIZE_X)
			this.relativePosition.x += Game.MAP_SIZE_X;
		if (this.relativePosition.z < -Game.MAP_SIZE_Y)
			this.relativePosition.z += Game.MAP_SIZE_Y;

		this.setPosition(Vector3f.add(relativePosition, field.getEntity().getPosition(), null));
		this.getPosition().y += (this.field.getHeight() - this.getPosition().y) * d * 3f;

		// did field change?
		float x = (this.getPosition().x - this.field.getPosX());
		float y = (this.getPosition().z - this.field.getPosY());
		float distance = x * x + y * y;
		Field field = this.field;

		for (Field f : this.field.getNeighbors()) {
			x = (this.getPosition().x - f.getPosX());
			if (x < -game.Game.MAP_SIZE_X / 2)
				x += game.Game.MAP_SIZE_X;
			if (x > game.Game.MAP_SIZE_X / 2)
				x -= game.Game.MAP_SIZE_X;
			y = (this.getPosition().z - f.getPosY());
			if (y < -game.Game.MAP_SIZE_Y / 2)
				y += game.Game.MAP_SIZE_Y;
			if (y > game.Game.MAP_SIZE_Y / 2)
				y -= game.Game.MAP_SIZE_Y;

			float distanceTemp = x * x + y * y;
			if (distanceTemp < distance) {
				distance = distanceTemp;
				field = f;
			}
		}

		// if field changed
		if (this.field != field) {

			Vector3f fieldToFieldDistance = new Vector3f(this.field.getPosX() - field.getPosX(), this.field.getHeight() - field.getHeight(), this.field.getPosY() - field.getPosY());

			if (fieldToFieldDistance.x < -game.Game.MAP_SIZE_X / 2)
				fieldToFieldDistance.x += game.Game.MAP_SIZE_X;
			if (fieldToFieldDistance.x > game.Game.MAP_SIZE_X / 2)
				fieldToFieldDistance.x -= game.Game.MAP_SIZE_X;

			if (fieldToFieldDistance.z < -game.Game.MAP_SIZE_Y / 2)
				fieldToFieldDistance.z += game.Game.MAP_SIZE_Y;
			if (fieldToFieldDistance.z > game.Game.MAP_SIZE_Y / 2)
				fieldToFieldDistance.z -= game.Game.MAP_SIZE_Y;

			this.relativePosition = Vector3f.add(this.relativePosition, fieldToFieldDistance, null);

			this.field.units.remove(this);
			this.field = field;
			this.field.units.add(this);
			// System.out.println("(" + this.field.getRawPosX() + ", " +
			// this.field.getRawPosY() + ")");

		}

		// Execute current task
		if (this.task != null) {
			this.task.update(d);
			if (this.task.isFinished())
				this.task = null;
		}

		// Think
		if (this.task == null && this.aiCalculation == null) {
			this.aiCalculation = CompletableFuture.supplyAsync(() -> AICalculation.calculate(this));
		} else if (this.task == null && this.aiCalculation.isDone()) {
			try {
				this.task = aiCalculation.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.aiCalculation = null;
			if (this.task != null)
				this.task.setUnit(this);
		}
	}

	public abstract String getModelName();

	public void damage(float damage) {
		this.health -= damage;
	}
	
	public void kill() {
		this.field.units.remove(this);
	}
	
	public boolean isDead() {
		return health < 0;
	}

	public void move(Field dest) {
		if (dest == this.field)
			return;

		for (int i = 0; i < this.field.getNeighbors().length; i++) {
			if (this.field.getNeighbors()[i] == dest) {
				Vector3f target = movementDest[i];
				this.setVelocity((Vector3f) Vector3f.sub(movementDest[i], this.relativePosition, null).normalise());
				break;
			}
		}
	}

	private static Vector3f[] movementDest = new Vector3f[] { new Vector3f((float) 0.5f * Field.DIMENSIONS.y * (float) Math.sqrt(0.75), 0, (float) Math.sqrt(3) / 2f * Field.DIMENSIONS.y * (float) Math.sqrt(0.75) * (-1f)), new Vector3f(1, 0, 0), new Vector3f((float) 0.5f * Field.DIMENSIONS.y * (float) Math.sqrt(0.75), 0, (float) Math.sqrt(3) / 2f * Field.DIMENSIONS.y * (float) Math.sqrt(0.75)), new Vector3f((float) 0.5f * Field.DIMENSIONS.y * (float) Math.sqrt(0.75) * (-1f), 0, (float) Math.sqrt(3) / 2f * Field.DIMENSIONS.y * (float) Math.sqrt(0.75)), new Vector3f(-1, 0, 0), new Vector3f((float) 0.5f * Field.DIMENSIONS.y * (float) Math.sqrt(0.75) * (-1f), 0, (float) Math.sqrt(3) / 2f * Field.DIMENSIONS.y * (float) Math.sqrt(0.75) * (-1f)) };
}
