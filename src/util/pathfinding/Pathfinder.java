package util.pathfinding;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import game.Field;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

public class Pathfinder {
	
	/**
	 * Calculates a path from source to destination
	 * @param src
	 * @param dest
	 * @return a path from src to dest, if available
	 */
	public static CompletableFuture<Optional<Path>> getPathAsync(@NonNull Field src, @NonNull Field dest) {
		return CompletableFuture.supplyAsync(() -> getPath(src, dest));
	}
	
	private static Comparator<Map.Entry<Field, FieldPath>> mapEntryComparator = (e1, e2) -> e1.getValue().getFields().size() - e2.getValue().getFields().size();
	
	/**
	 * Use the async method when possible
	 * @param src
	 * @param dest
	 * @return a path from src to dest, if available
	 */
	public static Optional<Path> getPath(@NonNull Field src, @NonNull Field dest) {
		Map<Field, FieldPath> closedList = new HashMap<Field, FieldPath>();
		Map<Field, FieldPath> openList = new HashMap<Field, FieldPath>();
		
		openList.put(src, new FieldPath().add(src));
		
		while(! openList.isEmpty()) {
			Map.Entry<Field, FieldPath> currentEntry = Collections.min(openList.entrySet(), mapEntryComparator);
			
			Stream.of(currentEntry.getKey().getNeighbors())
			.filter(n -> n.isPassable())
			.filter(n -> !openList.containsKey(n) && !closedList.containsKey(n))
			.forEach(n -> {
				openList.put(n, currentEntry.getValue().add(n));
			});
			
			if(openList.containsKey(dest)) {
				return Optional.of(new Path(openList.get(dest).getFields()));
			}
			
			openList.remove(currentEntry.getKey());
			closedList.put(currentEntry.getKey(), currentEntry.getValue());
		}
		
		return Optional.empty();
	}
	
	
	@NoArgsConstructor
	private static class FieldPath {
		
		private final @Getter List<Field> fields = new LinkedList<Field>();
		
		private FieldPath(FieldPath src, Field newElement) {
			this.fields.addAll(src.getFields());
			this.fields.add(newElement);
		}
		
		FieldPath add(Field field) {
			return new FieldPath(this, field);
		}
	}
}
