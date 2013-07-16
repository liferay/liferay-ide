
package com.liferay.ide.portal.core.tests;

import com.liferay.ide.core.tests.BaseTests;

import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.Before;

/**
 * @author Gregory Amerson
 */
public class PortalCoreTests extends BaseTests
{

    private IProject a;

    @Before
    public void createTestProject() throws Exception
    {
        this.a = createProject( "a" );
    }

    protected IProject getCurrentProject()
    {
        return this.a;
    }

    @After
    public void cleanup() throws Exception
    {
        deleteProject( "a" );
    }

}
