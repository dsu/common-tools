package pl.spring.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

public class JarTools {

	private static int count = 0;
	private static int broken = 0;

	public static void main(String[] args) throws IOException {
		String repo = System.getProperty("user.home") + "/.m2/repository";
		// String repo = "/winshared/workspaces/sts/spring-ui/mvnlib";
		System.out.println("find broken jars ... in " + repo);
		findBrokenZip(Paths.get(repo));
		System.out.println("Count," + count + " broken " + broken);
	}

	protected void parseJavaClassFile(InputStream classByteStream) throws Exception {
		DataInputStream dataInputStream = new DataInputStream(classByteStream);
		int magicNumber = dataInputStream.readInt();
		if (magicNumber == 0xCAFEBABE) {
			int minor = dataInputStream.readUnsignedShort();
			int major = dataInputStream.readUnsignedShort();
			System.out.println("version " + major + ", " + minor);
		}
	}

	static void findBrokenZip(Path path) throws IOException {

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					findBrokenZip(entry);
				} else {

					if (entry.getFileName().toString().endsWith(".jar")) {
						count++;

						if (java.nio.file.Files.isSymbolicLink(entry)) {
							broken++;
							System.out.println("Deleting symlink " + entry.getFileName());
							Files.delete(entry);
						} else {

							try (JarFile zipFile = new JarFile(entry.toFile())) {
								zipFile.getManifest();
							} catch (ZipException e) {
								broken++;
								System.out.println("Deleting " + entry.getFileName());
								Files.delete(entry);
							}
						}
					}

				}
			}
		}

	}

	public static boolean isSymlink(File file) throws IOException {
		if (file == null)
			throw new NullPointerException("File must not be null");
		File canon;
		if (file.getParent() == null) {
			canon = file;
		} else {
			File canonDir = file.getParentFile().getCanonicalFile();
			canon = new File(canonDir, file.getName());
		}
		return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
	}

}
