package pokemon.gui;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class WritableImageWrapper {
	private final int width;
	private final int height;
	private final Pane node;
	private int pixelWidth;
	private int pixelHeight;
	private PixelWriter writer;
	private WritableImage image;
	private boolean batch;
	private PixelReader reader;

	public WritableImageWrapper(int width, int height, int widthInPixels, int heightInPixels) {
		this.width = width;
		this.height = height;
		this.batch = false;
		this.node = new Pane();

		this.setSize(widthInPixels, heightInPixels);
	}

	private void updateNode() {
		System.out.println("updating node");
		int sclWidth = (int) ((double) this.width / this.pixelWidth);
		int sclHeight = (int) ((double) this.height / this.pixelHeight);

		WritableImage nodeImage = new WritableImage(this.width, this.height);
		PixelWriter nodeWriter = nodeImage.getPixelWriter();

		for (int pixelY = 0; pixelY < this.pixelHeight; pixelY++) {
			for (int pixelX = 0; pixelX < this.pixelWidth; pixelX++) {
				Color color = this.reader.getColor(pixelX, pixelY);
				for (int sclY = 0; sclY < sclWidth; sclY++) {
					for (int sclX = 0; sclX < sclHeight; sclX++) {
						int x = pixelX * sclWidth + sclX;
						int y = pixelY * sclHeight + sclY;
						nodeWriter.setColor(x, y, color);
					}
				}
			}
		}

		ImageView imageView = new ImageView(nodeImage);

		Platform.runLater(() -> {
			this.node.getChildren().clear();
			this.node.getChildren().add(imageView);

			this.node.setMinSize(this.width, this.height);
			this.node.setMaxSize(this.width, this.height);
		});
	}

	public void setSize(int widthInPixels, int heightInPixels) {
		this.pixelWidth = widthInPixels;
		this.pixelHeight = heightInPixels;

		this.image = new WritableImage(this.pixelWidth, this.pixelHeight);
		this.writer = this.image.getPixelWriter();
		this.reader = this.image.getPixelReader();

		this.updateNode();
	}

	public void set(int x, int y, Color color) {
		this.writer.setColor(x, y, color);

		if (!this.batch) {
			this.updateNode();
		}
	}

	public void announceBatchStart() {
		this.batch = true;
	}

	public void announceBatchEnd() {
		this.batch = false;

		this.updateNode();
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getTileWidth() {
		return this.pixelWidth;
	}

	public int getTileHeight() {
		return this.pixelHeight;
	}

	public Node getNode() {
		return this.node;
	}
}
