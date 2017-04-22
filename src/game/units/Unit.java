package game.units;

import org.lwjgl.util.vector.Vector3f;

import de.geosearchef.matella.entities.Entity;
import de.geosearchef.matella.models.Model;

public class Unit extends Entity {

	private static Model model;
	int alliance;
	int id;

	public Unit(int id, Vector3f position, int alliance) {
		super(model, position, new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), false, id);
		this.id = id;
		this.alliance = alliance;
	}

}
