import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;

import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class TestPdf {

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {
		Tidy tidy = new Tidy();
		tidy.setXHTML(true);
		StringWriter str = new StringWriter();
		tidy.parse(new URL("http://localhost:8888/print/blog/1").openStream(), str);
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
