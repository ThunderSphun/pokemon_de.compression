package pokemon.sprite;

@FunctionalInterface
public interface Consumer2D<T> {
	void accept(int x, int y, T t);
}
