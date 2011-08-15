/**
 * 
 */

package com.liferay.ide.eclipse.tests;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLTestCase;
import org.eclipse.sapphire.modeling.ByteArrayResourceStore;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author kamesh
 */
public abstract class AbstractTestCase extends XMLTestCase {

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * @param resourceName
	 * @return
	 */
	protected abstract InputStream loadResourceAsStream( String resourceName ) throws IOException;

	protected void doXmlAssert( String myControlXMLFile, ByteArrayResourceStore byteArrayResourceStore )
		throws SAXException, IOException {

		InputSource myControlXML = new InputSource( new FileReader( myControlXMLFile ) );
		InputSource myTestXML = new InputSource( new ByteArrayInputStream( byteArrayResourceStore.getContents() ) );

		DetailedDiff myDiff = new DetailedDiff( compareXML( myControlXML, myTestXML ) );
		@SuppressWarnings( "rawtypes" )
		List allDifferences = myDiff.getAllDifferences();
		assertEquals( myDiff.toString(), 0, allDifferences.size() );
	}
}
