package pl.spring.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

public class HtmlUtils {

	/**
	 * Closes tags, formats html, removes script, style tags
	 * 
	 * @param textToEscape
	 * @return
	 */
	public static String getSafeHtml(String textToEscape) {

		if (textToEscape == null || textToEscape.length() < 1) {
			return "";
		}

		Whitelist whitelist = Whitelist.relaxed();
		OutputSettings outputSettings = new OutputSettings().charset(StandardCharsets.UTF_8).prettyPrint(true);
		String safe = Jsoup.clean(textToEscape, "", whitelist, outputSettings);
		return safe;

	}

	/**
	 * Closes tags, formats html.
	 * 
	 * @param textToEscape
	 * @return
	 */
	public static String clean(String textToEscape) {

		if (textToEscape == null || textToEscape.length() < 1) {
			return "";
		}

		// Whitelist whitelist = Whitelist.none();
		// OutputSettings outputSettings = new
		// OutputSettings().charset(StandardCharsets.UTF_8).prettyPrint(false);
		// String safe = Jsoup.clean(textToEscape, "", whitelist, outputSettings);
		// return safe;
		Document doc = Jsoup.parseBodyFragment(textToEscape);
		return doc.body().html();

	}

	public static void clean(Path file) throws IOException {
		String string = new String(Files.readAllBytes(file));

		System.out.println("Clean ... " + file);

		String clean = clean(string);

		Files.write(file, clean.getBytes(Charset.forName("UTF-8")));

		if (!clean.equals(string)) {

			Path bk = Paths.get(file.toAbsolutePath() + "_old");
			Files.createFile(bk);
			Files.write(bk, string.getBytes(Charset.forName("UTF-8")));
		}
	}
}
