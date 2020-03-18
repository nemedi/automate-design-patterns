package demo.decorator;

import java.util.List;
import java.util.function.Consumer;

public interface VisitableList<T> extends Decorator<List<T>> {

	default void foreach(Consumer<T> consumer) {
		for (T item : $this()) {
			consumer.accept(item);
		}
	}
}
