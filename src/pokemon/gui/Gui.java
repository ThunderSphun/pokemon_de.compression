package pokemon.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pokemon.DecompressTask;
import pokemon.Main;

import java.io.File;
import java.util.List;

public class Gui extends Application {
	public static void main(String[] args) {
		Application.launch(Gui.class);
	}

	private BorderPane mainPane;

	@Override
	public void init() {
		Renderer renderer = Renderer.Instance();
		renderer.setSize(200, 200, 7, 7);
		renderer.setImageSclFactor(1.5);

		HBox buffer1 = new HBox();
		HBox buffer2 = new HBox();
		buffer1.setSpacing(5);
		buffer2.setSpacing(5);

		for (int i = 0; i < 3; i++) {
			buffer1.getChildren().add(renderer.getBuffer(i * 2).getNode());
			buffer2.getChildren().add(renderer.getBuffer((i * 2) + 1).getNode());
		}

		this.mainPane = new BorderPane();
		this.mainPane.setTop(buffer1);
		this.mainPane.setBottom(buffer2);
		this.mainPane.setLeft(renderer.getImage(0).getNode());
		this.mainPane.setRight(renderer.getImage(1).getNode());

		this.setMargin();
	}

	private void setMargin() {
		BorderPane.setAlignment(this.mainPane.getTop(), Pos.TOP_CENTER);
		BorderPane.setAlignment(this.mainPane.getBottom(), Pos.BOTTOM_CENTER);
		BorderPane.setAlignment(this.mainPane.getLeft(), Pos.CENTER_LEFT);
		BorderPane.setAlignment(this.mainPane.getRight(), Pos.CENTER_RIGHT);

		BorderPane.setMargin(this.mainPane.getTop(), new Insets(0, 0, 0, 0));
		BorderPane.setMargin(this.mainPane.getBottom(), new Insets(5, 0, 0, 0));
		BorderPane.setMargin(this.mainPane.getLeft(), new Insets(5, 0, 0, 0));
		BorderPane.setMargin(this.mainPane.getRight(), new Insets(5, 0, 0, 5));

		this.mainPane.setPadding(new Insets(5, 5, 5, 5));
	}

	@Override
	public void start(Stage stage) {
		Scene scene = new Scene(this.mainPane);
		scene.setFill(Color.hsb(0.79, 0.20, 1));

		scene.setOnDragOver(this::dragHandler);
		scene.setOnDragDropped(this::dropHandler);

		stage.setScene(scene);
		stage.setTitle("Pokémon decompression");
		stage.setAlwaysOnTop(true);
		stage.setOnShowing(e -> this.startDecompressTask(List.of(Main.getCompressed("spearow"))));
		stage.show();
	}

	private void dragHandler(DragEvent dragEvent) {
		Dragboard dragboard = dragEvent.getDragboard();
		if (dragboard.hasFiles()) {
			if (dragboard.getFiles().stream().allMatch(e -> e.getName().endsWith(".bin"))) {
				dragEvent.acceptTransferModes(TransferMode.COPY);
			}
		}
		dragEvent.consume();
	}

	private void dropHandler(DragEvent dragEvent) {
		Dragboard dragboard = dragEvent.getDragboard();
		if (dragboard.hasFiles()) {
			if (dragboard.getFiles().stream().allMatch(e -> e.getName().endsWith(".bin"))) {
				System.out.println(dragboard.getFiles());
				if (dragboard.getFiles().size() > 0) {
					this.startDecompressTask(dragboard.getFiles());
				}
				dragEvent.setDropCompleted(true);
			}
		} else {
			dragEvent.setDropCompleted(false);
		}

		dragEvent.consume();
	}

	private void startDecompressTask(List<File> files) {
		Thread fileHandlerThread = new Thread(new DecompressTask(files, true), "file loading");
		fileHandlerThread.setDaemon(true);
		fileHandlerThread.start();
	}
}
