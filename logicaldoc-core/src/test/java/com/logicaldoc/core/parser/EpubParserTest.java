package com.logicaldoc.core.parser;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.security.Tenant;

public class EpubParserTest extends AbstractCoreTCase {
	
	@Test
	public void testParse() throws UnsupportedEncodingException, ParseException {
		String inputFile = "src/test/resources/aliceDynamic.epub";
		File file = new File(inputFile);
		String filename = file.getPath();

		Parser parser = ParserFactory.getParser(filename);
		EpubParser epubp = (EpubParser) parser;

		String content = epubp.parse(file, filename, null, Locale.ENGLISH, Tenant.DEFAULT_NAME);
		Assert.assertTrue(content.contains("wonder"));
		Assert.assertTrue(content.contains("Alice"));
	}
}
