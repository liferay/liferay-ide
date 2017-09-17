package test;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class Test {

	public static void main(String[] args) throws IOException {
		System.out.println(new Lib().getSomething());
		IOUtils.contentEquals((InputStream)null, (InputStream)null);
	}

}
