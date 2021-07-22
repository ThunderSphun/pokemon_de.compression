package pokemon.sprite;

import java.util.Arrays;
import java.util.StringJoiner;

public class Sprite {
	private final Tile[] tiles;
	private final int width;
	private final int height;

	public Sprite(int width, int height) {
		this.tiles = new Tile[7 * 7];
		this.width = width;
		this.height = height;

		Arrays.setAll(this.tiles, e -> new Tile());
	}

	public Sprite() {
		this(7, 7);
	}

	protected int index(int x, int y) {
		return x + y * this.width;
	}

	public Tile get(int x, int y) {
		return this.tiles[this.index(x, y)];
	}

	public void set(int x, int y, Tile tile) {
		this.tiles[this.index(x, y)] = tile;
	}

	public byte getAbsolute(int x, int y) {
		Tile tile = this.get(x / Tile.SIZE, y / Tile.SIZE);
		return tile.get(x % Tile.SIZE, y % Tile.SIZE);
	}

	public void setAbsolute(int x, int y, byte color) {
		Tile tile = this.get(x / Tile.SIZE, y / Tile.SIZE);
		tile.set(x % Tile.SIZE, y % Tile.SIZE, color);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getAbsoluteWidth() {
		return this.getWidth() * Tile.SIZE;
	}

	public int getAbsoluteHeight() {
		return this.getHeight() * Tile.SIZE;
	}

	public void forEach(Consumer2D<Tile> action) {
		int width = this.getWidth();
		int height = this.getHeight();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				action.accept(i, j, this.get(i, j));
			}
		}
	}

	public void forEachAbsolute(Consumer2D<Byte> action) {
//		this.forEach((i, j, t) -> t.forEach(action));

		int width = this.getAbsoluteWidth();
		int height = this.getAbsoluteHeight();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				action.accept(i, j, this.getAbsolute(i, j));
			}
		}
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", Sprite.class.getSimpleName() + "[", "]")
				.add("tiles=" + Arrays.toString(tiles)).toString();
	}
}