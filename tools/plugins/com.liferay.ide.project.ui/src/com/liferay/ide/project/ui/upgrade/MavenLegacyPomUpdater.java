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
public class MavenLegacyPomUpdater
{

    private static String[][] dependenciesConvertMap;

    private void addBuildServicePlugin( IDOMElement pluginNode, String projectName )
    {
        Document document = pluginNode.getOwnerDocument();

        NodeList packagingNodeList = document.getElementsByTagName( "packaging" );

        if( packagingNodeList != null && packagingNodeList.getLength() == 1 )
        {
            Node node = packagingNodeList.item( 0 );

            String content = node.getTextContent();

            if( !CoreUtil.empty( content ) )
            {
                content = content.trim();

                if( content.equals( "war" ) )
                {
                    addChildNode( pluginNode, document, "groupId", "com.liferay" );
                    addChildNode( pluginNode, document, "artifactId", "com.liferay.portal.tools.service.builder" );
                    addChildNode( pluginNode, document, "version", "1.0.142" );

                    Element configuration = document.createElement( "configuration" );

                    addChildNode( configuration, document, "apiDirName",
                        "../" + projectName + "-service/src/main/java" );
                    addChildNode( configuration, document, "autoNamespaceTables", "true" );
                    addChildNode( configuration, document, "buildNumberIncrement", "true" );
                    addChildNode( configuration, document, "hbmFileName",
                        "src/main/resources/META-INF/portlet-hbm.xml" );
                    addChildNode( configuration, document, "implDirName", "src/main/java" );
                    addChildNode( configuration, document, "inputFileName", "src/main/webapp/WEB-INF/service.xml" );
                    addChildNode( configuration, document, "modelHintsFileName",
                        "src/main/resources/META-INF/portlet-model-hints.xml" );
                    addChildNode( configuration, document, "osgiModule", "false" );
                    addChildNode( configuration, document, "pluginName", projectName );
                    addChildNode( configuration, document, "propsUtil", "com.liferay.util.service.ServiceProps" );
                    addChildNode( configuration, document, "resourcesDirName", "src/main/resources" );
                    addChildNode( configuration, document, "springNamespaces", "beans" );
                    addChildNode( configuration, document, "springFileName",
                        "src/main/resources/META-INF/portlet-spring.xml" );
                    addChildNode( configuration, document, "sqlDirName", "src/main/webapp/WEB-INF/sql" );
                    addChildNode( configuration, document, "sqlFileName", "tables.sql" );

                    pluginNode.appendChild( configuration );

                    IStructuredFormatProcessor formatProcessor = new FormatProcessorXML();
                    formatProcessor.formatNode( pluginNode );
                }
                else
                {
                    // delete empty build and plugins tag
                    IDOMElement pluginsNode = (IDOMElement) pluginNode.getParentNode();

                    pluginsNode.removeChild( pluginNode );

                    NodeList pluginList = pluginsNode.getElementsByTagName( "plugin" );

                    if( pluginList != null && pluginList.getLength() == 0 )
                    {
                        Node buildNode = pluginsNode.getParentNode();

                        buildNode.removeChild( pluginsNode );

                        NodeList buildList = buildNode.getChildNodes();

                        if( buildList != null )
                        {
                            boolean deleteBuildNode = true;

                            for( int i = 0; i < buildList.getLength(); i++ )
                            {
                                if( buildList.item( i ).getNodeType() != Node.TEXT_NODE )
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
            }
        }
    }

    private void addChildNode( Element pluginNode, Document document, String tagName, String value )
    {
        Element testNode = document.createElement( tagName );

        testNode.appendChild( document.createTextNode( value ) );
        pluginNode.appendChild( testNode );
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

    public boolean isNeedUpgrade( IFile pomFile )
    {
        String tagName = "artifactId";
        String[] values = new String[] { "liferay-maven-plugin", "portal-service", "util-java", "util-bridges",
            "util-taglib", "util-slf4j" };

        IDOMModel domModel = null;

        boolean retval = false;

        try
        {
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

    private void removeChildren( Node node )
    {
        while( node.hasChildNodes() )
        {
            node.removeChild( node.getFirstChild() );
        }
    }

    public void upgradePomFile( IProject project, File outputFile )
    {
        IFile pomFile = project.getFile( "pom.xml" );

        IDOMModel domModel = null;

        try
        {
            domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead( pomFile );

            IDOMDocument document = domModel.getDocument();

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

                        domModel.aboutToChangeModel();

                        if( textContent.equals( "liferay-maven-plugin" ) )
                        {
                            IDOMElement pluginNode = (IDOMElement) element.getParentNode();

                            removeChildren( pluginNode );

                            String projectName = project.getName();

                            addBuildServicePlugin( pluginNode, projectName );

                        }
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

                        domModel.changedModel();
                    }
                }
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
