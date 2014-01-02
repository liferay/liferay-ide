
package com.liferay.ide.alloy.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.alloy.core.AlloyCore;
import com.liferay.ide.alloy.core.LautRunner;
import com.liferay.ide.core.tests.BaseTests;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class AlloyCoreTests extends BaseTests
{

    private IProject a;

    @Before
    public void createTestProject() throws Exception
    {
        deleteProject( "a" );
        this.a = createProject( "a" );
    }


    @Test
    public void testLautInstallation()
    {
        final LautRunner lautRunner = AlloyCore.getLautRunner();

        assertNotNull( lautRunner );

        final String execPath = lautRunner.getExecPath();

        assertNotNull( execPath );

        final File exec = new File( execPath );

        assertEquals( true, exec.exists() );

        lautRunner.exec( a, new NullProgressMonitor() );
    }

    @Test
    public void testUpgrade()
    {

    }

}
