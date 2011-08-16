/**
 * 
 */

package com.liferay.ide.eclipse.tests.modeling.xml.portlet.portletapp;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.modeling.DerivedValueService;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.junit.Test;

import com.liferay.ide.eclipse.portlet.core.model.IEventDefinition;
import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;

/**
 * @author kamesh
 */
public class PortletEventDefinitionTest extends PortletAppBaseTestBase {

	@Test
	public void test() throws Exception {
		final IPortletApp portletApp = IPortletApp.TYPE.instantiate( new RootXmlResource( xmlResourceStore ) );
		portletApp.setId( "ID_001" );
		IEventDefinition eventDef = portletApp.getEventDefinitions().addNewElement();
		eventDef.setNamespaceURI( "http://workspace7.org.in" );
		eventDef.setLocalPart( "demo-event" );
		portletApp.resource().save();
		doXmlAssert( TEST_DATA_FILES_PATH + "portlet-event-result.xml", byteArrayResourceStore );

	}

	@Test
	public void testQNameDerivedValue() throws Exception {
		final IPortletApp portletApp = IPortletApp.TYPE.instantiate( new RootXmlResource( xmlResourceStore ) );
		portletApp.setId( "ID_001" );
		IEventDefinition eventDef = portletApp.getEventDefinitions().addNewElement();
		eventDef.setNamespaceURI( "http://workspace7.org.in" );
		eventDef.setLocalPart( "demo-event" );
		portletApp.resource().save();
		doXmlAssert( TEST_DATA_FILES_PATH + "portlet-event-result.xml", byteArrayResourceStore );
		DerivedValueService derivedValueService =
			eventDef.service( IEventDefinition.PROP_QnAME, DerivedValueService.class );
		String eventQNameDerivedValue = derivedValueService.getDerivedValue();
		QName eventQName = new QName( "http://workspace7.org.in", "demo-event" );
		assertEquals( eventQName.toString(), eventQNameDerivedValue );

	}
}
