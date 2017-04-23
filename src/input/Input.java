package input;

import java.nio.file.Watchable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import de.geosearchef.matella.toolbox.EntityIntersection;
import de.geosearchef.matella.toolbox.MousePicker;
import game.Field;
import game.FieldType;
import game.Game;
import lombok.Getter;
import rendering.Renderer;
import update.Updater;
import util.pathfinding.Path;
import util.pathfinding.Pathfinder;

public class Input {

	private static MousePicker mousePicker;
	private static @Getter Field mouseField = null;

	public static boolean wallBuildMode = false;
	public static boolean farmlandBuildMode = false;
	public static Field wallBuildStart = null;
	public static Field wallBuildEnd = null;
	public static Path wallBuildPath = null;

	public static void input(float d) {
		Renderer.player.checkForInput(d);

		// Mouse picker
		mousePicker.updateForMouse();
		EntityIntersection intersection = mousePicker.getEntityIntersection(Renderer.fieldEntities, null, false);
		if (intersection != null) {
			mouseField = Game.fields[Integer.parseInt(intersection.getEntity().getName().split(",")[0])][Integer.parseInt(intersection.getEntity().getName().split(",")[1])];
		}

		while (Keyboard.next()) {

			// WALL
			if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_B) {
				wallBuildMode = !wallBuildMode;
				farmlandBuildMode = false;

				wallBuildPath = null;
				wallBuildStart = wallBuildEnd = null;
				break;
			}

			// FARM
			else if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_V) {
				wallBuildMode = false;
				farmlandBuildMode = !farmlandBuildMode;

				wallBuildPath = null;
				wallBuildStart = wallBuildEnd = null;
				break;
			}
		}

		while (Mouse.next()) {

			// WALL
			if (wallBuildMode && Mouse.getEventButton() == 0) {
				if (Mouse.getEventButtonState()) {
					wallBuildStart = wallBuildEnd = mouseField;
					wallBuildPath = Pathfinder.getPath(wallBuildStart, wallBuildEnd).orElse(null);
				} else {
					wallBuildStart = wallBuildEnd = null;

					if (wallBuildPath != null) {
						wallBuildPath.getFields().stream().filter(f -> f.getType() != FieldType.WALL).forEach(f -> {
							// TODO: cost check
							f.setType(FieldType.WALL);
						});
					}

					wallBuildPath = null;
				}
			}

			// FARM
			else if (farmlandBuildMode && Mouse.getEventButton() == 0) {
				if (!Mouse.getEventButtonState()) {
					if (mouseField.getType() == FieldType.GRASS) {
						mouseField.setType(FieldType.FARMLAND);
					}
				}
			}

		}

		// update wallbuildmode
		if (wallBuildMode && wallBuildStart != null) {
			boolean recalcNeeded = wallBuildEnd != mouseField;
			wallBuildEnd = mouseField;
			wallBuildPath = Pathfinder.getPath(wallBuildStart, wallBuildEnd).orElse(null);
		}
	}

	public static void init() {
		mousePicker = new MousePicker(Renderer.camera, Renderer.renderer.getProjectionMatrix());
	}
}
