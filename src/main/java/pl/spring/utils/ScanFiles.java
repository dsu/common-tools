package pl.spring.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class ScanFiles {

	protected Path path;
	protected String extenstion;
	protected Consumer<Path> consumer;
	private int count = 0;

	public ScanFiles(Path path, String extenstion, Consumer<Path> consumer) {
		super();
		this.path = path;
		this.extenstion = extenstion;
		this.consumer = consumer;
	}

	protected void scan(Path dir) throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path entry : stream) {

				System.out.println("Scan .. " + entry.getFileName());

				if (Files.isDirectory(entry)) {
					scan(entry);
				} else {
					if (entry.getFileName().toString().endsWith(extenstion)) {
						count++;
						consumer.accept(entry);
					}
				}
			}
		}
	}

	public void scan() throws IOException {
		count = 0;
		scan(path);
		System.out.println("Scann completed with " + count + " files");
	}
}
