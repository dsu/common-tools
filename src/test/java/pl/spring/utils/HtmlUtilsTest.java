package pl.spring.utils;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

public class HtmlUtilsTest {

	@Test
	public void testCloseTags() throws IOException {
		new ScanFiles(Paths.get("/winshared/workspaces/sts/spring-tools/src/main/resources"), ".html", p -> {
			try {
				HtmlUtils.clean(p);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).scan();
	}

	public void test() {

		String html = "<div>testy <style>.style{}</style> <image> <div> <span> test 2<script> alert('a'); </script>";
		System.out.println(HtmlUtils.clean(html));
		System.out.println(HtmlUtils.getSafeHtml(html));
	}

}
