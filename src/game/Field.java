package game;

import java.util.ArrayList;
import org.lwjgl.util.vector.Vector2f;

import game.units.Unit;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Field {

	private static final Vector2f DIMENSIONS = new Vector2f(1, (float) (2 / Math.sqrt(3)));

	public ArrayList<Unit> units = new ArrayList<Unit>();
	private @Getter final int rawPosX, rawPosY;
	private @NonNull @Getter @Setter FieldType type;
	private @Getter Field[] neighbors = new Field[6];

	void setneighbors(Field[][] fields) {

		int rightShift = isLefter() ? 0 : 1;

		neighbors[0] = fields[rawPosX + rightShift][rawPosY - 1];
		neighbors[1] = fields[rawPosX + 1][rawPosY];
		neighbors[2] = fields[rawPosX + rightShift][rawPosY + 1];
		neighbors[3] = fields[rawPosX - 1 + rightShift][rawPosY + 1];
		neighbors[4] = fields[rawPosX - 1][rawPosY];
		neighbors[5] = fields[rawPosX - 1 + rightShift][rawPosY - 1];

	}

	public float getX() {
		return rawPosX + (isLefter() ? 0 : 0.5f);
	}

	public float getY() {
		return rawPosY * DIMENSIONS.y * 0.75f;
	}

	public boolean isLefter() {
		return rawPosY % 2 == 0;
	}
}
