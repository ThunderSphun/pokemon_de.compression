package pokemon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		List<File> files = new ArrayList<>();
		files.add(getCompressed("vaporeon"));
		new DecompressTask(files, false).run();
	}

	@SuppressWarnings({"SameParameterValue", "unused"})
	public static File getCompressed(String name) {
		return new File("res/compressed/" + name + ".bin");
	}

	@SuppressWarnings({"SameParameterValue", "unused"})
	public static File getUncompressed(String name) {
		return new File("res/uncompressed/" + name + ".png");
	}
}
