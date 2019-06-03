package pl.spring.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FindDuplicates {

	private static final long MIN_SIZE = 1000000 * 10;

	public static void main(String... strings) throws IOException, NoSuchAlgorithmException {
		FindDuplicates findDuplicates = new FindDuplicates();
		findDuplicates.findDuplicateFiles("/run/media/dsu/NFTS/");
		findDuplicates.print();
		// move(new File("/home/dsu/Pobrane/usluga jedna platnosc ze skryptem
		// inline.html"), "/data/test");
	}

	private final MessageDigest md;

	public FindDuplicates() throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance("SHA-512");
	}

	// hash, and files map
	private Map<String, List<String>> fileHash = new HashMap<String, List<String>>();
	private Map<String, FileTime> fileAccess = new HashMap<String, FileTime>();

	public void print() {
		int count = 0;
		Instant maxOld = Instant.now().minus(24, ChronoUnit.DAYS);

		for (String hash : fileHash.keySet()) {

			List<String> values = fileHash.get(hash);

			if (values.size() > 1) {

				System.out.println("\n=======" + count++ + "======");
				sortByLasAccesedFirst(values);
				String first = values.get(0);
				// dont touch first duplicate
				System.out.println("Last accessed : " + first + ", " + getAccesTime(Paths.get(first)).toInstant());
				for (String file : values.subList(1, values.size())) {
					System.out.println(file + ", " + getAccesTime(Paths.get(file)).toInstant());
					FileTime accesTime = getAccesTime(Paths.get(file));
					if (accesTime.toInstant().isBefore(maxOld)) {
						System.out.println("MOVE!");
					}

				}
			}

		}

	}

	private static void move(File f, String rootDestination) throws IOException {

		// String root = f.getParentFile().getAbsolutePath();
		if (f.exists() && f.isFile() && f.canRead()) {
			File destFile = new File(rootDestination, f.getAbsolutePath());
			destFile.getParentFile().mkdirs();
			System.out.println("Moving file: " + f + " to " + destFile);
			Path move = Files.move(f.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING,
					java.nio.file.StandardCopyOption.COPY_ATTRIBUTES);
			System.out.println(move);

		} else {
			System.out.println("Could not find file: " + f);
		}

	}

	private void sortByLasAccesedFirst(List<String> values) {
		Collections.sort(values, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				// last accesed first
				Instant instant1 = getAccesTime(Paths.get(o1)).toInstant();
				Instant instant2 = getAccesTime(Paths.get(o2)).toInstant();
				return instant2.compareTo(instant1);
			}
		});
	}

	public void findDuplicateFiles(String dir) throws IOException {
		new ScanFiles(Paths.get(dir), "", p -> {

			FileTime accesTime = getAccesTime(p);
			File file = p.toFile();
			long length = file.length();
			// bytes
			if (length < MIN_SIZE) {
				return;
			}

			FileInputStream fileInput = null;
			try {
				fileInput = new FileInputStream(file);

				System.out.println("length: " + length);
				if (length >= Integer.MAX_VALUE) {
					// file to big, use file length as a key
					appenHash(file, "" + length);
				} else {

					byte fileData[] = new byte[(int) length];
					fileInput.read(fileData);
					fileInput.close();

					String uniqueFileHash = new BigInteger(1, md.digest(fileData)).toString(16);
					appenHash(file, uniqueFileHash);
				}

				// restore last acces time
				Files.setAttribute(p, "lastAccessTime", accesTime);
				// files

			} catch (IOException e) {
				throw new RuntimeException("cannot read file " + file.getAbsolutePath(), e);
			}
		}).scan();
	}

	private void appenHash(File file, String uniqueFileHash) {
		List<String> list = fileHash.get(uniqueFileHash);
		if (list == null) {
			list = new LinkedList<String>();
			fileHash.put(uniqueFileHash, list);
		}
		list.add(file.getAbsolutePath());
	}

	private FileTime getAccesTime(Path p) {
		try {
			BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
			FileTime time = attrs.lastAccessTime();
			return time;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
