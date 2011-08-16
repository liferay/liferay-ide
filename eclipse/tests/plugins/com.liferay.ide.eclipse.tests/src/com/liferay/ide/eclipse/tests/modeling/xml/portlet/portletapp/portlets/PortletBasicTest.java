
package com.liferay.ide.eclipse.tests.modeling.xml.portlet.portletapp.portlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.sapphire.modeling.ByteArrayResourceStore;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.eclipse.portlet.core.model.IPortlet;
import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;
import com.liferay.ide.eclipse.portlet.core.model.ISupports;
import com.liferay.ide.eclipse.tests.AbstractTestCase;

public class PortletBasicTest extends AbstractTestCase {

	private static final String TEST_DATA_FILES_PATH = "test-data/portletapp/portlets/";
	ByteArrayResourceStore byteArrayResourceStore;
	XmlResourceStore xmlResourceStore;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		byteArrayResourceStore = new ByteArrayResourceStore( loadResourceAsStream( "portlet.xml" ) );
		xmlResourceStore = new XmlResourceStore( byteArrayResourceStore );
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		byteArrayResourceStore = null;
		xmlResourceStore = null;
	}

	@Test
	public void test() throws Exception {
		final IPortletApp portletApp = IPortletApp.TYPE.instantiate( new RootXmlResource( xmlResourceStore ) );
		portletApp.setId( "ID_001" );
		IPortlet portlet = portletApp.getPortlets().addNewElement();
		portlet.setPortletName( "portlet-1" );
		portlet.setDisplayName( "Portlet One" );
		portlet.setPortletClass( "com.liferay.eclipse.ide.portlets.PortletOne" );
		ISupports supports = portlet.getSupports();
		supports.setMimeType( "text/html" );
		portletApp.resource().save();
		doXmlAssert( TEST_DATA_FILES_PATH + "portlet-basic-result.xml", byteArrayResourceStore );

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
