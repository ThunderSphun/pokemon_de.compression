package pokemon.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Renderer {
	public static final int TILE_SIZE = 8;
	private static Renderer INSTANCE = null;
	private final List<WritableImageWrapper> buffers;
	private int width;
	private int height;
	private List<WritableImageWrapper> images;
	private double imageScl;

	private Renderer() {
		this.buffers = new ArrayList<>();
		this.images = new ArrayList<>();

		this.width = 1;
		this.height = 1;
		this.imageScl = 1;
	}

	public static synchronized Renderer Instance() {
		if (INSTANCE == null) {
			System.out.println("making renderer");
			INSTANCE = new Renderer();
		}
		return INSTANCE;
	}

	public void addImage() {
		WritableImageWrapper newImage = this.wrapper(
				(int) (this.width * this.imageScl),
				(int) (this.height * this.imageScl),
				this.width / TILE_SIZE,
				this.height / TILE_SIZE);
		this.images.add(newImage);
	}

	public void addBuffer() {
		WritableImageWrapper newBuffer = this.wrapper(
				this.width,
				this.height,
				this.width / TILE_SIZE,
				this.height / TILE_SIZE);
		this.buffers.add(newBuffer);
	}

	public WritableImageWrapper getImage(int index) {
		while (index >= this.images.size()) {
			this.addImage();
		}
		return this.images.get(index);
	}

	public WritableImageWrapper getBuffer(int index) {
		while (index >= this.buffers.size()) {
			this.addBuffer();
		}
		return this.buffers.get(index);
	}

	private WritableImageWrapper wrapper(int width, int height, int widthInTiles, int heightInTiles) {
		return new WritableImageWrapper(width, height, widthInTiles, heightInTiles);
	}

	public void setSize(int tileWidth, int tileHeight) {
		this.images.forEach(e -> e.setSize(e.getWidth(), e.getHeight(), tileWidth, tileHeight));
		this.buffers.forEach(e -> e.setSize(e.getWidth(), e.getHeight(), tileWidth, tileHeight));
	}

	public void setSize(int width, int height, int tileWidth, int tileHeight) {
		this.width = width;
		this.height = height;

		this.images.forEach(e -> e.setSize((int) (this.width * this.imageScl), (int) (this.height * this.imageScl), tileWidth, tileHeight));
		this.buffers.forEach(e -> e.setSize(this.width, this.height, tileWidth, tileHeight));
	}

	public void setImageSclFactor(double sclFactor) {
		this.imageScl = sclFactor;

		this.images = this.images.stream().map(e -> this.wrapper(
				(int) (this.width * this.imageScl),
				(int) (this.height * this.imageScl),
				(int) ((this.width * this.imageScl) / TILE_SIZE),
				(int) ((this.height * this.imageScl) / TILE_SIZE)
		)).collect(Collectors.toList());
	}
}
