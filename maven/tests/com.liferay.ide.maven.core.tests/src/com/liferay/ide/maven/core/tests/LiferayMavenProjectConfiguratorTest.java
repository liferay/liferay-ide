/**
 *
 */

package com.liferay.ide.maven.core.tests;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.m2e.tests.common.RequireMavenExecutionContext;
import org.junit.Test;

/**
 * @author Kamesh Sampath
 */
@SuppressWarnings( "restriction" )
@RequireMavenExecutionContext
public class LiferayMavenProjectConfiguratorTest extends AbstractMavenProjectTestCase
{

    @Test
    public void testLiferayFactedConfigured() throws Exception
    {
        IProject project = importProject( "projects/configurators/webapp-1/pom.xml" );

        assertNotNull( project );

        IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create( project, monitor );

        assertNotNull( facade );

        assertFalse( CoreUtil.isLiferayProject( project ) );
    }

    @Test
    public void testLiferayFacetNoLiferayPluginConfigured() throws Exception
    {
        IProject project = importProject( "projects/configurators/default-webapp-no-liferay-plugin/pom.xml" );

        assertNotNull( project );

        IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create( project, monitor );

        assertNotNull( facade );

        assertTrue( CoreUtil.isLiferayProject( project ) );
    }

    @Test
    public void testLiferayFacetNoLiferayPluginWarPluginConfigured() throws Exception
    {
        IProject project = importProject( "projects/configurators/webapp-alternate-webapp-folder/pom.xml" );

        assertNotNull( project );

        IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create( project, monitor );

        assertNotNull( facade );

        assertTrue( CoreUtil.isLiferayProject( project ) );
    }

    @Test
    public void testWarPluginNoWarSourceDirConfigured() throws Exception
    {
        IProject project = importProject( "projects/configurators/bad-war-config/pom.xml" );

        assertNotNull( project );

        IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create( project, monitor );

        assertNotNull( facade );

        assertTrue( CoreUtil.isLiferayProject( project ) );

    }
}
