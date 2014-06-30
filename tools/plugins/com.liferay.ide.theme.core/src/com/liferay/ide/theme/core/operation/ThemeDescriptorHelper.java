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

package com.liferay.ide.theme.core.operation;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;
import com.liferay.ide.theme.core.ThemeCore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class ThemeDescriptorHelper extends LiferayDescriptorHelper
{
    private static final String DESCRIPTOR_FILE = ILiferayConstants.LIFERAY_LOOK_AND_FEEL_XML_FILE;

    public static final String DEFUALT_FILE_TEMPLATE =
        "<?xml version=\"1.0\"?>\n<!DOCTYPE look-and-feel PUBLIC \"-//Liferay//DTD Look and Feel {0}//EN\" " + //$NON-NLS-1$
        "\"http://www.liferay.com/dtd/liferay-look-and-feel_{1}.dtd\">\n\n<look-and-feel>\n\t<compatibility>\n\t\t" + //$NON-NLS-1$
        "<version>__VERSION__</version>\n\t</compatibility>\n\t<theme id=\"__ID__\" name=\"__NAME__\" />\n</look-and-feel>"; //$NON-NLS-1$

    public ThemeDescriptorHelper( IProject project )
    {
        super( project );
    }

    @Override
    protected void addDescriptorOperations()
    {
        // currently, no descriptor operations for this descriptor
    }

    public void createDefaultFile( IContainer container, String version, String id, String name, String themeType )
    {
        if( container == null || id == null || name == null )
        {
            return;
        }

        try
        {
            final Path path = new Path( ILiferayConstants.LIFERAY_LOOK_AND_FEEL_XML_FILE );
            final IFile lookAndFeelFile = container.getFile( path );
            final String descriptorVersion = getDescriptorVersionFromPortalVersion( version );

            CoreUtil.prepareFolder( (IFolder) lookAndFeelFile.getParent() );

            String contents =
                MessageFormat.format( DEFUALT_FILE_TEMPLATE, descriptorVersion, descriptorVersion.replace( '.', '_' ) );
            contents =
                contents.replaceAll( "__VERSION__", version + "+" ).replaceAll( "__ID__", id ).replaceAll( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    "__NAME__", name ); //$NON-NLS-1$

            lookAndFeelFile.create( new ByteArrayInputStream( contents.getBytes() ), true, null );

            if( ! themeType.equals( "vm" ) ) //$NON-NLS-1$
            {
                setTemplateExtension( lookAndFeelFile, themeType );
            }

            FormatProcessorXML processor = new FormatProcessorXML();

            try
            {
                processor.formatFile( lookAndFeelFile );
            }
            catch( IOException e )
            {
            }
        }
        catch( CoreException e )
        {
            ThemeCore.logError( "Error creating default descriptor file", e ); //$NON-NLS-1$
        }
    }

    @Override
    public IFile getDescriptorFile()
    {
        return super.getDescriptorFile( DESCRIPTOR_FILE );
    }

    protected void setTemplateExtension( IFile lookAndFeelFile, final String extension )
    {
        new DOMModelEditOperation( lookAndFeelFile )
        {
            protected void createDefaultFile()
            {
            }

            protected IStatus doExecute( IDOMDocument document )
            {
                try
                {
                    NodeList themeElements = document.getElementsByTagName( "theme" ); //$NON-NLS-1$

                    Element themeElement = (Element) themeElements.item( 0 );

                    NodeUtil.appendChildElement( themeElement, "template-extension", extension ); //$NON-NLS-1$
                }
                catch( Exception e )
                {
                    return ThemeCore.createErrorStatus( e );
                }

                return Status.OK_STATUS;
            }
        }.execute();
    }

}
