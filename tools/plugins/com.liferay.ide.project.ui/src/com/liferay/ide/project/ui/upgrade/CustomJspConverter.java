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

package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.ImportLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.ImportLiferayModuleProjectOpMethods;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.upgrade.animated.CustomJspPage;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IRuntime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Andy Wu
 */
public class CustomJspConverter
{

    class FileVisitorImpl implements FileVisitor<Path>
    {

        private List<String> filePaths;

        private Path source;

        public FileVisitorImpl( Path source )
        {
            filePaths = new ArrayList<String>();

            this.source = source;
        }

        public List<String> getResults()
        {
            return filePaths;
        }

        @Override
        public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs ) throws IOException
        {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory( Path dir, IOException exc ) throws IOException
        {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException
        {
            String relativizePath = source.relativize( file ).toString().replaceAll( "\\\\", "/" );

            filePaths.add( relativizePath );

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed( Path file, IOException exc ) throws IOException
        {
            return FileVisitResult.CONTINUE;
        }
    }

    private String coreJspHookResourcesPath = "codeupgrade.corejsphook/src/main/resources/META-INF/resources/";

    private static List<String> jspPathMap;
    private static Map<String, String> portlet2ModuleMap;

    public static String resultPrefix = "result";
    public static String sourcePrefix = "source";

    private IRuntime liferay70Runtime;

    private String liferay62ServerLocation = null;

    private static String resultFileName = "convertJspHookResult.properties";
    private Properties resultProp;

    private String sourcePortletDir = null;

    private CustomJspPage ui;

    static
    {
        portlet2ModuleMap = new HashMap<String, String>();

        portlet2ModuleMap.put( "activities", "com.liferay.social.activities.web" );
        // mapper.put( "amazon_rankings", "com.liferay.amazon.rankings.web");
        portlet2ModuleMap.put( "announcements", "com.liferay.announcements.web" );
        portlet2ModuleMap.put( "asset_browser", "com.liferay.asset.browser.web" );
        portlet2ModuleMap.put( "asset_categories_navigation", "com.liferay.asset.categories.navigation.web" );
        portlet2ModuleMap.put( "asset_category_admin", "com.liferay.asset.categories.admin.web" );
        portlet2ModuleMap.put( "asset_publisher", "com.liferay.asset.publisher.web" );
        portlet2ModuleMap.put( "asset_tags_navigation", "com.liferay.asset.tags.navigation.web" );
        portlet2ModuleMap.put( "asset_tag_admin", "com.liferay.asset.tags.admin.web" );
        portlet2ModuleMap.put( "blogs", "com.liferay.blogs.web" );
        portlet2ModuleMap.put( "blogs_admin", "com.liferay.blogs.web" );
        portlet2ModuleMap.put( "blogs_aggregator", "com.liferay.blogs.web" );
        portlet2ModuleMap.put( "bookmarks", "com.liferay.bookmarks.web" );
        portlet2ModuleMap.put( "breadcrumb", "com.liferay.site.navigation.breadcrumb.web" );
        // mapper.put( "currency_converter", "com.liferay.currency.converter.web");
        // mapper.put( "dictionary", "com.liferay.dictionary.web");
        portlet2ModuleMap.put( "document_library", "com.liferay.document.library.web" );
        portlet2ModuleMap.put( "document_library_display", "com.liferay.document.library.web" );
        portlet2ModuleMap.put( "image_gallery_display", "com.liferay.document.library.web" );
        portlet2ModuleMap.put( "dynamic_data_lists", "com.liferay.dynamic.data.lists.web" );
        portlet2ModuleMap.put( "dynamic_data_list_display", "com.liferay.dynamic.data.lists.web" );
        portlet2ModuleMap.put( "dynamic_data_mapping", "com.liferay.dynamic.data.mapping.web" );
        portlet2ModuleMap.put( "expando", "com.liferay.expando.web" );
        portlet2ModuleMap.put( "group_statistics", "com.liferay.social.group.statistics.web" );
        portlet2ModuleMap.put( "hello_velocity", "com.liferay.hello.velocity.web" );
        portlet2ModuleMap.put( "iframe", "com.liferay.iframe.web" );
        // mapper.put( "invitation", "com.liferay.invitation.web");
        portlet2ModuleMap.put( "journal", "com.liferay.journal.web" );
        portlet2ModuleMap.put( "journal_content", "com.liferay.journal.content.web" );
        portlet2ModuleMap.put( "journal_content_search", "com.liferay.journal.content.search.web" );
        portlet2ModuleMap.put( "language", "com.liferay.site.navigation.language.web" );
        portlet2ModuleMap.put( "layouts_admin", "com.liferay.layout.admin.web" );
        portlet2ModuleMap.put( "layout_prototypes", "com.liferay.layout.prototype.web" );
        portlet2ModuleMap.put( "layout_set_prototypes", "com.liferay.layout.set.prototype.web" );
        // mapper.put( "loan_calculator", "com.liferay.loan.calculator.web");
        portlet2ModuleMap.put( "login", "com.liferay.login.web" );
        portlet2ModuleMap.put( "message_boards", "com.liferay.message.boards.web" );
        portlet2ModuleMap.put( "message_boards_admin", "com.liferay.message.boards.web" );
        portlet2ModuleMap.put( "mobile_device_rules", "com.liferay.mobile.device.rules.web" );
        portlet2ModuleMap.put( "monitoring", "com.liferay.monitoring.web" );
        portlet2ModuleMap.put( "my_account", "com.liferay.my.account.web" );
        portlet2ModuleMap.put( "my_sites", "com.liferay.site.my.sites.web" );
        portlet2ModuleMap.put( "navigation", "com.liferay.site.navigation.menu.web" );
        portlet2ModuleMap.put( "nested_portlets", "com.liferay.nested.portlets.web" );
        // mapper.put( "network", "com.liferay.network.utilities.web");
        portlet2ModuleMap.put( "page_comments", "com.liferay.comment.page.comments.web" );
        portlet2ModuleMap.put( "page_flags", "com.liferay.flags.web" );
        portlet2ModuleMap.put( "page_ratings", "com.liferay.ratings.page.ratings.web" );
        // mapper.put( "password_generator", "com.liferay.password.generator.web");
        portlet2ModuleMap.put( "password_policies_admin", "com.liferay.password.policies.admin.web" );
        portlet2ModuleMap.put( "plugins_admin", "com.liferay.plugins.admin.web" );
        portlet2ModuleMap.put( "polls", "com.liferay.polls.web" );
        portlet2ModuleMap.put( "polls_display", "com.liferay.polls.web" );
        portlet2ModuleMap.put( "portal_settings", "com.liferay.portal.settings.web" );
        portlet2ModuleMap.put( "portlet_configuration", "com.liferay.portlet.configuration.web" );
        portlet2ModuleMap.put( "portlet_css", "com.liferay.portlet.configuration.css.web" );
        portlet2ModuleMap.put( "quick_note", "com.liferay.quick.note.web" );
        portlet2ModuleMap.put( "recent_bloggers", "com.liferay.blogs.recent.bloggers.web" );
        portlet2ModuleMap.put( "requests", "com.liferay.social.requests.web" );
        portlet2ModuleMap.put( "roles_admin", "com.liferay.roles.admin.web" );
        portlet2ModuleMap.put( "rss", "com.liferay.rss.web" );
        portlet2ModuleMap.put( "search", "com.liferay.portal.search.web" );
        // mapper.put( "shopping", "com.liferay.shopping.web");
        portlet2ModuleMap.put( "sites_admin", "com.liferay.site.admin.web" );
        portlet2ModuleMap.put( "sites_directory", "com.liferay.site.navigation.directory.web" );
        portlet2ModuleMap.put( "site_browser", "com.liferay.site.browser.web" );
        portlet2ModuleMap.put( "site_map", "com.liferay.site.navigation.site.map.web" );
        portlet2ModuleMap.put( "social_activity", "com.liferay.social.activity.web" );
        portlet2ModuleMap.put( "staging_bar", "com.liferay.staging.bar.web" );
        // mapper.put( "translator", "com.liferay.translator.web");
        portlet2ModuleMap.put( "trash", "com.liferay.trash.web" );
        // mapper.put( "unit_converter", "com.liferay.unit.converter.web");
        portlet2ModuleMap.put( "users_admin", "com.liferay.users.admin.web" );
        portlet2ModuleMap.put( "user_groups_admin", "com.liferay.user.groups.admin.web" );
        portlet2ModuleMap.put( "user_statistics", "com.liferay.social.user.statistics.web" );
        portlet2ModuleMap.put( "web_proxy", "com.liferay.web.proxy.web" );
        portlet2ModuleMap.put( "wiki", "com.liferay.wiki.web" );
        portlet2ModuleMap.put( "wiki_display", "com.liferay.wiki.web" );
        portlet2ModuleMap.put( "workflow_definitions", "com.liferay.portal.workflow.definition.web" );
        portlet2ModuleMap.put( "workflow_definition_links", "com.liferay.portal.workflow.definition.link.web" );
        portlet2ModuleMap.put( "workflow_instances", "com.liferay.portal.workflow.instance.web" );
        portlet2ModuleMap.put( "workflow_tasks", "com.liferay.portal.workflow.task.web" );
        portlet2ModuleMap.put( "xsl_content", "com.liferay.xsl.content.web" );

        jspPathMap = new ArrayList<String>();

        jspPathMap.add( "bookmarks" );
        jspPathMap.add( "blogs" );
        jspPathMap.add( "blogs_admin" );
        jspPathMap.add( "blogs_aggregator" );
        jspPathMap.add( "document_library" );
        jspPathMap.add( "image_gallery_display" );
        jspPathMap.add( "message_boards" );
        jspPathMap.add( "message_boards_admin" );
        jspPathMap.add( "wiki" );
        jspPathMap.add( "wiki_display" );
        // jspPathMap.add("document_library_display");
    }

    public static void clearConvertResults()
    {
        IPath path = ProjectUI.getDefault().getStateLocation().append( resultFileName );

        File resultFile = path.toFile();

        if( resultFile.exists() )
        {
            resultFile.delete();
        }
    }

    public static String[] getConvertResult( String filter )
    {
        List<String> results = new ArrayList<String>();

        IPath path = ProjectUI.getDefault().getStateLocation().append( resultFileName );

        File resultFile = path.toFile();

        if( !resultFile.exists() )
        {
            return null;
        }

        Properties resultProp = PropertiesUtil.loadProperties( resultFile );

        if( resultProp == null || resultProp.keySet().isEmpty() )
        {
            return null;
        }

        Enumeration<?> keys = resultProp.propertyNames();

        while( keys.hasMoreElements() )
        {
            String key = (String) keys.nextElement();

            if( key.startsWith( filter ) )
            {
                results.add( resultProp.getProperty( key ) );
            }
        }

        return results.toArray( new String[] {} );
    }

    public static String getCustomJspPath( String sourcePath )
    {
        String hookFilePath = "/docroot/WEB-INF/liferay-hook.xml";

        File hookFile = new File( sourcePath + hookFilePath );

        if( !hookFile.exists() )
        {
            return null;
        }

        String customJspPath = null;

        try
        {
            DocumentBuilder domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            domBuilder.setEntityResolver( new EntityResolver()
            {

                public InputSource resolveEntity( String publicId, String systemId ) throws SAXException, IOException
                {
                    // don't connect internet to fetch dtd for validation
                    return new InputSource( new ByteArrayInputStream( new String( "" ).getBytes() ) );
                }
            } );

            InputStream input = new FileInputStream( hookFile );
            Document doc = domBuilder.parse( input );
            Element root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for( int i = 0; i < nodeList.getLength(); i++ )
            {
                Node node = nodeList.item( i );
                if( node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals( "custom-jsp-dir" ) )
                {
                    customJspPath = node.getFirstChild().getNodeValue();
                }
            }

            input.close();
        }
        catch( Exception e )
        {
        }

        if( customJspPath == null || customJspPath.trim().length() == 0 )
        {
            return null;
        }

        File customJspDir = new File( sourcePath + "/docroot/" + customJspPath );

        if( !customJspDir.exists() || !customJspDir.isDirectory() )
        {
            return null;
        }

        return customJspPath;
    }

    // the main method of converting jsp hook project
    public void convertJspHookProject(
        String[] sourcePaths, String targetPath, IProgressMonitor monitor, boolean isLiferayWorkspace ) throws Exception
    {
        resultProp = new Properties();

        for( String sourcePath : sourcePaths )
        {
            String customJspPath = getCustomJspPath( sourcePath );

            if( customJspPath == null || customJspPath.trim().length() <= 0 )
            {
                throw new Exception( "convert failed, can't find custom jsp folder" );
            }

            convertToCoreJspHook( sourcePath, customJspPath, targetPath, monitor, isLiferayWorkspace );

            convertToFragment( sourcePath, customJspPath, targetPath );

            File sourceFile = new File( sourcePath );

            resultProp.setProperty( sourcePrefix + "." + sourceFile.getName(),
                sourceFile.getName() + ":" + customJspPath );
        }

        saveResultProperties();
    }

    // convert common, portal, taglib folders to 7.x CustomJspBag
    private void convertToCoreJspHook(
        String sourcePath, String customJspPath, String targetPath, IProgressMonitor monitor,
        boolean isLiferayWorkspace ) throws Exception
    {
        File commonDir = getMovedDir( sourcePath, customJspPath, "common" );
        File portalDir = getMovedDir( sourcePath, customJspPath, "portal" );
        File taglibDir = getMovedDir( sourcePath, customJspPath, "taglib" );

        // at least one folder exist
        if( commonDir != null || portalDir != null || taglibDir != null )
        {
            File[] dirs = new File[3];

            dirs[0] = commonDir;
            dirs[1] = portalDir;
            dirs[2] = taglibDir;

            File location = new File( targetPath );

            final URL projectZipUrl =
                ProjectUI.getDefault().getBundle().getEntry( "resources/codeupgrade.corejsphook.zip" );

            final File projectZipFile = new File( FileLocator.toFileURL( projectZipUrl ).getFile() );

            ZipUtil.unzip( projectZipFile, location );

            // File jspDir = new File( location, coreJspHookResourcesPath + "html" );

            File ignoreFolder = new File( location, coreJspHookResourcesPath + ".ignore/" );
            File destFolder = new File( location, coreJspHookResourcesPath );

            PortalBundle portalBundle = LiferayServerCore.newPortalBundle( getLiferay70Runtime().getLocation() );

            for( File dir : dirs )
            {
                if( dir != null )
                {
                    // File dest = new File( jspDir, dir.getName() );

                    // dest.mkdirs();

                    // IOUtil.copyDirToDir( dir, dest );

                    // copy 70 original jsp file to converted project ignore folder
                    List<String> fileRelativizePaths = getAllRelativizeFilePaths( dir );

                    for( String fileRelativizePath : fileRelativizePaths )
                    {
                        File original62File = new File( get62HtmlDir() + dir.getName() + "/" + fileRelativizePath );

                        File original70File = portalBundle.getAppServerDir().append(
                            "webapps/ROOT/html/" + dir.getName() + "/" + fileRelativizePath ).toFile();

                        if( original62File.exists() && original70File.exists() )
                        {
                            File target62File =
                                new File( ignoreFolder, "html/" + dir.getName() + "/" + fileRelativizePath + ".62" );
                            File target70File =
                                new File( destFolder, "html/" + dir.getName() + "/" + fileRelativizePath );

                            makeParentDir( target62File );
                            makeParentDir( target70File );

                            FileUtil.copyFile( original62File, target62File );
                            FileUtil.copyFile( original70File, target70File );
                        }
                    }
                }
            }

            String sourceProjectName = ( new File( sourcePath ) ).getName();

            File projectFolder = new File( location, "codeupgrade.corejsphook" );
            File gradleWsFile = new File( projectFolder, "build-ws.gradle" );
            File gradleFile = new File( projectFolder, "build.gradle" );

            if( isLiferayWorkspace )
            {
                gradleFile.delete();

                gradleWsFile.renameTo( gradleFile );
            }
            else
            {
                gradleWsFile.delete();
            }

            File newFolder = new File( location, sourceProjectName + ".corejsphook" );

            projectFolder.renameTo( newFolder );

            resultProp.setProperty( resultPrefix + "." + sourceProjectName + "/portalCore",
                newFolder.getAbsolutePath().replace( "\\\\", "/" ) );
        }
    }

    // convert portlets under portlet dir into fragment
    private void convertToFragment( String sourcePath, String customJspPath, String targetPah ) throws Exception
    {
        File[] portlets = getPortletDirs( sourcePath, customJspPath );

        if( portlets == null )
        {
            return;
        }

        for( File portlet : portlets )
        {
            String fragmentPath = createFragment( portlet.getName(), sourcePath, targetPah );

            File sourceFile = new File( sourcePath );

            if( fragmentPath != null && !fragmentPath.trim().isEmpty() )
            {
                resultProp.setProperty( resultPrefix + "." + sourceFile.getName() + "/portlet/" + portlet.getName(),
                    fragmentPath );
            }
        }
    }

    private void copy62JspFile( String portlet, String jsp, File targetJspDir, String mappedJsp ) throws Exception
    {
        String htmlDir = get62HtmlDir();

        if( htmlDir != null )
        {
            File jsp62 = new File( htmlDir + "portlet/" + portlet + "/" + jsp );

            File targetFile = new File( targetJspDir + "/.ignore/", mappedJsp + ".62" );

            makeParentDir( targetFile );

            FileUtil.copyFile( jsp62, targetFile );
        }
    }

    private void copy70JspFile( String portlet, File targetJspDir, String mappedJsp ) throws Exception
    {
        File module = getModuleFile( portlet );

        JarFile jarFile = new JarFile( module );

        JarEntry entry = (JarEntry) jarFile.getEntry( "META-INF/resources/" + mappedJsp );

        InputStream ins = jarFile.getInputStream( entry );

        File targetFile = new File( targetJspDir, mappedJsp );

        makeParentDir( targetFile );

        FileUtil.writeFile( targetFile, ins );

        jarFile.close();
    }

/*    private void copyCustomJspFile(
        String sourceJsp, String jsp, File targetJspDir, boolean isIgnore, String mappedJsp ) throws Exception
    {
        File srcJsp = new File( sourceJsp, jsp );

        File targetJsp = null;

        if( isIgnore )
        {
            targetJsp = new File( targetJspDir + "/.ignore/", mappedJsp );
        }
        else
        {
            targetJsp = new File( targetJspDir, mappedJsp );
        }

        makeParentDir( targetJsp );

        FileUtil.copyFile( srcJsp, targetJsp );
    }*/

    private String createEmptyJspHookProject( String portlet, String originProjectName, String targetPath )
        throws Exception
    {
        String projectName = originProjectName + "-" + portlet + "-fragment";

        String module = portlet2ModuleMap.get( portlet );

        if( module == null )
        {
            return null;
        }

        StringBuilder strBuilder = new StringBuilder( "create " );

        strBuilder.append( "-d \"" + targetPath + "\" " );
        strBuilder.append( "-t " + "fragment" + " " );
        strBuilder.append( "-h " + module + " " );
        strBuilder.append( "-H " + getModuleVersion( portlet ) + " " );
        strBuilder.append( "\"" + projectName + "\"" );

        BladeCLI.execute( strBuilder.toString() );

        return projectName;
    }

    private String createFragment( String portlet, String sourcePath, String targetPath ) throws Exception
    {
        String result = null;

        File src = new File( sourcePath );

        String originProjectName = src.getName();

        String projectName = createEmptyJspHookProject( portlet, originProjectName, targetPath );

        if( projectName == null )
        {
            return null;
        }

        // String sourceJsp = sourcePortletDir + "/" + portlet;

        File targetJspDir = new File( targetPath + "/" + projectName + "/src/main/resources/META-INF/resources/" );

        List<String> jspList = getAllFilesFromSourcePortletDir( portlet );

        List<String> moduleJsps = getAllFilesFromModuleJar( portlet );

        for( String jsp : jspList )
        {
            String mappedJsp = jspPathConvert( portlet, jsp );

            // copyCustomJspFile( sourceJsp, jsp, targetJspDir, false, mappedJsp );

            if( moduleJsps != null && moduleJsps.contains( mappedJsp ) )
            {
                copy62JspFile( portlet, jsp, targetJspDir, mappedJsp );

                copy70JspFile( portlet, targetJspDir, mappedJsp );
            }
        }

        result = targetPath + "/" + projectName;

        return result;
    }

    public void doExecute( String[] projectPaths, String targetPath, boolean isLiferayWorkspace )
    {
        Job job = new WorkspaceJob( "Converting Jsp hook to fragments..." )
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
            {
                IStatus retval = Status.OK_STATUS;

                try
                {
                    convertJspHookProject( projectPaths, targetPath, monitor, isLiferayWorkspace );

                    String[] projectPaths = getConvertResult( resultPrefix );

                    if( projectPaths != null && projectPaths.length > 0 )
                    {
                        for( String path : projectPaths )
                        {
                            ImportLiferayModuleProjectOp importOp = ImportLiferayModuleProjectOp.TYPE.instantiate();

                            importOp.setLocation( path );

                            if( importOp.validation().severity() != org.eclipse.sapphire.modeling.Status.Severity.ERROR )
                            {
                                ImportLiferayModuleProjectOpMethods.execute( importOp,
                                    ProgressMonitorBridge.create( monitor ) );
                            }
                        }

                        refreshUI();
                    }
                }
                catch( Exception e )
                {
                    retval = ProjectUI.createErrorStatus( "Error in convert jsp", e );
                }

                return retval;
            }
        };

        try
        {
            PlatformUI.getWorkbench().getProgressService().showInDialog( Display.getDefault().getActiveShell(), job );

            job.schedule();
        }
        catch( Exception e )
        {
        }
    }

    private List<String> getAllFilesFromModuleJar( String portlet ) throws Exception
    {
        List<String> result = new ArrayList<String>();

        File moduleFile = getModuleFile( portlet );

        if( moduleFile == null )
        {
            return null;
        }

        JarFile jarFile = new JarFile( moduleFile );

        Enumeration<JarEntry> jarEntrys = jarFile.entries();

        while( jarEntrys.hasMoreElements() )
        {
            JarEntry entry = jarEntrys.nextElement();

            String entryName = entry.getName();

            if( entryName.startsWith( "META-INF/resources/" ) && !entry.isDirectory() )
            {
                result.add( entry.getName().substring( 19 ) );
            }
        }

        jarFile.close();

        return result;
    }

    private List<String> getAllFilesFromSourcePortletDir( String portlet )
    {
        File portletDir = new File( sourcePortletDir + "/" + portlet );

        return getAllRelativizeFilePaths( portletDir );
    }

    private List<String> getAllRelativizeFilePaths( File file )
    {
        List<String> retVal = new ArrayList<String>();

        Path source = file.toPath();

        FileVisitorImpl visitor = new FileVisitorImpl( source );

        try
        {
            Files.walkFileTree( source, visitor );

            retVal = visitor.getResults();
        }
        catch( IOException e )
        {
        }

        return retVal;
    }

    private String get62HtmlDir()
    {
        if( CoreUtil.empty( liferay62ServerLocation ) )
        {
            return null;
        }

        File bundleDir = new File( liferay62ServerLocation );

        String[] names = bundleDir.list( new FilenameFilter()
        {

            @Override
            public boolean accept( File dir, String name )
            {
                if( name.startsWith( "tomcat-" ) )
                {
                    return true;
                }

                return false;
            }
        } );

        if( names != null && names.length == 1 )
        {
            return liferay62ServerLocation + "/" + names[0] + "/webapps/ROOT/html/";
        }
        else
        {
            return null;
        }
    }

    public IRuntime getLiferay70Runtime()
    {
        return liferay70Runtime;
    }

    private File getModuleFile( String portlet )
    {
        String moduleName = portlet2ModuleMap.get( portlet );

        if( moduleName == null )
        {
            return null;
        }

        String moduleFileName = null;

        for( String name : ServerUtil.getModuleFileListFrom70Server( getLiferay70Runtime() ) )
        {
            if( name.contains( moduleName ) )
            {
                moduleFileName = name;
            }
        }

        if( moduleFileName == null )
        {
            return null;
        }

        final IPath temp = ProjectCore.getDefault().getStateLocation().append( "moduleCache" );

        File tempFile = temp.toFile();

        if( !tempFile.exists() )
        {
            tempFile.mkdirs();
        }

        return ServerUtil.getModuleFileFrom70Server( getLiferay70Runtime(), moduleFileName, temp );
    }

    private String getModuleVersion( String portlet ) throws Exception
    {
        File moduleFile = getModuleFile( portlet );

        JarFile jarFile = new JarFile( moduleFile );

        String version = jarFile.getManifest().getMainAttributes().getValue( "Bundle-Version" );

        jarFile.close();

        return version;
    }

    private File getMovedDir( String sourcePath, String customJspPath, String dirName )
    {
        String path = "/html/" + dirName;

        File dir = new File( sourcePath + "/docroot/" + customJspPath + path );

        if( dir.exists() )
        {
            return dir;
        }
        else
        {
            return null;
        }
    }

    private File[] getPortletDirs( String sourcePath, String customJspPath )
    {
        sourcePortletDir = sourcePath + "/docroot/" + customJspPath + "/html/portlet/";

        File portletDir = new File( sourcePortletDir );

        if( !portletDir.exists() || !portletDir.isDirectory() )
        {
            return null;
        }

        File[] portlets = portletDir.listFiles( new FileFilter()
        {

            @Override
            public boolean accept( File file )
            {
                if( file.isDirectory() )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        } );

        return portlets;
    }

    // some jsps in some portlets were moved to subfolder in corresponding jar so the path need to be converted
    private String jspPathConvert( String portletName, String jspPath )
    {
        String result = jspPath;

        if( jspPathMap.contains( portletName ) )
        {
            result = portletName + "/" + jspPath;
        }

        return result;
    }

    private void makeParentDir( File target ) throws Exception
    {
        File parent = target.getParentFile();

        if( !parent.exists() && !parent.mkdirs() )
        {
            throw new Exception( "can't create dir " + parent );
        }
    }

    public void refreshUI()
    {
        UIUtil.async( new Runnable()
        {

            @Override
            public void run()
            {
                ui.refreshTreeViews();
            }
        } );
    }

    private void saveResultProperties()
    {
        IPath path = ProjectUI.getDefault().getStateLocation().append( resultFileName );

        File resultFile = path.toFile();

        if( resultFile.exists() )
        {
            resultFile.delete();
        }

        PropertiesUtil.saveProperties( resultProp, resultFile );
    }

    public void setLiferay62ServerLocation( String liferay62ServerLocation )
    {
        this.liferay62ServerLocation = liferay62ServerLocation;
    }

    public void setLiferay70Runtime( IRuntime liferay70Runtime )
    {
        this.liferay70Runtime = liferay70Runtime;
    }

    public void setUi( CustomJspPage ui )
    {
        this.ui = ui;
    }
}
