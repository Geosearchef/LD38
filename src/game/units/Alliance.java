package game.units;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import game.Game;
import lombok.Getter;

public enum Alliance {
	LIGHT(new Vector2f(Game.FIELDS_X / 3, Game.FIELDS_Y / 2), new Vector4f(0.9f, 0.9f, 0.9f, 1)), DARK(new Vector2f(2* Game.FIELDS_X / 3, Game.FIELDS_Y / 2), new Vector4f(0.2f, 0.2f, 0.2f, 1));
	
	private @Getter Vector2f spawnPos;
	private @Getter Vector4f color;

	private Alliance(Vector2f spawnPos, Vector4f color) {
		this.color = color;
		this.spawnPos = spawnPos;
	}
}
