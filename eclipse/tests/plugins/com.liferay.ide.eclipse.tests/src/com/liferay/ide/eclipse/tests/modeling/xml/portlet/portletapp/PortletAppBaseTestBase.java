/**
 * 
 */

package com.liferay.ide.eclipse.tests.modeling.xml.portlet.portletapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.sapphire.modeling.ByteArrayResourceStore;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.junit.After;
import org.junit.Before;

import com.liferay.ide.eclipse.tests.AbstractTestCase;

/**
 * @author kamesh
 */
public abstract class PortletAppBaseTestBase extends AbstractTestCase {

	protected static final String TEST_DATA_FILES_PATH = "test-data/portletapp/";
	protected ByteArrayResourceStore byteArrayResourceStore;
	protected XmlResourceStore xmlResourceStore;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	protected void setUp() throws Exception {
		byteArrayResourceStore = new ByteArrayResourceStore( loadResourceAsStream( "portlet.xml" ) );
		xmlResourceStore = new XmlResourceStore( byteArrayResourceStore );
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	protected void tearDown() throws Exception {
		byteArrayResourceStore = null;
		xmlResourceStore = null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.liferay.ide.eclipse.tests.AbstractTestCase#loadResourceAsStream(java.lang.String)
	 */
	@Override
	protected InputStream loadResourceAsStream( String resourceName ) throws IOException {
		InputStream in = new FileInputStream( new File( TEST_DATA_FILES_PATH + "portlet.xml" ) );
		return in;
	}

}
