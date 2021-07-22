package pokemon.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pokemon.DecompressTask;
import pokemon.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Gui extends Application {
	public static void main(String[] args) throws InterruptedException {
		new Thread(() -> Application.launch(Gui.class)).start();
		Thread.sleep(1000);
		List<File> files = new ArrayList<>();
		files.add(Main.getCompressed("vaporeon"));
		Thread fileHandlerThread = new Thread(new DecompressTask(files, true), "file loading");
		fileHandlerThread.setDaemon(true);
		fileHandlerThread.start();
	}

	private BorderPane mainPane;

	@Override
	public void init() {
		this.mainPane = new BorderPane();

		HBox hbox = new HBox();
		VBox vbox = new VBox();

		Renderer renderer = Renderer.Instance();
		renderer.setImage(400, 400);
		renderer.setBuffer(0, 200, 200);
		renderer.setBuffer(1, 200, 200);

		vbox.getChildren().addAll(renderer.getBuffer(0).getNode(), renderer.getBuffer(1).getNode());
		hbox.getChildren().addAll(renderer.getImage().getNode(), vbox);

		this.mainPane.setCenter(hbox);
	}

	@Override
	public void start(Stage stage) {
		Scene scene = new Scene(this.mainPane);

		scene.setOnDragOver(this::dragHandler);
		scene.setOnDragDropped(this::dropHandler);

		stage.setScene(scene);
		stage.setTitle("Pokémon (de)compression");
		stage.setAlwaysOnTop(true);
		stage.show();
	}

	private void dragHandler(DragEvent dragEvent) {
		if (dragEvent.getDragboard().hasFiles()) {
			dragEvent.acceptTransferModes(TransferMode.COPY);
		}
		dragEvent.consume();
	}

	private void dropHandler(DragEvent dragEvent) {
		Dragboard dragboard = dragEvent.getDragboard();
		if (dragboard.hasFiles()) {
			System.out.println(dragboard.getFiles());
			if (dragboard.getFiles().size() > 0) {
				Thread fileHandlerThread = new Thread(new DecompressTask(dragboard.getFiles(), true), "file loading");
				fileHandlerThread.setDaemon(true);
				fileHandlerThread.start();
			}
			dragEvent.setDropCompleted(true);
		} else {
			dragEvent.setDropCompleted(false);
		}

		dragEvent.consume();
	}
}
