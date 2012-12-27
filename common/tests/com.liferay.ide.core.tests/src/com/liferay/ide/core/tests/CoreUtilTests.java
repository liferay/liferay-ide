
package com.liferay.ide.core.tests;

import org.eclipse.core.resources.IProject;
import org.junit.Before;
import org.junit.Test;

public class CoreUtilTests extends BaseTests
{

    private IProject a;

    @Before
    public void createTestProject() throws Exception
    {
        deleteProject( "a" );
        this.a = createProject( "a" );
    }

    @Test
    public void testAddNaturesToProject() throws Exception
    {

    }

}
