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

package com.liferay.ide.project.ui.upgrade.animated;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.SWTUtil;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 */
public class DescriptorsPage extends AbstractLiferayTableViewCustomPart
{
    private final static String[][] DESCRIPTORS_AND_IMAGES = 
    { 
        { "liferay-portlet.xml", "/icons/e16/portlet.png" },
        { "liferay-display.xml", "/icons/e16/liferay_display_xml.png" }, 
        { "service.xml", "/icons/e16/service.png" },
        { "liferay-hook.xml", "/icons/e16/hook.png" },
        { "liferay-layout-templates.xml", "/icons/e16/layout.png" },
        { "liferay-look-and-feel.xml", "/icons/e16/theme.png" }, 
        { "liferay-portlet-ext.xml", "/icons/e16/ext.png" } 
    };

    private final static String PUBLICID_REGREX =
        "-\\//(?:[a-z][a-z]+)\\//(?:[a-z][a-z]+)[\\s+(?:[a-z][a-z0-9_]*)]*\\s+(\\d\\.\\d\\.\\d)\\//(?:[a-z][a-z]+)";

    private final static String SYSTEMID_REGREX =
        "^http://www.liferay.com/dtd/[-A-Za-z0-9+&@#/%?=~_()]*(\\d_\\d_\\d).dtd";

    public DescriptorsPage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel, DESCRIPTORS_PAGE_ID, true );
    }

    public void createSpecialDescriptor( Composite parent, int style )
    {
        final String descriptor = "This step upgrades descriptor XML DTD versions from 6.2 to 7.0 and " +
            "deletes the wap-template-path \ntag from liferay-layout-template.xml files.\n" +
            "Double click the file in the list. It will popup a comparison page which shows the differences\n" +
            "between your original source file and the upgrade preview file.";
        String url = "";

        Link link = SWTUtil.createHyperLink( this, style, descriptor, 1, url );
        link.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false, 2, 1 ) );
    }

    @Override
    public String getPageTitle()
    {
        return "Upgrade Descriptor Files";
    }

    @Override
    public int getGridLayoutCount()
    {
        return 2;
    }

    @Override
    protected void createTempFile( File srcFile, File templateFile, String projectName )
    {
        SAXBuilder builder = new SAXBuilder( false );
        builder.setValidation( false );
        builder.setFeature( "http://xml.org/sax/features/validation", false );
        builder.setFeature( "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false );
        builder.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false ); 

        try( InputStream ivyInput = Files.newInputStream( srcFile.toPath() ) )
        {
            Document doc = builder.build( ivyInput );
            DocType docType = doc.getDocType();

            if( docType != null )
            {
                final String publicId = docType.getPublicID();
                final String newPublicId = getNewDoctTypeSetting( publicId, "7.0.0", PUBLICID_REGREX );
                docType.setPublicID( newPublicId );

                final String systemId = docType.getSystemID();
                final String newSystemId = getNewDoctTypeSetting( systemId, "7_0_0", SYSTEMID_REGREX );
                docType.setSystemID( newSystemId );
            }

            removeLayoutWapNode( srcFile, doc );

            if( templateFile.exists() )
            {
                templateFile.delete();
            }

            templateFile.createNewFile();

            saveXML( templateFile, doc );
        }
        catch( JDOMException | IOException e )
        {
            ProjectCore.logError( e );
        }
    }

    @Override
    protected void doUpgrade( File srcFile, IProject project )
    {
        SAXBuilder builder = new SAXBuilder( false );
        builder.setValidation( false );
        builder.setFeature( "http://xml.org/sax/features/validation", false );
        builder.setFeature( "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false );
        builder.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false );

        try( InputStream ivyInput = Files.newInputStream( srcFile.toPath() ) )
        {

            Document doc = builder.build( ivyInput );
            DocType docType = doc.getDocType();

            if( docType != null )
            {
                final String publicId = docType.getPublicID();
                final String newPublicId = getNewDoctTypeSetting( publicId, "7.0.0", PUBLICID_REGREX );
                docType.setPublicID( newPublicId );

                final String systemId = docType.getSystemID();
                final String newSystemId = getNewDoctTypeSetting( systemId, "7_0_0", SYSTEMID_REGREX );
                docType.setSystemID( newSystemId );
            }

            removeLayoutWapNode( srcFile, doc );

            saveXML( srcFile, doc );
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }
    }

    @Override
    protected IFile[] getAvaiableUpgradeFiles( IProject project )
    {
        List<IFile> files = new ArrayList<IFile>();

        for( String[] descriptors : DESCRIPTORS_AND_IMAGES )
        {
            final String searchName = descriptors[0];

            List<IFile> searchFiles = new SearchFilesVisitor().searchFiles( project, searchName );
            files.addAll( searchFiles );
        }

        return files.toArray( new IFile[files.size()] );
    }

    @Override
    protected IStyledLabelProvider getLableProvider()
    {
        return new LiferayUpgradeTabeViewLabelProvider( "Upgrade Descriptors")
        {

            @Override
            public Image getImage( Object element )
            {
                if( element instanceof LiferayUpgradeElement )
                {
                    final String itemName = ( (LiferayUpgradeElement) element ).itemName;

                    return this.getImageRegistry().get( itemName );
                }

                return null;
            }

            @Override
            protected void initalizeImageRegistry( ImageRegistry imageRegistry )
            {
                for( String[] descriptorsAndImages : DESCRIPTORS_AND_IMAGES )
                {
                    final String descName = descriptorsAndImages[0];
                    final String descImage = descriptorsAndImages[1];

                    imageRegistry.put(
                        descName, ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, descImage ) );
                }
            }
        };
    }

    private String getNewDoctTypeSetting( String doctypeSetting, String newValue, String regrex )
    {
        String newDoctTypeSetting = null;
        Pattern p = Pattern.compile( regrex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
        Matcher m = p.matcher( doctypeSetting );

        if( m.find() )
        {
            String oldVersionString = m.group( m.groupCount() );
            newDoctTypeSetting = doctypeSetting.replace( oldVersionString, newValue );
        }

        return newDoctTypeSetting;
    }

    private String getOldVersion( final String sourceDTDVersion, final String regrex )
    {

        if( sourceDTDVersion == null )
        {
            return null;
        }

        Pattern p = Pattern.compile( regrex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
        Matcher m = p.matcher( sourceDTDVersion );

        if( m.find() )
        {
            String oldVersionString = m.group( m.groupCount() );
            return oldVersionString;
        }

        return null;
    }

    @Override
    protected boolean isNeedUpgrade( File srcFile )
    {
        SAXBuilder builder = new SAXBuilder( false );
        builder.setValidation( false );
        builder.setFeature( "http://xml.org/sax/features/validation", false );
        builder.setFeature( "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false );
        builder.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false );


        try( InputStream ivyInput = Files.newInputStream( srcFile.toPath() ) )
        {
            Document doc = builder.build( ivyInput );
            DocType docType = doc.getDocType();

            if( docType != null )
            {
                final String publicId = docType.getPublicID();
                String oldPublicIdVersion = getOldVersion( publicId, PUBLICID_REGREX );

                final String systemId = docType.getSystemID();
                String oldSystemIdVersion = getOldVersion( systemId, SYSTEMID_REGREX );

                if( ( publicId != null && !oldPublicIdVersion.equals( "7.0.0" ) ) ||
                    ( systemId != null && !oldSystemIdVersion.equals( "7_0_0" ) ) )
                {
                    return true;
                }
            }
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }
        return false;
    }

    @SuppressWarnings( "unchecked" )
    private void removeLayoutWapNode( File srcFile, Document doc )
    {
        if( srcFile.getName().equals( "liferay-layout-templates.xml" ) )
        {
            Element itemRem = null;
            Element elementRoot = doc.getRootElement();
            List<Element> customers = elementRoot.getChildren( "custom" );
            for( Iterator<Element> customerIterator = customers.iterator(); customerIterator.hasNext(); )
            {
                Element customerElement = (Element) customerIterator.next();
                List<Element> layoutElements = customerElement.getChildren( "layout-template" );
                for( Iterator<Element> layoutIterator = layoutElements.iterator(); layoutIterator.hasNext(); )
                {

                    Element layoutElement = (Element) layoutIterator.next();
                    List<Element> wapElements = layoutElement.getChildren( "wap-template-path" );
                    for( Iterator<Element> wapIterator = wapElements.iterator(); wapIterator.hasNext(); )
                    {
                        Element removeItem = (Element) wapIterator.next();
                        wapIterator.remove();
                        itemRem = removeItem;
                    }
                }
            }
            elementRoot.removeContent( itemRem );
        }
    }

    private void saveXML( File templateFile, Document doc )
    {
        XMLOutputter out = new XMLOutputter();

        try(OutputStream fos = Files.newOutputStream( templateFile.toPath() );)
        {
            Format fm = Format.getPrettyFormat();
            out.setFormat( fm );
            out.output( doc, fos );
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }
    }
    
}
