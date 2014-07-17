package com.liferay.ide.maven.core.tests;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.junit.Test;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayMavenProjectTests extends LiferayMavenProjectTestCase
{

    @Test
    public void testNewLiferayHookProject() throws Exception
    {
        NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( "hookproject" );
        op.setProjectProvider( "maven" );
        op.setPluginType( PluginType.hook );

        createTestBundleProfile( op );

        final IProject project = base.createProject( op );

        assertNotNull( project );

        String pomContents = CoreUtil.readStreamToString( project.getFile( "pom.xml" ).getContents() );

        assertTrue( pomContents.contains( "<pluginType>hook</pluginType>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-maven-plugin</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>portal-service</artifactId>" ) );

        project.build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );
        waitForJobsToComplete();

        IVirtualComponent projectComponent = ComponentCore.createComponent( project );
        assertEquals( "hookproject-hook", projectComponent.getDeployedName() );
    }

    @Test
    public void testNewLiferayLayouttplProject() throws Exception
    {
        NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( "template" );
        op.setProjectProvider( "maven" );
        op.setPluginType( PluginType.layouttpl );

        createTestBundleProfile( op );

        final IProject project = base.createProject( op );

        assertNotNull( project );

        String pomContents = CoreUtil.readStreamToString( project.getFile( "pom.xml" ).getContents() );

        assertTrue( pomContents.contains( "<pluginType>layouttpl</pluginType>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-maven-plugin</artifactId>" ) );

        project.build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );
        waitForJobsToComplete();

        IVirtualComponent projectComponent = ComponentCore.createComponent( project );
        assertEquals( "template-layouttpl", projectComponent.getDeployedName() );
    }

    @Test
    public void testNewLiferayPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( "mvc" );
        op.setProjectProvider( "maven" );
        op.setPortletFramework( "mvc" );

        createTestBundleProfile( op );

        final IProject project = base.createProject( op );

        assertNotNull( project );

        String pomContents = CoreUtil.readStreamToString( project.getFile( "pom.xml" ).getContents() );

        assertTrue( pomContents.contains( "<liferay.version>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-maven-plugin</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>portal-service</artifactId>" ) );

        project.build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );
        waitForJobsToComplete();

        IVirtualComponent projectComponent = ComponentCore.createComponent( project );
        assertEquals( "mvc-portlet", projectComponent.getDeployedName() );
    }

    @Test
    public void testNewLiferayThemeProject() throws Exception
    {
        NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( "mytheme" );
        op.setProjectProvider( "maven" );
        op.setPluginType( PluginType.theme );

        createTestBundleProfile( op );

        final IProject project = base.createProject( op );

        assertNotNull( project );

        String pomContents = CoreUtil.readStreamToString( project.getFile( "pom.xml" ).getContents() );

        assertTrue( pomContents.contains( "<pluginType>theme</pluginType>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-maven-plugin</artifactId>" ) );

        project.build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );
        waitForJobsToComplete();

        IVirtualComponent projectComponent = ComponentCore.createComponent( project );
        assertEquals( "mytheme-theme", projectComponent.getDeployedName() );
    }
}
