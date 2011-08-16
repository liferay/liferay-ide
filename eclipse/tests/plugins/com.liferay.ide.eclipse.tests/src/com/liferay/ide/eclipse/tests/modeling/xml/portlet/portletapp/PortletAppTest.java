/**
 * 
 */

package com.liferay.ide.eclipse.tests.modeling.xml.portlet.portletapp;

import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.junit.Test;

import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;

/**
 * @author kamesh
 */
public class PortletAppTest extends PortletAppBaseTestBase {

	@Test
	public void test() throws Exception {
		final IPortletApp portletApp = IPortletApp.TYPE.instantiate( new RootXmlResource( xmlResourceStore ) );
		portletApp.setId( "ID_001" );
		portletApp.resource().save();
		doXmlAssert( TEST_DATA_FILES_PATH + "portlet-id-result.xml", byteArrayResourceStore );

	}

}
