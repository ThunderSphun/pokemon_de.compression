package pokemon;

import java.io.EOFException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		List<File> files = new ArrayList<>();
		files.add(getCompressed("spearow"));
		new DecompressTask(files, false).run();

//		File folder = new File("res/compressed");
//		File[] pokemon = folder.listFiles();
//		if (pokemon != null) {
//			for (File file : pokemon) {
//				new DecompressTask(file).run();
//				System.out.println();
//			}
//		}
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
