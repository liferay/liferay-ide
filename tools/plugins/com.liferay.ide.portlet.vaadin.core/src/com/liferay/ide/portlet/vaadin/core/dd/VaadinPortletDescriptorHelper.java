/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.portlet.vaadin.core.dd;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.IPluginPackageModel;
import com.liferay.ide.portlet.core.PluginPropertiesConfiguration;
import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;
import com.liferay.ide.portlet.vaadin.core.VaadinCore;
import com.liferay.ide.portlet.vaadin.core.operation.INewVaadinPortletClassDataModelProperties;

import java.io.File;
import java.io.FileWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * Helper for editing various portlet configuration XML files, to add Vaadin portlet configuration to them. Also
 * supports adding a dependency to Vaadin in liferay-plugin-package.properties (if necessary).
 * 
 * @author Henri Sara
 */
public class VaadinPortletDescriptorHelper extends PortletDescriptorHelper
    implements INewVaadinPortletClassDataModelProperties
{

    public VaadinPortletDescriptorHelper( IProject project )
    {
        super( project );
    }

    @Override
    public IStatus addNewPortlet( IDataModel model )
    {
        IStatus status = super.addNewPortlet( model );
        if( !status.isOK() )
        {
            return status;
        }

        return addPortalDependency( IPluginPackageModel.PROPERTY_PORTAL_DEPENDENCY_JARS, "vaadin.jar" ); //$NON-NLS-1$
    }

    public IStatus addPortalDependency( String propertyName, String value )
    {
        if( CoreUtil.isNullOrEmpty( value ) )
        {
            return Status.OK_STATUS;
        }

        PluginPropertiesConfiguration pluginPackageProperties;

        try
        {
            IVirtualComponent comp = ComponentCore.createComponent( this.project.getProject() );

            if( comp == null )
            {
                IStatus warning = VaadinCore.createWarningStatus( "Could not add Vaadin dependency to the project." ); //$NON-NLS-1$
                VaadinCore.getDefault().getLog().log( warning );
                return Status.OK_STATUS;
            }

            IFolder webroot = (IFolder) comp.getRootFolder().getUnderlyingFolder();
            IFile pluginPackageFile =
                webroot.getFile( "WEB-INF/" + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE ); //$NON-NLS-1$

            if( !pluginPackageFile.exists() )
            {
                IStatus warning =
                    VaadinCore.createWarningStatus( "No " + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE + //$NON-NLS-1$
                        " file in the project, not adding Vaadin dependency." ); //$NON-NLS-1$

                VaadinCore.getDefault().getLog().log( warning );

                return Status.OK_STATUS;
            }

            File osfile = new File( pluginPackageFile.getLocation().toOSString() );

            pluginPackageProperties = new PluginPropertiesConfiguration();
            pluginPackageProperties.load( osfile );

            String existingDeps = pluginPackageProperties.getString( propertyName, StringPool.EMPTY );

            String[] existingValues = existingDeps.split( "," ); //$NON-NLS-1$

            for( String existingValue : existingValues )
            {
                if( value.equals( existingValue ) )
                {
                    return Status.OK_STATUS;
                }
            }

            String newPortalDeps = null;

            if( CoreUtil.isNullOrEmpty( existingDeps ) )
            {
                newPortalDeps = value;
            }
            else
            {
                newPortalDeps = existingDeps + "," + value; //$NON-NLS-1$
            }

            pluginPackageProperties.setProperty( propertyName, newPortalDeps );

            FileWriter output = new FileWriter( osfile );
            try
            {
                pluginPackageProperties.save( output );
            }
            finally
            {
                output.close();
            }

            // refresh file
            pluginPackageFile.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );

        }
        catch( Exception e )
        {
            VaadinCore.logError( e );
            return VaadinCore.createErrorStatus( "Could not add dependency to vaadin.jar in liferay-plugin-package.properties ." ); //$NON-NLS-1$
        }

        return Status.OK_STATUS;
    }

    @Override
    protected String getPortletClassText( IDataModel model )
    {
        return model.getStringProperty( VAADIN_PORTLET_CLASS );
    }

}
