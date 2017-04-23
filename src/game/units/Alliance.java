package game.units;

import org.lwjgl.util.vector.Vector4f;

import lombok.Getter;

public enum Alliance {
	LIGHT(new Vector4f(0.9f, 0.9f, 0.9f, 1)), DARK(new Vector4f(0.2f, 0.2f, 0.2f, 1));

	private @Getter Vector4f color;

	private Alliance(Vector4f color) {
		this.color = color;
	}
}
