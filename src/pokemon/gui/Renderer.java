package pokemon.gui;

public class Renderer {
	public static final int TILE_SIZE = 8;
	private static Renderer INSTANCE = null;
	private final WritableImageWrapper[] buffers;
	private WritableImageWrapper image;

	private Renderer() {
		System.out.println("making renderer");
		this.buffers = new WritableImageWrapper[2];

		this.setImage(1, 1, 1, 1);
		this.setBuffer(0, 1, 1, 1, 1);
		this.setBuffer(1, 1, 1, 1, 1);
	}

	public static synchronized Renderer Instance() {
		if (INSTANCE == null) {
			INSTANCE = new Renderer();
		}
		return INSTANCE;
	}

	public WritableImageWrapper getImage() {
		return this.image;
	}

	public WritableImageWrapper getBuffer(int index) {
		return this.buffers[index];
	}

	public void setImage(int width, int height) {
		this.image = this.wrapper(width, height, width / TILE_SIZE, height / TILE_SIZE);
	}

	public void setImage(int width, int height, int widthInTiles, int heightInTiles) {
		this.image = this.wrapper(width, height, widthInTiles, heightInTiles);
	}

	public void setBuffer(int index, int width, int height) {
		this.buffers[index] = this.wrapper(width, height, width / TILE_SIZE, height / TILE_SIZE);
	}

	public void setBuffer(int index, int width, int height, int widthInTiles, int heightInTiles) {
		this.buffers[index] = this.wrapper(width, height, widthInTiles, heightInTiles);
	}

	private WritableImageWrapper wrapper(int width, int height, int widthInTiles, int heightInTiles) {
		return new WritableImageWrapper(width, height, widthInTiles, heightInTiles);
	}
}
