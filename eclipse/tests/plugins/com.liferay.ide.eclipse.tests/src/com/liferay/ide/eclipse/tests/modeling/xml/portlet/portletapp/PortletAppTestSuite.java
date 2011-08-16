
package com.liferay.ide.eclipse.tests.modeling.xml.portlet.portletapp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.liferay.ide.eclipse.tests.modeling.xml.portlet.portletapp.portlets.PortletModelTestSuite;

@RunWith( Suite.class )
@SuiteClasses( { PortletAppTest.class, PortletEventDefinitionTest.class, PortletModelTestSuite.class } )
public class PortletAppTestSuite {

}
