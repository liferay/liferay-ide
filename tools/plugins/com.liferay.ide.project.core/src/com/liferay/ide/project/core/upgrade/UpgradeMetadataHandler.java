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
package com.liferay.ide.project.core.upgrade;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.AbstractUpgradeProjectHandler;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class UpgradeMetadataHandler extends AbstractUpgradeProjectHandler
{
    private final static String publicid_regrex =
                    "-\\//(?:[a-z][a-z]+)\\//(?:[a-z][a-z]+)[\\s+(?:[a-z][a-z0-9_]*)]*\\s+(\\d\\.\\d\\.\\d)\\//(?:[a-z][a-z]+)";

    private final static String systemid_regrex =
        "^http://www.liferay.com/dtd/[-A-Za-z0-9+&@#/%?=~_()]*(\\d_\\d_\\d).dtd";

    private final static String[] fileNames = { "liferay-portlet.xml", "liferay-display.xml", "service.xml",
        "liferay-hook.xml", "liferay-layout-templates.xml", "liferay-look-and-feel.xml", "liferay-portlet-ext.xml",
        "liferay-plugin-package.properties" };

    @Override
    public Status execute( IProject project, String runtimeName, IProgressMonitor monitor, int perUnit )
    {
        Status retval = Status.createOkStatus();

        try
        {
            int worked = 0;
            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "Prograde Upgrade Update DTD Header" );

            final IFile[] metaFiles = getUpgradeDTDFiles( project );

            for( IFile file : metaFiles )
            {
                final IStructuredModel editModel = StructuredModelManager.getModelManager().getModelForEdit( file );

                try
                {
                    if( editModel != null && editModel instanceof IDOMModel )
                    {
                        worked = worked + perUnit;
                        submon.worked( worked );

                        final IDOMDocument xmlDocument = ( (IDOMModel) editModel ).getDocument();
                        final DocumentTypeImpl docType = (DocumentTypeImpl) xmlDocument.getDoctype();

                        final String publicId = docType.getPublicId();
                        final String newPublicId = getNewDoctTypeSetting( publicId, "6.2.0", publicid_regrex );

                        if( newPublicId != null )
                        {
                            docType.setPublicId( newPublicId );
                        }

                        worked = worked + perUnit;
                        submon.worked( worked );

                        final String systemId = docType.getSystemId();
                        final String newSystemId = getNewDoctTypeSetting( systemId, "6_2_0", systemid_regrex );

                        if( newSystemId != null )
                        {
                            docType.setSystemId( newSystemId );
                        }

                        editModel.save();

                        worked = worked + perUnit;
                        submon.worked( worked );
                    }
                    else
                    {
                        updateProperties( file, "liferay-versions", "6.2.0+" );
                    }
                }
                finally
                {
                    editModel.releaseFromEdit();
                }
            }
        }
        catch( Exception e )
        {
            final IStatus error =
                ProjectCore.createErrorStatus(
                    "Unable to upgrade deployment meta file for " + project.getName(), e );
            ProjectCore.logError( error );

            retval = StatusBridge.create( error );
        }

        return retval;
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

    private IFile[] getUpgradeDTDFiles( IProject project )
    {
        List<IFile> files = new ArrayList<IFile>();

        for( String name : fileNames )
        {
            files.addAll( new SearchFilesVisitor().searchFiles( project, name ) );
        }

        return files.toArray( new IFile[files.size()] );
    }

    private void updateProperties( IFile file, String propertyName, String propertiesValue ) throws Exception
    {
        File osfile = new File( file.getLocation().toOSString() );
        PropertiesConfiguration pluginPackageProperties = new PropertiesConfiguration();
        pluginPackageProperties.load( osfile );
        pluginPackageProperties.setProperty( propertyName, propertiesValue );

        FileWriter output = new FileWriter( osfile );

        try
        {
            pluginPackageProperties.save( output );
        }
        finally
        {
            output.close();
        }

        file.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );
    }
}
