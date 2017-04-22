package game;

import java.util.ArrayList;

import game.units.Unit;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Field {
	public ArrayList<Unit> units = new ArrayList<Unit>();
	private @Getter final int posX, posY;
	private @NonNull @Getter @Setter FieldType type;
	private @Getter Field[] neighbors = new Field[6];

	void setneighbors(Field[][] fields) {
		
	}

	public boolean isLefter() {
		return posY % 2 == 0;
	}
}
