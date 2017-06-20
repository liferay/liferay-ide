/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.swtbot.module.fragment.tests;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;

import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liferay.ide.swtbot.module.fragment.ui.tests.page.AddFilesToOverridePO;
import com.liferay.ide.swtbot.module.fragment.ui.tests.page.CreateModuleFragmentProjectWizardPO;
import com.liferay.ide.swtbot.module.fragment.ui.tests.page.HostOSGiBundlePO;
import com.liferay.ide.swtbot.module.fragment.ui.tests.page.SetModuleFragmentProjectOSGiBundlePO;
import com.liferay.ide.swtbot.server.ui.tests.page.DeleteRuntimeConfirmPO;
import com.liferay.ide.swtbot.server.ui.tests.page.DeleteRuntimePO;
import com.liferay.ide.swtbot.server.ui.tests.page.NewServerPO;
import com.liferay.ide.swtbot.server.ui.tests.page.NewServerRuntimeEnvPO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.swtbot.page.CTabItemPO;
import com.liferay.ide.swtbot.ui.tests.swtbot.page.TextEditorPO;
import com.liferay.ide.swtbot.ui.tests.swtbot.page.TreePO;

/**
 * @author Vicky Wang
 * @author Sunny Shi
 */
public class ModuleFragmentProjectWizardTests extends SWTBotBase implements ModuleFragmentProjectWizard
{

    String projectName = "module-fragment-project";

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    NewServerPO newServerPage = new NewServerPO( bot );

    NewServerRuntimeEnvPO setRuntimePage = new NewServerRuntimeEnvPO( bot );

    CreateModuleFragmentProjectWizardPO newModuleFragmentPage =
        new CreateModuleFragmentProjectWizardPO( bot, INDEX_VALIDATION_PAGE_MESSAGE3 );

    SetModuleFragmentProjectOSGiBundlePO moduleFragmentOSGiBundlePage =
        new SetModuleFragmentProjectOSGiBundlePO( bot, INDEX_VALIDATION_MESSAGE2 );

    HostOSGiBundlePO selectOSGiBundlePage = new HostOSGiBundlePO( bot );
    AddFilesToOverridePO addJSPFilesPage = new AddFilesToOverridePO( bot );

    TreePO projectTree = eclipse.showPackageExporerView().getProjectTree();

    TreePO serverTree = new TreePO( bot, 1 );

    @Before
    public void isRunTest()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        eclipse.getLiferayWorkspacePerspective().activate();
    }

    public void deleteRuntimeFromPreferences()
    {
        eclipse.getPreferencesMenu().click();

        TreePO preferencesTree = new TreePO( bot );

        preferencesTree.expandNode( "Server", "Runtime Environments" ).select();
        sleep();

        DeleteRuntimePO deleteRuntime = new DeleteRuntimePO( bot );

        if( deleteRuntime.getServerRuntimeEnvironments().containsItem( NODE_LIFERAY_7X ) )
        {
            deleteRuntime.getServerRuntimeEnvironments().click( NODE_LIFERAY_7X );
            deleteRuntime.getRemove().click();

            DeleteRuntimeConfirmPO confirmDelete = new DeleteRuntimeConfirmPO( bot );

            confirmDelete.confirm();
        }

        deleteRuntime.confirm();

        sleep();
    }

    public void addLiferayServerAndOpenWizard()
    {
        eclipse.getCreateLiferayProjectToolbar().getNewLiferayServer().click();

        newServerPage.getServerTypeTree().selectTreeItem( NODE_LIFERAY_INC, NODE_LIFERAY_7X );

        sleep( 2000 );

        if( !newServerPage.finishButton().isEnabled() )
        {
            newServerPage.next();

            sleep();

            setRuntimePage.getServerLocation().setText( getLiferayServerDir().toOSString() );

            assertEquals( "Tomcat", setRuntimePage.getPortalBundleType().getText() );
            setRuntimePage.finish();
        }
        else
        {
            newServerPage.finish();
        }

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayModuleFragmentProject().click();
    }

    public void checkBuildType()
    {
        String[] expectedFragmentBuildTypeItems = { TEXT_BUILD_TYPE_GRADLE, TEXT_BUILD_TYPE_MAVEN };
        String[] fragmentBuildTypeItems = newModuleFragmentPage.getBuildType().getAvailableComboValues();
        assertEquals( expectedFragmentBuildTypeItems.length, fragmentBuildTypeItems.length );

        for( int j = 0; j < fragmentBuildTypeItems.length; j++ )
        {
            assertTrue( fragmentBuildTypeItems[j].equals( expectedFragmentBuildTypeItems[j] ) );
        }

    }

    @After
    public void deleteProject()
    {
        eclipse.closeShell( LABEL_NEW_LIFERAY_MODULE_PROJECT_FRAGMENT );

        if( addedProjects() )
        {
            eclipse.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() },
                true );
        }
    }

    @BeforeClass
    public static void prepareServer() throws IOException
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        unzipServer();
    }

    @Test
    public void moduleFragmentProjectWizardWithoutServer()

    {
        if( serverTree.hasItems() )
        {
            deleteRuntimeFromPreferences();
        }

        else
        {
            eclipse.getCreateLiferayProjectToolbar().getNewLiferayModuleFragmentProject().click();

            newModuleFragmentPage.setProjectName( ".." );
            sleep();
            assertEquals( " '..'" + TEXT_INVALID_NAME_ON_PLATFORM, newModuleFragmentPage.getValidationMessage() );

            newModuleFragmentPage.setProjectName( "##" );
            sleep();
            assertEquals( " " + TEXT_INVALID_GRADLE_PROJECT, newModuleFragmentPage.getValidationMessage() );

            newModuleFragmentPage.setProjectName( "*" );
            sleep();
            assertEquals( " *" + TEXT_INVALID_CHARACTER_IN_RESOURCE_NAME + "'*'.",
                newModuleFragmentPage.getValidationMessage() );

            newModuleFragmentPage.setProjectName( TEXT_BLANK );
            sleep();
            assertEquals( TEXT_PROJECT_NAME_MUST_BE_SPECIFIED, newModuleFragmentPage.getValidationMessage() );

            newModuleFragmentPage.setProjectName( projectName );
            sleep();
            assertEquals( TEXT_LIFERAY_RUNTIME_MUST_BE_CONFIGURED, newModuleFragmentPage.getValidationMessage() );

            assertTrue( newModuleFragmentPage.isLiferayRuntimeTextEnabled() );
            newModuleFragmentPage.cancel();
        }
    }

    @Test
    public void moduleFragmentProjectWizard()
    {
        addLiferayServerAndOpenWizard();

        checkBuildType();
        newModuleFragmentPage.setProjectName( projectName, TEXT_BUILD_TYPE_GRADLE );
        sleep();
        assertTrue( newModuleFragmentPage.isLiferayRuntimeTextEnabled() );
        newModuleFragmentPage.next();

        // select OSGi Bundle and Overridden files
        assertEquals( TEXT_OSGI_BUNDLE_BLANK, moduleFragmentOSGiBundlePage.getValidationMessage() );

        moduleFragmentOSGiBundlePage.getSelectOSGiBundleButton().click();

        selectOSGiBundlePage.setOSGiBundle( "com.liferay.announcements." );
        selectOSGiBundlePage.confirm();

        moduleFragmentOSGiBundlePage.getAddOverriddenFilesButton().click();
        addJSPFilesPage.select( "META-INF/resources/configuration.jsp" );
        addJSPFilesPage.confirm();

        moduleFragmentOSGiBundlePage.getSelectOSGiBundleButton().click();

        selectOSGiBundlePage.setOSGiBundle( "com.liferay.blogs.web" );
        selectOSGiBundlePage.confirm();

        moduleFragmentOSGiBundlePage.getOverriddenFiles().containsItem( null );

        String[] files = new String[] { "META-INF/resources/blogs_admin/configuration.jsp",
            "META-INF/resources/blogs_aggregator/init.jsp", "META-INF/resources/blogs/asset/abstract.jsp",
            "META-INF/resources/blogs/edit_entry.jsp", "portlet.properties" };

        for( String file : files )
        {
            moduleFragmentOSGiBundlePage.getAddOverriddenFilesButton().click();
            addJSPFilesPage.select( file );
            addJSPFilesPage.confirm();
        }

        moduleFragmentOSGiBundlePage.finish();
        sleep( 7000 );

        String[] pathTree = new String[] { projectName, "src/main/java" };

        projectTree.expandNode( pathTree ).doubleClick( "portlet-ext.properties" );

        pathTree = new String[] { projectName, "src/main/resources", "META-INF", "resources", "blogs" };

        projectTree.expandNode( pathTree ).doubleClick( "edit_entry.jsp" );

        pathTree = new String[] { projectName, "src/main/resources", "META-INF", "resources", "blogs", "asset" };

        projectTree.expandNode( pathTree ).doubleClick( "abstract.jsp" );

        pathTree = new String[] { projectName, "src/main/resources", "META-INF", "resources", "blogs_admin" };

        projectTree.expandNode( pathTree ).doubleClick( "configuration.jsp" );

        pathTree = new String[] { projectName, "src/main/resources", "META-INF", "resources", "blogs_aggregator" };

        projectTree.expandNode( pathTree ).doubleClick( "init.jsp" );
    }

    @Test
    public void mavenModuleFragmentProjectWizard()
    {
        addLiferayServerAndOpenWizard();

        String projectName = "maven-fragment-project";

        newModuleFragmentPage.setProjectName( projectName, TEXT_BUILD_TYPE_MAVEN );
        sleep();
        assertTrue( newModuleFragmentPage.isLiferayRuntimeTextEnabled() );
        newModuleFragmentPage.next();

        // select OSGi Bundle and Overridden files

        assertEquals( TEXT_OSGI_BUNDLE_BLANK, moduleFragmentOSGiBundlePage.getValidationMessage() );

        moduleFragmentOSGiBundlePage.getSelectOSGiBundleButton().click();

        selectOSGiBundlePage.setOSGiBundle( "com.liferay.blogs.web" );
        selectOSGiBundlePage.confirm();

        moduleFragmentOSGiBundlePage.getOverriddenFiles().containsItem( null );

        String[] files = new String[] { "META-INF/resources/blogs_admin/configuration.jsp",
            "META-INF/resources/blogs_aggregator/init.jsp", "META-INF/resources/blogs/asset/abstract.jsp",
            "META-INF/resources/blogs/edit_entry.jsp", "portlet.properties" };

        for( String file : files )
        {
            moduleFragmentOSGiBundlePage.getAddOverriddenFilesButton().click();
            addJSPFilesPage.select( file );
            addJSPFilesPage.confirm();
        }

        moduleFragmentOSGiBundlePage.finish();
        sleep( 6000 );

        projectTree.setFocus();
        projectTree.expandNode( projectName ).doubleClick();

        assertTrue( projectTree.expandNode( projectName, "src/main/java", "portlet-ext.properties" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/resources", "META-INF", "resources", "blogs", "asset",
                "abstract.jsp" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/resources", "META-INF", "resources", "blogs", "edit_entry.jsp" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/resources", "META-INF", "resources", "blogs_admin",
                "configuration.jsp" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/resources", "META-INF", "resources", "blogs_aggregator",
                "init.jsp" ).isVisible() );

        String pomXmlFileName = "pom.xml";

        projectTree.expandNode( projectName, pomXmlFileName ).doubleClick();

        CTabItemPO switchCTabItem = new CTabItemPO( bot, "pom.xml" );
        switchCTabItem.click();

        TextEditorPO pomXmlFile = eclipse.getTextEditor( projectName + "/pom.xml" );

        assertContains( "<artifactId>maven-fragment-project</artifactId>", pomXmlFile.getText() );

        pomXmlFile.close();

    }

}
