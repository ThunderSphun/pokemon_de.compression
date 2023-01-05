package pokemon;

import java.io.File;
import java.io.FileFilter;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
	public static void main(String[] args) {
		new DecompressTask(getCompressed("spearow")).run();

		System.out.println("\nremoving printStream\n");
		PrintStream stream = System.out;
		System.setOut(new PrintStream(OutputStream.nullOutputStream()));

		File folder = new File("res/compressed");
		File[] pokemon = folder.listFiles(file -> file.getName().endsWith(".bin"));
		Map<String, Double> timeMap = new HashMap<>();
		if (pokemon != null) {
			for (File file : pokemon) {
				long time = 0;
				int amount = 1000;
				System.err.println(file.getName());
				for (int i = 0; i < amount; i++) {
					long nano = System.nanoTime();
//					new DecompressTask(file).run();
					new optimized.DecompressTask(file).run();
					long decompressTime = System.nanoTime() - nano;
					time += decompressTime;
				}
				timeMap.put(file.getName(), (time / amount) / 1.0E6);
			}
		}

		System.setOut(stream);
		System.out.println("average ms per sprite");
		System.out.println(timeMap.entrySet().stream()
				.sorted((a, b) -> (int) Math.signum(a.getValue() - b.getValue())).collect(Collectors.toList()));
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
