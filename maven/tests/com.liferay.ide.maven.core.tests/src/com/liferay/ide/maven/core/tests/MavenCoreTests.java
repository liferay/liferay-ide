
package com.liferay.ide.maven.core.tests;

import com.liferay.ide.core.tests.BaseTests;

import org.eclipse.core.resources.IProject;
import org.junit.Before;

public class MavenCoreTests extends BaseTests
{

    private IProject a;

    @Before
    public void createTestProject() throws Exception
    {
        deleteProject( "a" );
        this.a = createProject( "a" );
    }

    

}
