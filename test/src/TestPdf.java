/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;

public class TestPdf {

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {
		Tidy tidy = new Tidy();
		tidy.setXHTML(true);
		StringWriter str = new StringWriter();
		tidy.parse(new URL("http://localhost:8888/print/blog/1").openStream(),
				str);
		// test git comment
		// tidy.parse(new URL("http://localhost:8888/print").openStream(), new
		// FileOutputStream("d:/firstdoc.html"));
		// String inputFile = "samples/firstdoc.xhtml";
		// String url = new File(inputFile).toURI().toURL().toString();
		// test for git merge
		String outputFile = "d:/firstdoc.pdf";
		File fout = new File(outputFile);
		fout.createNewFile();
		OutputStream os = new FileOutputStream(outputFile);

		ITextRenderer renderer = new ITextRenderer();
		// renderer.setDocumentFromString(str.toString());
		// renderer.setDocument(new File("d:/print.htm"));
		renderer.setDocument("http://localhost:8888/print/blog/1");
		// renderer.setDocument("http://www.devproof.org/article/portal_howto_create_modules");
		renderer.layout();
		renderer.createPDF(os, true);

		os.close();

	}
}
