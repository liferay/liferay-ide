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

package com.liferay.ide.service.core.operation;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.service.core.AddServiceBuilderOperation;
import com.liferay.ide.service.core.ServiceCore;
import com.liferay.ide.service.core.util.ServiceUtil;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jem.workbench.utility.JemProjectUtilities;
import org.eclipse.jst.j2ee.internal.common.J2EECommonMessages;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.internal.operation.ArtifactEditOperationDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
@SuppressWarnings( { "restriction", "unchecked" } )
public class NewServiceBuilderDataModelProvider extends ArtifactEditOperationDataModelProvider
    implements INewServiceBuilderDataModelProperties
{

    public NewServiceBuilderDataModelProvider()
    {
        super();
    }

    @Override
    public IDataModelOperation getDefaultOperation()
    {
        return new AddServiceBuilderOperation( getDataModel() );
    }

    @Override
    public Object getDefaultProperty( String propertyName )
    {
        if( SERVICE_FILE.equals( propertyName ) )
        {
            return ILiferayConstants.SERVICE_XML_FILE;
        }
        else if( AUTHOR.equals( propertyName ) )
        {
            return System.getProperty( "user.name" ); //$NON-NLS-1$
        }
        else if( JAVA_PACKAGE_FRAGMENT_ROOT.equals( propertyName ) )
        {
            return getJavaPackageFragmentRoot();
        }
        else if( propertyName.equals( JAVA_SOURCE_FOLDER ) )
        {
            return getJavaSourceFolder();
        }
        else if( propertyName.equals( SOURCE_FOLDER ) )
        {
            IFolder sourceFolder = getDefaultJavaSourceFolder();

            if( sourceFolder != null && sourceFolder.exists() )
            {
                return sourceFolder.getFullPath().toOSString();
            }
        }
        else if( INewJavaClassDataModelProperties.OPEN_IN_EDITOR.equals( propertyName ) )
        {
            return true;
        }
        else if( propertyName.equals( USE_SAMPLE_TEMPLATE ) )
        {
            return true;
        }

        // if (CREATE_CUSTOM_JSPS.equals(propertyName)) {
        // return true;
        // } else if (CUSTOM_JSPS_FOLDER.equals(propertyName)) {
        // HookDescriptorHelper hookDescriptorHelper = new
        // HookDescriptorHelper(getTargetProject());
        // String customJspFolder =
        // hookDescriptorHelper.getCustomJSPFolder(getDataModel());
        // if (customJspFolder != null) {
        // return
        // CoreUtil.getDocroot(getTargetProject()).getFolder(customJspFolder).getFullPath().toPortableString();
        // }
        // return
        // CoreUtil.getDocroot(getTargetProject()).getFullPath().append("custom_jsps").toPortableString();
        // } else if (PORTAL_PROPERTIES_FILE.equals(propertyName)) {
        // return
        // PortletUtil.getFirstSrcFolder(getTargetProject()).getFullPath().append("portal.properties").toPortableString();
        // } else if (CONTENT_FOLDER.equals(propertyName)) {
        // return
        // PortletUtil.getFirstSrcFolder(getTargetProject()).getFullPath().append("content").toPortableString();
        // } else if (propertyName.equals(PROJECT)) {
        // return getTargetProject();
        // } else if (propertyName.equals(SOURCE_FOLDER)) {
        // IFolder sourceFolder = getDefaultJavaSourceFolder();
        // if (sourceFolder != null && sourceFolder.exists())
        // return sourceFolder.getFullPath().toOSString();
        // } else if (propertyName.equals(JAVA_SOURCE_FOLDER)) {
        // return getJavaSourceFolder();
        // } else if (propertyName.equals(JAVA_PACKAGE_FRAGMENT_ROOT)) {
        // return getJavaPackageFragmentRoot();
        // } else if (CUSTOM_JSPS_FILES_CREATED.equals(propertyName)) {
        // return new HashSet<IFile>();
        // } else if (LANGUAGE_PROPERTIES_FILES_CREATED.equals(propertyName)) {
        // return new HashSet<IFile>();
        // }

        return super.getDefaultProperty( propertyName );
    }

    @SuppressWarnings( "rawtypes" )
    @Override
    public Set getPropertyNames()
    {
        Set propertyNames = super.getPropertyNames();
        propertyNames.add( SERVICE_FILE );
        propertyNames.add( JAVA_PACKAGE_FRAGMENT_ROOT );
        propertyNames.add( JAVA_SOURCE_FOLDER );
        propertyNames.add( SOURCE_FOLDER );
        propertyNames.add( PACKAGE_PATH );
        propertyNames.add( NAMESPACE );
        propertyNames.add( AUTHOR );
        propertyNames.add( CREATED_SERVICE_FILE );
        propertyNames.add( INewJavaClassDataModelProperties.OPEN_IN_EDITOR );
        propertyNames.add( USE_SAMPLE_TEMPLATE );
        // propertyNames.add(CREATE_CUSTOM_JSPS);
        // propertyNames.add(CREATE_PORTAL_PROPERTIES);
        // propertyNames.add(CREATE_SERVICES);
        // propertyNames.add(CREATE_LANGUAGE_PROPERTIES);
        // propertyNames.add(CUSTOM_JSPS_FOLDER);
        // propertyNames.add(CUSTOM_JSPS_ITEMS);
        // propertyNames.add(CUSTOM_JSPS_FILES_CREATED);
        // propertyNames.add(PORTAL_PROPERTIES_FILE);
        // propertyNames.add(PORTAL_PROPERTIES_ACTION_ITEMS);
        // propertyNames.add(PORTAL_PROPERTIES_OVERRIDE_ITEMS);
        // propertyNames.add(SERVICES_ITEMS);
        // propertyNames.add(CONTENT_FOLDER);
        // propertyNames.add(LANGUAGE_PROPERTIES_ITEMS);
        // propertyNames.add(LANGUAGE_PROPERTIES_FILES_CREATED);
        // propertyNames.add(SOURCE_FOLDER);
        // propertyNames.add(JAVA_SOURCE_FOLDER);
        // propertyNames.add(JAVA_PACKAGE_FRAGMENT_ROOT);

        return propertyNames;
    }

    @Override
    public void init()
    {
        super.init();
    }

    @Override
    public boolean isPropertyEnabled( String propertyName )
    {
        if( SERVICE_FILE.equals( propertyName ) )
        {
            return false;
        }

        return super.isPropertyEnabled( propertyName );
    }

    @Override
    public IStatus validate( String propertyName )
    {
        if( SERVICE_FILE.equals( propertyName ) )
        {
            IFile serviceFile = getServiceFile();

            if( serviceFile != null && serviceFile.exists() )
            {
                return ServiceCore.createErrorStatus( Msgs.projectContainsServiceXmlFile );
            }
        }
        else if( PACKAGE_PATH.equals( propertyName ) )
        {
            String packagePath = getStringProperty( propertyName );

            if( empty( packagePath ) )
            {
                return ServiceCore.createErrorStatus( Msgs.packagePathNotEmpty );
            }

            if( !empty( packagePath ) )
            {
                return validateJavaPackage( packagePath );
            }
        }
        else if( NAMESPACE.equals( propertyName ) )
        {
            String namespace = getStringProperty( propertyName );

            if( empty( namespace ) )
            {
                return ServiceCore.createErrorStatus( Msgs.namespaceNotEmpty );
            }

            if( ! ServiceUtil.isValidNamespace( namespace ) )
            {
                return ServiceCore.createErrorStatus( Msgs.namespaceInvalid );
            }
        }

        // if (CUSTOM_JSPS_FOLDER.equals(propertyName) &&
        // getBooleanProperty(CREATE_CUSTOM_JSPS)) {
        // String jspFolder = getStringProperty(CUSTOM_JSPS_FOLDER);
        // if (jspFolder == null || jspFolder.isEmpty()) {
        // return
        // ServiceCore.createErrorStatus("Custom JSPs folder not configured.");
        // }
        // } else if (CUSTOM_JSPS_ITEMS.equals(propertyName) &&
        // getBooleanProperty(CREATE_CUSTOM_JSPS)) {
        // Object jspItems = getProperty(CUSTOM_JSPS_ITEMS);
        // if (jspItems instanceof List) {
        // List jsps = (List)jspItems;
        // if (jsps.size() > 0) {
        // return Status.OK_STATUS;
        // }
        // }
        // return
        // ServiceCore.createErrorStatus("Need to specify at least one JSP to override.");
        // } else if (PORTAL_PROPERTIES_FILE.equals(propertyName) &&
        // getBooleanProperty(CREATE_PORTAL_PROPERTIES)) {
        // String portalPropertiesFile =
        // getStringProperty(PORTAL_PROPERTIES_FILE);
        // if (CoreUtil.isNullOrEmpty(portalPropertiesFile)) {
        // return
        // ServiceCore.createErrorStatus("portal.properties file not configured.");
        // }
        // } else if (PORTAL_PROPERTIES_ACTION_ITEMS.equals(propertyName) &&
        // getBooleanProperty(CREATE_PORTAL_PROPERTIES)) {
        // IStatus actionItemsStatus =
        // validateListItems(PORTAL_PROPERTIES_ACTION_ITEMS);
        // IStatus propertyOverridesStatus =
        // validateListItems(PORTAL_PROPERTIES_OVERRIDE_ITEMS);
        // if (actionItemsStatus.isOK() || propertyOverridesStatus.isOK()) {
        // return Status.OK_STATUS;
        // } else {
        // return
        // ServiceCore.createErrorStatus("Need to specify at least one Event Action or Property to override.");
        // }
        // } else if (SERVICES_ITEMS.equals(propertyName) &&
        // getBooleanProperty(CREATE_SERVICES)) {
        // IStatus itemsStatus = validateListItems(SERVICES_ITEMS);
        // if (itemsStatus.isOK()) {
        // return Status.OK_STATUS;
        // } else {
        // return
        // ServiceCore.createErrorStatus("Need to specify at least one Service to override.");
        // }
        // } else if (CONTENT_FOLDER.equals(propertyName) &&
        // getBooleanProperty(CREATE_LANGUAGE_PROPERTIES)) {
        // String contentFolder = getStringProperty(CONTENT_FOLDER);
        // if (contentFolder == null || contentFolder.isEmpty()) {
        // return
        // ServiceCore.createErrorStatus("Content folder not configured.");
        // }
        // } else if (LANGUAGE_PROPERTIES_ITEMS.equals(propertyName) &&
        // getBooleanProperty(CREATE_LANGUAGE_PROPERTIES)) {
        // Object propertiesItems = getProperty(LANGUAGE_PROPERTIES_ITEMS);
        // if (propertiesItems instanceof List) {
        // List jsps = (List)propertiesItems;
        // if (jsps.size() > 0) {
        // return Status.OK_STATUS;
        // }
        // }
        // return
        // ServiceCore.createErrorStatus("Need to specify at least one language property file.");
        // }

        return super.validate( propertyName );
    }

    private IStatus validateJavaPackage( String packName )
    {
        if( packName != null && packName.trim().length() > 0 )
        {
            // Use standard java conventions to validate the package name
            IStatus javaStatus =
                JavaConventions.validatePackageName( packName, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7 );

            if( javaStatus.getSeverity() == IStatus.ERROR )
            {
                String msg = J2EECommonMessages.ERR_JAVA_PACAKGE_NAME_INVALID + javaStatus.getMessage();

                return WTPCommonPlugin.createErrorStatus( msg );
            }
            else if( javaStatus.getSeverity() == IStatus.WARNING )
            {
                String msg = J2EECommonMessages.ERR_JAVA_PACKAGE_NAME_WARNING + javaStatus.getMessage();

                return WTPCommonPlugin.createWarningStatus( msg );
            }
        }

        // java package name is valid
        return Status.OK_STATUS;
    }

    /**
     * Subclasses may extend this method to perform their own retrieval mechanism. This implementation simply returns
     * the JDT package fragment root for the selected source folder. This method may return null.
     *
     * @see IJavaProject#getPackageFragmentRoot(org.eclipse.core.resources.IResource)
     * @return IPackageFragmentRoot
     */
    // protected IPackageFragmentRoot getJavaPackageFragmentRoot() {
    // IProject project = getTargetProject();
    // if (project != null) {
    // IJavaProject aJavaProject = JemProjectUtilities.getJavaProject(project);
    // // Return the source folder for the java project of the selected project
    // if (aJavaProject != null) {
    // IFolder sourcefolder = (IFolder) getProperty(JAVA_SOURCE_FOLDER);
    // if (sourcefolder != null)
    // return aJavaProject.getPackageFragmentRoot(sourcefolder);
    // }
    // }
    // return null;
    // }

    @SuppressWarnings( "deprecation" )
    protected IFolder getDefaultJavaSourceFolder()
    {
        IProject project = getTargetProject();

        if( project == null )
        {
            return null;
        }

        IPackageFragmentRoot[] sources = J2EEProjectUtilities.getSourceContainers( project );

        // Try and return the first source folder
        if( sources.length > 0 )
        {
            try
            {
                return (IFolder) sources[0].getCorrespondingResource();
            }
            catch( Exception e )
            {
                return null;
            }
        }

        return null;
    }

    protected IPackageFragmentRoot getJavaPackageFragmentRoot()
    {
        IProject project = getTargetProject();

        if( project != null )
        {
            IJavaProject aJavaProject = JemProjectUtilities.getJavaProject( project );

            // Return the source folder for the java project of the selected
            // project
            if( aJavaProject != null )
            {
                IFolder sourcefolder = (IFolder) getProperty( JAVA_SOURCE_FOLDER );

                if( sourcefolder != null )
                {
                    return aJavaProject.getPackageFragmentRoot( sourcefolder );
                }
            }
        }

        return null;
    }

    @SuppressWarnings( "deprecation" )
    protected final IFolder getJavaSourceFolder()
    {
        IPackageFragmentRoot[] sources = J2EEProjectUtilities.getSourceContainers( getTargetProject() );

        // Ensure there is valid source folder(s)
        if( sources == null || sources.length == 0 )
        {
            return null;
        }

        String folderFullPath = getStringProperty( SOURCE_FOLDER );

        // Get the source folder whose path matches the source folder name value
        // in the data model
        for( int i = 0; i < sources.length; i++ )
        {
            if( sources[i].getPath().equals( new Path( folderFullPath ) ) )
            {
                try
                {
                    return (IFolder) sources[i].getCorrespondingResource();
                }
                catch( Exception e )
                {
                    break;
                }
            }
        }

        return null;
    }

    protected IFile getServiceFile()
    {
        String serviceFileProperty = getStringProperty( SERVICE_FILE );
        final IWebProject webproject = LiferayCore.create( IWebProject.class, getTargetProject() );

        if( CoreUtil.isNullOrEmpty( serviceFileProperty ) || webproject == null )
        {
            return null;
        }

        // IDE-110 IDE-648
        final IResource serviceXmlResource =
            webproject.findDocrootResource( new Path( "WEB-INF/" + serviceFileProperty ) );

        if( serviceXmlResource != null && serviceXmlResource.exists() && serviceXmlResource instanceof IFile )
        {
            return (IFile) serviceXmlResource;
        }

        return null;
    }

    protected IWorkspaceRoot getWorkspaceRoot()
    {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    @SuppressWarnings( "rawtypes" )
    protected IStatus validateListItems( String propertyName )
    {
        Object items = getProperty( propertyName );

        if( items instanceof List )
        {
            List itemsList = (List) items;

            if( itemsList.size() > 0 )
            {
                return Status.OK_STATUS;
            }
        }

        return ServiceCore.createErrorStatus( Msgs.specifyOneItem );
    }

    private static class Msgs extends NLS
    {
        public static String namespaceInvalid;
        public static String namespaceNotEmpty;
        public static String packagePathNotEmpty;
        public static String projectContainsServiceXmlFile;
        public static String specifyOneItem;

        static
        {
            initializeMessages( NewServiceBuilderDataModelProvider.class.getName(), Msgs.class );
        }
    }
}
