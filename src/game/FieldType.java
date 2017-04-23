package game;

import org.lwjgl.util.vector.Vector4f;

import lombok.Getter;

public enum FieldType {
	SAND(new Vector4f(0.898f, 0.737f, 0.521f, 1f)),
	GROUND(new Vector4f(0.305f, 0.203f, 0.180f, 1f)), 
	GRASS(new Vector4f(0.219f, 0.556f, 0.235f, 1f)), 
	WATER(new Vector4f(0, 0, 1, 0)), 
	WALL(new Vector4f(0.75f, 0.75f, 0.75f, 1f));

	private @Getter Vector4f color;

	FieldType(Vector4f color) {
		this.color = color;
	}
}
