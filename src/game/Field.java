package game;

import java.util.ArrayList;
import org.lwjgl.util.vector.Vector2f;

import de.geosearchef.matella.entities.Entity;
import game.units.Unit;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Field {

	public static final Vector2f DIMENSIONS = new Vector2f(1, (float) (2 / Math.sqrt(3)));

	public ArrayList<Unit> units = new ArrayList<Unit>();
	private @Getter final int rawPosX, rawPosY;
	private @NonNull @Getter @Setter FieldType type;
	private @Getter Field[] neighbors = new Field[6];
	private @Getter float posX, posY;
	private @Getter @Setter float height = 2f;//too heigh, error can be clearly seen in game
	private @Getter @Setter Entity entity;

	void calculateNeighbors(Field[][] fields) {

		int rightShift = isLefter() ? 0 : 1;

		int x, y;

		// 0
		x = (rawPosX + rightShift) % Game.FIELDS_X;
		y = (rawPosY - 1);
		if (y < 0)
			y += Game.FIELDS_Y;
		neighbors[0] = fields[x][y];

		// 1
		x = (rawPosX + 1) % Game.FIELDS_X;
		y = (rawPosY);
		neighbors[1] = fields[x][y];

		// 2
		x = (rawPosX + rightShift) % Game.FIELDS_X;
		y = (rawPosY + 1) % Game.FIELDS_Y;
		neighbors[2] = fields[x][y];

		// 3
		x = (rawPosX - 1 + rightShift) % Game.FIELDS_X;
		y = (rawPosY + 1) % Game.FIELDS_Y;
		if (x < 0)
			x += Game.FIELDS_X;
		neighbors[3] = fields[x][y];

		// 4
		x = (rawPosX - 1);
		y = (rawPosY);
		if (x < 0)
			x += Game.FIELDS_X;
		neighbors[4] = fields[x][y];

		// 5
		x = (rawPosX - 1 + rightShift) % Game.FIELDS_X;
		y = (rawPosY - 1);
		if (x < 0)
			x += Game.FIELDS_X;
		if (y < 0)
			y += Game.FIELDS_Y;
		neighbors[5] = fields[x][y];

		this.posX = rawPosX + (isLefter() ? 0 : 0.5f);
		this.posY = rawPosY * DIMENSIONS.y * 0.75f;
	}

	public boolean isLefter() {
		return rawPosY % 2 == 0;
	}
}
