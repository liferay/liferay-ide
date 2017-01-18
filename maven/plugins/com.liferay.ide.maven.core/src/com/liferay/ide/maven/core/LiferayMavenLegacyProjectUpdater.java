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

package com.liferay.ide.maven.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.upgrade.ILiferayLegacyProjectUpdater;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Andy Wu
 */
@SuppressWarnings( "restriction" )
public class LiferayMavenLegacyProjectUpdater implements ILiferayLegacyProjectUpdater
{

    private static String[][] dependenciesConvertMap;

    IStructuredFormatProcessor formatProcessor = new FormatProcessorXML();

    private void addChildNode( Element pluginNode, Document document, String tagName, String value )
    {
        Element testNode = document.createElement( tagName );

        testNode.appendChild( document.createTextNode( value ) );
        pluginNode.appendChild( testNode );
    }

    private void addCssBuilderPlugin( Node pluginsNode )
    {
        Document document = pluginsNode.getOwnerDocument();

        Element cssBuidlerPlugin = document.createElement( "plugin" );

        addChildNode( cssBuidlerPlugin, document, "groupId", "com.liferay" );
        addChildNode( cssBuidlerPlugin, document, "artifactId", "com.liferay.css.builder" );
        addChildNode( cssBuidlerPlugin, document, "version", "1.0.21" );

        Element executions = document.createElement( "executions" );
        Element execution = document.createElement( "execution" );

        executions.appendChild( execution );
        cssBuidlerPlugin.appendChild( executions );

        addChildNode( execution, document, "id", "default-build-css" );
        addChildNode( execution, document, "phase", "generate-sources" );

        Element goals = document.createElement( "goals" );
        execution.appendChild( goals );
        addChildNode( goals, document, "goal", "build-css" );

        Element configuration = document.createElement( "configuration" );
        cssBuidlerPlugin.appendChild( configuration );
        addChildNode( configuration, document, "docrootDirName", "src/main/webapp" );

        pluginsNode.appendChild( cssBuidlerPlugin );

        formatNode( cssBuidlerPlugin );
    }

    private void addCssBuilderThemePlugin( Node pluginsNode )
    {
        Document document = pluginsNode.getOwnerDocument();

        Element cssBuidlerPlugin = document.createElement( "plugin" );

        addChildNode( cssBuidlerPlugin, document, "groupId", "com.liferay" );
        addChildNode( cssBuidlerPlugin, document, "artifactId", "com.liferay.css.builder" );
        addChildNode( cssBuidlerPlugin, document, "version", "1.0.23" );

        Element executions = document.createElement( "executions" );
        Element execution = document.createElement( "execution" );

        executions.appendChild( execution );
        cssBuidlerPlugin.appendChild( executions );

        addChildNode( execution, document, "id", "default-build-css" );
        addChildNode( execution, document, "phase", "compile" );

        Element goals = document.createElement( "goals" );
        execution.appendChild( goals );

        addChildNode( goals, document, "goal", "build-css" );

        Element configuration = document.createElement( "configuration" );
        cssBuidlerPlugin.appendChild( configuration );
        addChildNode( configuration, document, "docrootDirName",
            "${com.liferay.portal.tools.theme.builder.outputDir}" );
        addChildNode( configuration, document, "outputDirName", "/" );
        addChildNode( configuration, document, "portalCommonPath", "target/deps/com.liferay.frontend.css.common.jar" );

        pluginsNode.appendChild( cssBuidlerPlugin );

        formatNode( cssBuidlerPlugin );
    }

    private void addDependency( Node dependenciesNode, String groupId, String artifactId, String version, String scope )
    {
        Document document = dependenciesNode.getOwnerDocument();

        NodeList dependencyList = document.getElementsByTagName( "dependency" );

        boolean isExist = false;

        if( dependencyList != null && dependencyList.getLength() > 0 )
        {
            for( int i = 0; i < dependencyList.getLength(); i++ )
            {
                Element dependency = (Element) dependencyList.item( i );

                String tempGroupId = dependency.getElementsByTagName( "groupId" ).item( 0 ).getTextContent();
                String tempArtifactId = dependency.getElementsByTagName( "artifactId" ).item( 0 ).getTextContent();
                String tempVersion = dependency.getElementsByTagName( "version" ).item( 0 ).getTextContent();

                if( groupId.equals( tempGroupId ) && artifactId.equals( tempArtifactId ) &&
                    version.equals( tempVersion ) )
                {
                    isExist = true;
                }
            }
        }

        if( !isExist )
        {
            Element dependency = document.createElement( "dependency" );
            addChildNode( dependency, document, "groupId", groupId );
            addChildNode( dependency, document, "artifactId", artifactId );
            addChildNode( dependency, document, "version", version );
            addChildNode( dependency, document, "scope", scope );

            dependenciesNode.appendChild( dependency );

            formatNode( dependenciesNode );
        }
    }

    private void addMavenDependencyPlugin( Node pluginsNode )
    {
        Document document = pluginsNode.getOwnerDocument();

        Element mavenDependencyPlguin = document.createElement( "plugin" );
        addChildNode( mavenDependencyPlguin, document, "artifactId", "maven-dependency-plugin" );

        Element executions = document.createElement( "executions" );
        Element execution = document.createElement( "execution" );

        executions.appendChild( execution );
        mavenDependencyPlguin.appendChild( executions );

        addChildNode( execution, document, "phase", "generate-sources" );

        Element goals = document.createElement( "goals" );
        execution.appendChild( goals );

        addChildNode( goals, document, "goal", "copy" );

        Element configuration = document.createElement( "configuration" );
        Element artifactItems = document.createElement( "artifactItems" );

        Element commonArtifactItem = document.createElement( "artifactItem" );
        addChildNode( commonArtifactItem, document, "groupId", "com.liferay" );
        addChildNode( commonArtifactItem, document, "artifactId", "com.liferay.frontend.css.common" );
        addChildNode( commonArtifactItem, document, "version", "2.0.1" );

        Element styledArtifactItem = document.createElement( "artifactItem" );
        addChildNode( styledArtifactItem, document, "groupId", "com.liferay" );
        addChildNode( styledArtifactItem, document, "artifactId", "com.liferay.frontend.theme.styled" );
        addChildNode( styledArtifactItem, document, "version", "2.0.13" );

        Element unstyledArtifactItem = document.createElement( "artifactItem" );
        addChildNode( unstyledArtifactItem, document, "groupId", "com.liferay" );
        addChildNode( unstyledArtifactItem, document, "artifactId", "com.liferay.frontend.theme.unstyled" );
        addChildNode( unstyledArtifactItem, document, "version", "2.0.13" );

        artifactItems.appendChild( commonArtifactItem );
        artifactItems.appendChild( styledArtifactItem );
        artifactItems.appendChild( unstyledArtifactItem );

        configuration.appendChild( artifactItems );

        addChildNode( configuration, document, "outputDirectory", "${project.build.directory}/deps" );
        addChildNode( configuration, document, "stripVersion", "true" );

        mavenDependencyPlguin.appendChild( configuration );

        pluginsNode.appendChild( mavenDependencyPlguin );

        formatNode( mavenDependencyPlguin );
    }

    private void addMavenThemePlugins( Node pluginsNode )
    {
        addMavenDependencyPlugin( pluginsNode );
        addMavenWarPlugin( pluginsNode );
        addCssBuilderThemePlugin( pluginsNode );
        addThemeBuilderPlugin( pluginsNode );
    }

    private void addMavenWarPlugin( Node pluginsNode )
    {
        Document document = pluginsNode.getOwnerDocument();

        Element mavenWarPlguin = document.createElement( "plugin" );
        addChildNode( mavenWarPlguin, document, "artifactId", "maven-war-plugin" );
        addChildNode( mavenWarPlguin, document, "version", "3.0.0" );

        Element configuration = document.createElement( "configuration" );
        addChildNode( configuration, document, "packagingExcludes", "**/*.scss" );

        Element webResources = document.createElement( "webResources" );
        Element resource = document.createElement( "resource" );
        addChildNode( resource, document, "directory", "${com.liferay.portal.tools.theme.builder.outputDir}" );

        Element excludes = document.createElement( "excludes" );
        addChildNode( excludes, document, "exclude", "**/*.scss" );

        resource.appendChild( excludes );
        webResources.appendChild( resource );
        configuration.appendChild( webResources );
        mavenWarPlguin.appendChild( configuration );
        pluginsNode.appendChild( mavenWarPlguin );

        formatNode( mavenWarPlguin );
    }

    private void addProperties( Node propertiesNode )
    {
        Document document = propertiesNode.getOwnerDocument();
        addChildNode( (Element) propertiesNode, document, "com.liferay.portal.tools.theme.builder.outputDir",
            "target/build-theme" );
        addChildNode( (Element) propertiesNode, document, "project.build.sourceEncoding", "UTF-8" );
        formatNode( propertiesNode );
    }

    private void addServiceBuilderPlugin( Node pluginsNode, String projectName )
    {
        Document document = pluginsNode.getOwnerDocument();

        Element serviceBuidlerPlugin = document.createElement( "plugin" );

        addChildNode( serviceBuidlerPlugin, document, "groupId", "com.liferay" );
        addChildNode( serviceBuidlerPlugin, document, "artifactId", "com.liferay.portal.tools.service.builder" );
        addChildNode( serviceBuidlerPlugin, document, "version", "1.0.142" );

        Element configuration = document.createElement( "configuration" );

        addChildNode( configuration, document, "apiDirName", "../" + projectName + "-service/src/main/java" );
        addChildNode( configuration, document, "autoNamespaceTables", "true" );
        addChildNode( configuration, document, "buildNumberIncrement", "true" );
        addChildNode( configuration, document, "hbmFileName", "src/main/resources/META-INF/portlet-hbm.xml" );
        addChildNode( configuration, document, "implDirName", "src/main/java" );
        addChildNode( configuration, document, "inputFileName", "src/main/webapp/WEB-INF/service.xml" );
        addChildNode( configuration, document, "modelHintsFileName",
            "src/main/resources/META-INF/portlet-model-hints.xml" );
        addChildNode( configuration, document, "osgiModule", "false" );
        addChildNode( configuration, document, "pluginName", projectName );
        addChildNode( configuration, document, "propsUtil", "com.liferay.util.service.ServiceProps" );
        addChildNode( configuration, document, "resourcesDirName", "src/main/resources" );
        addChildNode( configuration, document, "springNamespaces", "beans" );
        addChildNode( configuration, document, "springFileName", "src/main/resources/META-INF/portlet-spring.xml" );
        addChildNode( configuration, document, "sqlDirName", "src/main/webapp/WEB-INF/sql" );
        addChildNode( configuration, document, "sqlFileName", "tables.sql" );

        serviceBuidlerPlugin.appendChild( configuration );
        pluginsNode.appendChild( serviceBuidlerPlugin );

        formatNode( serviceBuidlerPlugin );
    }

    private void addThemeBuilderPlugin( Node pluginsNode )
    {
        Document document = pluginsNode.getOwnerDocument();

        Element themeBuidlerPlugin = document.createElement( "plugin" );

        addChildNode( themeBuidlerPlugin, document, "groupId", "com.liferay" );
        addChildNode( themeBuidlerPlugin, document, "artifactId", "com.liferay.portal.tools.theme.builder" );
        addChildNode( themeBuidlerPlugin, document, "version", "1.0.1" );

        Element executions = document.createElement( "executions" );
        Element execution = document.createElement( "execution" );

        executions.appendChild( execution );
        themeBuidlerPlugin.appendChild( executions );

        addChildNode( execution, document, "phase", "generate-resources" );

        Element goals = document.createElement( "goals" );
        execution.appendChild( goals );

        addChildNode( goals, document, "goal", "build-theme" );

        Element configuration = document.createElement( "configuration" );

        addChildNode( configuration, document, "diffsDir", "src/main/webapp/" );
        addChildNode( configuration, document, "outputDir", "${com.liferay.portal.tools.theme.builder.outputDir}" );
        addChildNode( configuration, document, "parentDir",
            "${project.build.directory}/deps/com.liferay.frontend.theme.styled.jar" );
        addChildNode( configuration, document, "parentName", "_styled" );
        addChildNode( configuration, document, "unstyledDir",
            "${project.build.directory}/deps/com.liferay.frontend.theme.unstyled.jar" );

        execution.appendChild( configuration );

        pluginsNode.appendChild( themeBuidlerPlugin );

        formatNode( themeBuidlerPlugin );
    }

    private void cleanBuildNode( Document document )
    {

        NodeList buildList = document.getElementsByTagName( "build" );

        if( buildList == null || buildList.getLength() == 0 )
        {
            return;
        }
        else if( buildList.getLength() > 1 )
        {
            // TODO throw error
        }
        // only one item
        else
        {
            Element buildNode = (Element) buildList.item( 0 );

            NodeList pluginsList = buildNode.getElementsByTagName( "plugins" );

            if( pluginsList != null && pluginsList.getLength() == 1 )
            {
                Element pluginsNode = (Element) pluginsList.item( 0 );

                NodeList pluginList = pluginsNode.getElementsByTagName( "plugin" );

                if( pluginList == null || pluginList.getLength() == 0 )
                {
                    buildNode.removeChild( pluginsNode );
                }
            }

            NodeList buildChildrenList = buildNode.getChildNodes();

            if( buildChildrenList != null && buildChildrenList.getLength() > 0 )
            {
                boolean deleteBuildNode = true;

                for( int i = 0; i < buildChildrenList.getLength(); i++ )
                {
                    if( buildChildrenList.item( i ).getNodeType() != Node.TEXT_NODE )
                    {
                        deleteBuildNode = false;
                    }
                }

                if( deleteBuildNode )
                {
                    buildNode.getParentNode().removeChild( buildNode );
                }
            }
        }
    }

    private void formatNode( Node node )
    {
        formatProcessor.formatNode( node );
    }

    private Node getDependenciesNode( IDOMDocument document )
    {
        NodeList dependenciesList = document.getElementsByTagName( "dependencies" );

        if( dependenciesList != null && dependenciesList.getLength() == 1 )
        {
            return dependenciesList.item( 0 );
        }
        else
        {
            Element dependenciesNode = document.createElement( "dependencies" );

            document.getElementsByTagName( "project" ).item( 0 ).appendChild( dependenciesNode );

            return dependenciesNode;
        }
    }

    private String[] getFixedArtifactIdAndVersion( String artifactId )
    {
        if( dependenciesConvertMap == null )
        {
            dependenciesConvertMap = new String[5][];

            dependenciesConvertMap[0] = new String[] { "portal-service", "com.liferay.portal.kernel", "2.6.0" };
            dependenciesConvertMap[1] = new String[] { "util-java", "com.liferay.util.java", "2.0.0" };
            dependenciesConvertMap[2] = new String[] { "util-bridges", "com.liferay.util.bridges", "2.0.0" };
            dependenciesConvertMap[3] = new String[] { "util-taglib", "com.liferay.util.taglib", "2.0.0" };
            dependenciesConvertMap[4] = new String[] { "util-slf4j", "com.liferay.util.slf4j", "1.0.0" };
        }

        for( String[] str : dependenciesConvertMap )
        {
            if( artifactId.equals( str[0] ) )
            {
                String[] result = new String[2];
                result[0] = str[1];
                result[1] = str[2];

                return result;
            }
        }

        return null;
    }

    private Node getPluginsNode( IDOMDocument document )
    {
        NodeList buildList = document.getElementsByTagName( "build" );

        if( buildList != null && buildList.getLength() == 1 )
        {
            IDOMElement buildNode = (IDOMElement) buildList.item( 0 );

            NodeList pluginsList = buildNode.getElementsByTagName( "plugins" );

            if( pluginsList == null || pluginsList.getLength() != 1 )
            {
                Element pluginsNode = document.createElement( "plugins" );
                buildNode.appendChild( pluginsNode );

                return pluginsNode;
            }
            else
            {
                return pluginsList.item( 0 );
            }
        }
        else
        {
            Element buildNode = document.createElement( "build" );
            Element pluginsNode = document.createElement( "plugins" );
            buildNode.appendChild( pluginsNode );
            document.getElementsByTagName( "project" ).item( 0 ).appendChild( buildNode );

            return pluginsNode;
        }
    }

    private Node getPropertiesNode( IDOMDocument document )
    {
        NodeList nodeList = document.getElementsByTagName( "properties" );

        if( nodeList != null && nodeList.getLength() > 0 )
        {
            for( int i = 0; i < nodeList.getLength(); i++ )
            {
                Node node = nodeList.item( 0 );

                if( node instanceof Element )
                {
                    Element element = (Element) node;

                    if( element.getParentNode().getNodeName().equals( "project" ) )
                    {
                        return node;
                    }
                }
            }
        }

        Element propertiesListNode = document.createElement( "properties" );
        document.getElementsByTagName( "project" ).item( 0 ).appendChild( propertiesListNode );

        return propertiesListNode;
    }

    @Override
    public boolean isNeedUpgrade( IFile pomFile )
    {
        String tagName = "artifactId";
        String[] values = new String[] { "liferay-maven-plugin", "portal-service", "util-java", "util-bridges",
            "util-taglib", "util-slf4j" };

        IDOMModel domModel = null;

        boolean retval = false;

        try
        {
            System.out.println( "pom---------------------------"+pomFile.exists() );
            
            domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead( pomFile );

            IDOMDocument document = domModel.getDocument();

            NodeList elements = document.getElementsByTagName( tagName );

            if( elements != null )
            {
                for( int i = 0; i < elements.getLength(); i++ )
                {
                    IDOMElement element = (IDOMElement) elements.item( i );

                    String textContent = element.getTextContent();

                    if( !CoreUtil.empty( textContent ) )
                    {
                        textContent = textContent.trim();

                        for( String str : values )
                        {
                            if( textContent.equals( str ) )
                            {
                                retval = true;
                            }
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            if( domModel != null )
            {
                domModel.releaseFromRead();
            }
        }

        return retval;
    }

    @Override
    public boolean isNeedUpgrade( IProject project )
    {
        IFile pomFile = project.getFile( "pom.xml" );

        return isNeedUpgrade( pomFile );
    }

    private boolean isProtletProject( IProject project )
    {
        IFile portletFile = project.getFile( "src/main/webapp/WEB-INF/portlet.xml" );
        IFile liferayPortletFile = project.getFile( "src/main/webapp/WEB-INF/liferay-portlet.xml" );

        if( portletFile.exists() && liferayPortletFile.exists() )
        {
            return true;
        }

        return false;
    }

    private boolean isServiceBuilderProject( IProject project )
    {
        IFile serviceFile = project.getFile( "src/main/webapp/WEB-INF/service.xml" );

        if( serviceFile.exists() )
        {
            return true;
        }

        return false;
    }

    private boolean isServiceBuilderSubProject( IProject project )
    {
        return project.getName().endsWith( "-service" );
    }

    private boolean isThemeProject( IProject project )
    {
        IFile lookAndFeelFile = project.getFile( "src/main/webapp/WEB-INF/liferay-look-and-feel.xml" );

        if( lookAndFeelFile.exists() )
        {
            return true;
        }

        return false;
    }

    private void removeChildren( Node node )
    {
        while( node.hasChildNodes() )
        {
            node.removeChild( node.getFirstChild() );
        }
    }

    @Override
    public void upgradePomFile( IProject project, File outputFile )
    {
        IFile pomFile = project.getFile( "pom.xml" );
        IFile tempPomFile = project.getFile( ".pom-tmp.xml" );

        boolean needUpgrade = isNeedUpgrade( pomFile );

        if( outputFile == null && !needUpgrade )
        {
            return;
        }

        IDOMModel domModel = null;

        try
        {
            if( outputFile != null )
            {
                pomFile.copy( tempPomFile.getFullPath(), true, null );

                pomFile = tempPomFile;
            }

            domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead( pomFile );

            if( needUpgrade )
            {
                IDOMDocument document = domModel.getDocument();

                domModel.aboutToChangeModel();

                NodeList elements = document.getElementsByTagName( "artifactId" );

                if( elements != null )
                {
                    for( int i = 0; i < elements.getLength(); i++ )
                    {
                        IDOMElement element = (IDOMElement) elements.item( i );

                        String textContent = element.getTextContent();

                        if( !CoreUtil.empty( textContent ) )
                        {
                            textContent = textContent.trim();

                            // remove liferay-maven-plugin
                            if( textContent.equals( "liferay-maven-plugin" ) )
                            {
                                IDOMElement liferayMavenPluginNode = (IDOMElement) element.getParentNode();

                                removeChildren( liferayMavenPluginNode );

                                IDOMElement pluginsNode = (IDOMElement) liferayMavenPluginNode.getParentNode();

                                pluginsNode.removeChild( liferayMavenPluginNode );
                            }
                            // fix dependencies
                            else if( textContent.equals( "portal-service" ) || textContent.equals( "util-java" ) ||
                                textContent.equals( "util-bridges" ) || textContent.equals( "util-taglib" ) ||
                                textContent.equals( "util-slf4j" ) )
                            {
                                IDOMElement dependencyElement = (IDOMElement) element.getParentNode();

                                String[] fixArtifactIdandVersion = getFixedArtifactIdAndVersion( textContent );

                                removeChildren( element );
                                Text artifactIdTextContent =
                                    element.getOwnerDocument().createTextNode( fixArtifactIdandVersion[0] );
                                element.appendChild( artifactIdTextContent );

                                NodeList versionList = dependencyElement.getElementsByTagName( "version" );

                                if( versionList != null && versionList.getLength() == 1 )
                                {
                                    IDOMElement versionElement = (IDOMElement) versionList.item( 0 );
                                    removeChildren( versionElement );
                                    Text versionTextContent =
                                        element.getOwnerDocument().createTextNode( fixArtifactIdandVersion[1] );
                                    versionElement.appendChild( versionTextContent );
                                }
                            }

                        }
                    }
                }

                if( isProtletProject( project ) )
                {
                    addCssBuilderPlugin( getPluginsNode( document ) );
                }
                if( isServiceBuilderProject( project ) )
                {
                    addServiceBuilderPlugin( getPluginsNode( document ), project.getName() );
                    addDependency( getDependenciesNode( document ), "biz.aQute.bnd", "biz.aQute.bnd.annotation",
                        "3.2.0", "provided" );
                }
                if( isServiceBuilderSubProject( project ) )
                {
                    addDependency( getDependenciesNode( document ), "biz.aQute.bnd", "biz.aQute.bnd.annotation",
                        "3.2.0", "provided" );
                }
                if( isThemeProject( project ) )
                {
                    addProperties( getPropertiesNode( document ) );
                    addMavenThemePlugins( getPluginsNode( document ) );
                }

                cleanBuildNode( document );

                domModel.changedModel();
            }

            if( tempPomFile.exists() )
            {
                tempPomFile.delete( true, null );
            }

            if( outputFile != null )
            {
                try(FileOutputStream fos = new FileOutputStream( outputFile ))
                {
                    domModel.save( fos );
                }
                catch( Exception e )
                {
                }
            }
            else
            {
                domModel.save();
            }
        }
        catch( Exception e )
        {
            ProjectCore.logError( "update pom file error", e );
        }
        finally
        {
            if( domModel != null )
            {
                domModel.releaseFromRead();
            }
        }
    }

}
