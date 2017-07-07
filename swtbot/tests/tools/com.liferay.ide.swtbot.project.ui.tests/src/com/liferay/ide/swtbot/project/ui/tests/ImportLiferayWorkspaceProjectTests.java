
package com.liferay.ide.swtbot.project.ui.tests;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;

import org.junit.Test;

import com.liferay.ide.swtbot.project.ui.tests.page.ImportLiferayWorkspaceProjectPO;
import com.liferay.ide.swtbot.project.ui.tests.page.SelectTypePO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.DeleteResourcesContinueDialog;
import com.liferay.ide.swtbot.ui.tests.page.CTabItemPO;
import com.liferay.ide.swtbot.ui.tests.page.TextEditorPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Sunny Shi
 */
public class ImportLiferayWorkspaceProjectTests extends SWTBotBase implements ImportLiferayWorkspaceProject
{

    private String liferayWorkspaceRootPath = System.getProperty( "user.dir" );

    TreePO projectTree = eclipse.showPackageExporerView().getProjectTree();

    ImportLiferayWorkspaceProjectPO importLiferayWorkspaceProject = new ImportLiferayWorkspaceProjectPO(
        bot, TITLE_IMPORT_LIFERAY_WORKSPACE, INDEX_PLEASE_SELECT_THE_WORKSPACE_LOCATION );

    ImportLiferayWorkspaceProjectPO importLiferayWorkspaceProjectWithServerLabel = new ImportLiferayWorkspaceProjectPO(
        bot, TITLE_IMPORT_LIFERAY_WORKSPACE, INDEX_PLEASE_SELECT_THE_WORKSPACE_LOCATION_WITH_SERVERNAME );

    SelectTypePO selectImportPage =
        new SelectTypePO( bot, INDEX_SELECT_IMPORT_LIFERAY_WORKSPACE_PROJECT_VALIDATION_MESSAGE );

    DeleteResourcesContinueDialog continueDeleteResources =
        new DeleteResourcesContinueDialog( bot, "Delete Resources" );

    @Before
    public void importWorkspaceProject()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        eclipse.getLiferayWorkspacePerspective().activate();
        eclipse.showPackageExporerView();

        importLiferayWorkspaceProject();
    }

    @After
    public void clean()
    {
        eclipse.closeShell( TITLE_IMPORT_LIFERAY_WORKSPACE );

        if( addedProjects() )
        {
            eclipse.getPackageExporerView().deleteProjectExcludeNames(
                new String[] { getLiferayPluginsSdkName() }, false );
        }
    }

    public void importLiferayWorkspaceProject()
    {
        eclipse.getFileMenu().clickMenu( LABEL_IMPORT );

        selectImportPage.selectItem( "liferay", "Liferay", LABEL_IMPORT_LIFERAY_WORKSPACE_PROJECT );
        assertEquals(
            TEXT_SELECT_IMPORT_LIFERAY_WORKSPACE_PROJECT_VALIDATION_MESSAGE, selectImportPage.getValidationMessage() );
        selectImportPage.next();
    }

    @Test
    public void importGradleLiferayWorkspaceProject()
    {
        importLiferayWorkspaceProject.setWorkspaceLocation(
            liferayWorkspaceRootPath + "/projects/testGradleWorkspace" );

        String liferayWorkspaceName = "testGradleWorkspace";

        sleep();
        assertEquals( TEXT_GRADLE_LIFERAY_WORKSPACE, importLiferayWorkspaceProject.getBuildTypeText().getText() );
        sleep();
        assertEquals( "", importLiferayWorkspaceProject.getServerName().getText() );
        importLiferayWorkspaceProject.finish();
        sleep( 8000 );

        projectTree.setFocus();
        sleep();

        bot.viewByTitle( "Package Explorer" ).show();
        projectTree.expandNode( liferayWorkspaceName ).select();
        projectTree.expandNode( liferayWorkspaceName, "bundles" ).select();
        projectTree.getTreeItem( liferayWorkspaceName ).getTreeItem( "bundles" ).doAction( BUTTON_DELETE );

        continueDeleteResources.confirm();
        sleep( 6000 );

        if( addedProjects() )
        {
            eclipse.getPackageExporerView().deleteProjectExcludeNames(
                new String[] { getLiferayPluginsSdkName() }, false );
        }

        importLiferayWorkspaceProject();

        importLiferayWorkspaceProject.setWorkspaceLocation(
            liferayWorkspaceRootPath + "/projects/testGradleWorkspace" );
        sleep();
        assertEquals(
            TEXT_SELECT_LOCATION_OF_LIFERAY_WORKSPACE_PARENT_DIRECTORY,
            importLiferayWorkspaceProject.getValidationMessage() );

        assertEquals( TEXT_GRADLE_LIFERAY_WORKSPACE, importLiferayWorkspaceProject.getBuildTypeText().getText() );

        importLiferayWorkspaceProject.getDownloadLiferaybundle().select();
        sleep();

        assertEquals( "", importLiferayWorkspaceProject.getServerName().getText() );
        assertEquals( "", importLiferayWorkspaceProject.getBundleUrl().getText() );
        sleep();

        importLiferayWorkspaceProject.getServerName().setText( "test-lrws" );
        importLiferayWorkspaceProject.finish();
        sleep( 20000 );

        projectTree.setFocus();
        bot.viewByTitle( "Package Explorer" ).show();
        projectTree.expandNode( liferayWorkspaceName, "bundles" );
        projectTree.getTreeItem( liferayWorkspaceName ).getTreeItem( "bundles" ).doAction( BUTTON_DELETE );
        continueDeleteResources.confirm();

        sleep( 6000 );

        projectTree.getTreeItem( liferayWorkspaceName ).doAction( "Liferay", "Initialize Server Bundle" );

        sleep( 10000 );
        projectTree.getTreeItem( liferayWorkspaceName ).expand();

        sleep( 2000 );
        assertTrue( projectTree.getTreeItem( liferayWorkspaceName ).getTreeItem( "bundles" ).isVisible() );
        assertTrue( projectTree.getTreeItem( liferayWorkspaceName ).getTreeItem( "configs" ).isVisible() );
        assertTrue( projectTree.getTreeItem( liferayWorkspaceName ).getTreeItem( "gradle" ).isVisible() );

        String gradlePropertyFileName = "gradle.properties";
        String settingGradleFileName = "settings.gradle";

        projectTree.expandNode( liferayWorkspaceName, gradlePropertyFileName ).doubleClick();
        TextEditorPO gradlePropertiesFile = eclipse.getTextEditor( gradlePropertyFileName );

        assertContains( "liferay.workspace.modules.dir", gradlePropertiesFile.getText() );
        assertContains( "liferay.workspace.home.dir", gradlePropertiesFile.getText() );
        gradlePropertiesFile.close();
        sleep();

        projectTree.expandNode( liferayWorkspaceName, settingGradleFileName ).doubleClick();
        TextEditorPO settingGradleFile = eclipse.getTextEditor( settingGradleFileName );

        assertContains( "buildscript", settingGradleFile.getText() );
        assertContains( "repositories", settingGradleFile.getText() );

        settingGradleFile.close();
        sleep( 2000 );
    }

    @Test
    public void importMavenLiferayWorkspaceProject()
    {
        String liferayWorkspaceName = "testMavenWorkspace";

        importLiferayWorkspaceProject.setWorkspaceLocation( liferayWorkspaceRootPath + "/projects/testMavenWorkspace" );
        sleep();
        assertEquals(
            TEXT_SELECT_LOCATION_OF_LIFERAY_WORKSPACE_PARENT_DIRECTORY,
            importLiferayWorkspaceProject.getValidationMessage() );

        assertEquals( TEXT_MAVEN_LIFERAY_WORKSPACE, importLiferayWorkspaceProject.getBuildTypeText().getText() );
        assertFalse( importLiferayWorkspaceProject.getDownloadLiferaybundle().isChecked() );

        importLiferayWorkspaceProject.finish();
        sleep( 6000 );

        projectTree.setFocus();
        bot.viewByTitle( "Package Explorer" ).show();
        projectTree.expandNode( liferayWorkspaceName ).doubleClick();
        assertTrue( projectTree.getTreeItem( "testMavenWorkspace-modules" ).isVisible() );
        assertTrue( projectTree.getTreeItem( "testMavenWorkspace-themes" ).isVisible() );
        assertTrue( projectTree.getTreeItem( "testMavenWorkspace-wars" ).isVisible() );
        projectTree.getTreeItem( "testMavenWorkspace-modules" ).getTreeItem( "pom.xml" ).doubleClick();

        CTabItemPO switchCTabItem = new CTabItemPO( bot, "pom.xml" );
        sleep();
        switchCTabItem.click();

        TextEditorPO pomXmlFileModules = eclipse.getTextEditor( "testMavenWorkspace-modules/pom.xml" );
        assertContains( "testMavenWorkspace-modules", pomXmlFileModules.getText() );
        assertContains( "artifactId", pomXmlFileModules.getText() );

        pomXmlFileModules.close();
        sleep();

        projectTree.expandNode( liferayWorkspaceName, "pom.xml" ).doubleClick();
        sleep();
        switchCTabItem.click();

        TextEditorPO pomXmlFile = eclipse.getTextEditor( "testMavenWorkspace/pom.xml" );
        assertContains( "testMavenWorkspace", pomXmlFile.getText() );

        pomXmlFile.close();
        sleep();

        importLiferayWorkspaceProject();
        importLiferayWorkspaceProject.setWorkspaceLocation(
            liferayWorkspaceRootPath + "/projects/testGradleWorkspace" );

        sleep();
        assertEquals(
            TEXT_A_LIFERAY_WORKSPACE_PROJECT_ALREADY_EXISTS_IN_THIS_ECLIPSE_INSTANCE,
            importLiferayWorkspaceProjectWithServerLabel.getValidationMessage() );
        importLiferayWorkspaceProject.cancel();

    }

    @Test
    public void initialStateAndValidationProjectName()
    {
        assertEquals( TEXT_PLEASE_SELECT_THE_WORKSPACE_LOCATION, importLiferayWorkspaceProject.getValidationMessage() );

        assertEquals( TEXT_BLANK, importLiferayWorkspaceProject.getWorkspaceLocation().getText() );
        assertEquals( TEXT_BLANK, importLiferayWorkspaceProject.getBuildTypeText().getText() );
        assertFalse( importLiferayWorkspaceProject.getAddProjectToWorkingSet().isChecked() );

        assertTrue( importLiferayWorkspaceProject.getBackButton().isEnabled() );
        assertFalse( importLiferayWorkspaceProject.getNextButton().isEnabled() );
        assertFalse( importLiferayWorkspaceProject.getFinishButton().isEnabled() );
        assertTrue( importLiferayWorkspaceProject.getCancelButton().isEnabled() );

        importLiferayWorkspaceProject.setWorkspaceLocation( ".." );
        sleep();
        assertEquals(
            " " + "\"" + ".." + "\"" + TEXT_IS_NOT_AN_ABSOLUTE_PATH,
            importLiferayWorkspaceProject.getValidationMessage() );

        importLiferayWorkspaceProject.setWorkspaceLocation( "1.*" );
        sleep();
        assertEquals(
            " " + "\"" + "1.*" + "\"" + TEXT_IS_NOT_A_VALID_PATH,
            importLiferayWorkspaceProject.getValidationMessage() );

        importLiferayWorkspaceProject.setWorkspaceLocation( " " );
        sleep();
        assertEquals( TEXT_WORKSPACE_LOCATION_MUST_BE_SPECIFIED, importLiferayWorkspaceProject.getValidationMessage() );

        importLiferayWorkspaceProject.setWorkspaceLocation( "C://non-exist-dir" );
        sleep();
        assertEquals( TEXT_DIRECTORY_DOESNT_EXIST, importLiferayWorkspaceProject.getValidationMessage() );

        importLiferayWorkspaceProject.setWorkspaceLocation( "C:/Users" );
        sleep();
        assertEquals( TEXT_INVALID_LIFERAY_WORKSPACE, importLiferayWorkspaceProject.getValidationMessage() );

        importLiferayWorkspaceProject.setWorkspaceLocation(
            liferayWorkspaceRootPath + "/projects/testGradleWorkspace" );
        sleep();

        assertEquals(
            TEXT_THE_SERVER_OR_RUNTIME_NAME_IS_ALREADY_IN_USE,
            importLiferayWorkspaceProjectWithServerLabel.getValidationMessage() );

        importLiferayWorkspaceProject.cancel();;

    }

}
