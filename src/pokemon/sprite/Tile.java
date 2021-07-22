package pokemon.sprite;

import java.util.Arrays;
import java.util.StringJoiner;

public class Tile {
	public static final int SIZE = 8;
	private final byte[][] pixels;

	public Tile() {
		this.pixels = new byte[SIZE][SIZE];
	}

	public byte get(int x, int y) {
		return this.pixels[x][y];
	}

	public void set(int x, int y, byte color) {
		this.pixels[x][y] = color;
	}

	public void forEach(Consumer2D<Byte> action) {
		int width = this.pixels.length;
		int height = this.pixels[0].length;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				action.accept(i, j, this.pixels[i][j]);
			}
		}
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", Tile.class.getSimpleName() + "[", "]");
		for (byte[] bytes : this.pixels) {
			joiner.add(Arrays.toString(bytes));
		}
		return joiner.toString();
	}
}
