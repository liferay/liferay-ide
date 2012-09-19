package com.liferay.ide.portal.core.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class PortalCoreTestSuite extends TestCase
{

    private PortalCoreTestSuite( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "PortalCore" );
        
        return suite;
    }

}
