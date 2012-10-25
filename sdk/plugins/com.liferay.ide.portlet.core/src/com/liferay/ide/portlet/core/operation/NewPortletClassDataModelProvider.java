/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.portlet.core.operation;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;
import com.liferay.ide.project.core.IPluginWizardFragmentProperties;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jst.j2ee.common.CommonFactory;
import org.eclipse.jst.j2ee.common.ParamValue;
import org.eclipse.jst.j2ee.internal.web.operations.NewWebClassDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.server.core.IRuntime;
import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
@SuppressWarnings( { "restriction", "unchecked", "rawtypes" } )
public class NewPortletClassDataModelProvider extends NewWebClassDataModelProvider
    implements INewPortletClassDataModelProperties, IPluginWizardFragmentProperties
{

    private static final String PORTLET_SUFFIX_PATTERN = "(?<!^)portlet$";

    protected Properties categories;
    protected Properties entryCategories;
    protected TemplateContextType contextType;
    protected boolean fragment;
    protected IProject initialProject;
    protected TemplateStore templateStore;

    public NewPortletClassDataModelProvider()
    {
        super();
    }

    public NewPortletClassDataModelProvider(
        TemplateStore templateStore, TemplateContextType contextType, boolean fragment )
    {
        super();

        this.templateStore = templateStore;
        this.contextType = contextType;
        this.fragment = fragment;
    }

    public NewPortletClassDataModelProvider(
        TemplateStore templateStore, TemplateContextType contextType, boolean fragment, IProject initialProject )
    {
        this( templateStore, contextType, fragment );

        this.initialProject = initialProject;
    }

    protected ParamValue[] createDefaultParamValuesForModes( String[] modes, String[] names, String[] values )
    {
        Assert.isTrue( modes != null && names != null && values != null && ( modes.length == names.length ) &&
            ( names.length == values.length ) );

        List<ParamValue> defaultParams = new ArrayList<ParamValue>();

        // for each path value need to prepend a path that will be specific to
        // the portlet being created
        String prependPath = getDataModel().getStringProperty( CREATE_JSPS_FOLDER );

        for( int i = 0; i < modes.length; i++ )
        {
            if( getBooleanProperty( modes[i] ) )
            {
                ParamValue paramValue = CommonFactory.eINSTANCE.createParamValue();

                paramValue.setName( names[i] );

                if( CoreUtil.isNullOrEmpty( prependPath ) )
                {
                    paramValue.setValue( values[i] );
                }
                else
                {
                    if( CoreUtil.isNullOrEmpty( prependPath ) || ( !prependPath.startsWith( "/" ) ) )
                    {
                        prependPath = "/" + prependPath;
                    }
                    paramValue.setValue( prependPath + values[i] );
                }

                defaultParams.add( paramValue );
            }
        }

        return defaultParams.toArray( new ParamValue[0] );
    }

    protected IRuntime getRuntime() throws CoreException
    {
        IRuntime runtime = null;

        if( this.fragment )
        {
            org.eclipse.wst.common.project.facet.core.runtime.IRuntime bRuntime =
                (org.eclipse.wst.common.project.facet.core.runtime.IRuntime) getDataModel().getProperty( FACET_RUNTIME );
            runtime = ServerUtil.getRuntime( bRuntime );
        }
        else
        {
            runtime = ServerUtil.getRuntime( (IProject) getProperty( PROJECT ) );
        }

        return runtime;
    }

    protected ILiferayRuntime getLiferayRuntime() throws CoreException
    {
        IRuntime runtime = getRuntime();

        ILiferayRuntime portalRuntime =
            (ILiferayRuntime) runtime.createWorkingCopy().loadAdapter( ILiferayRuntime.class, null );

        return portalRuntime;
    }

    protected Properties getCategories()
    {
        if( categories == null )
        {
            IProject project = (IProject) getProperty( PROJECT );

            if( project != null )
            {
                try
                {
                    ILiferayRuntime portalRuntime = getLiferayRuntime();

                    categories = portalRuntime.getPortletCategories();
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                }
            }
        }

        return categories;
    }

    @Override
    public IDataModelOperation getDefaultOperation()
    {
        return new AddPortletOperation( this.model );
    }

    @Override
    public Object getDefaultProperty( String propertyName )
    {
        if( SUPERCLASS.equals( propertyName ) )
        {
            return QUALIFIED_MVC_PORTLET;
        }
        else if( CONSTRUCTOR.equals( propertyName ) )
        {
            return false;
        }
        else if( INIT_OVERRIDE.equals( propertyName ) || DOVIEW_OVERRIDE.equals( propertyName ) )
        {
            return true;
        }
        else if( DESTROY_OVERRIDE.equals( propertyName ) )
        {
            return false;
        }
        else if( JAVA_PACKAGE.equals( propertyName ) )
        {
            return "com.test";
        }
        else if( CLASS_NAME.equals( propertyName ) )
        {
            return "NewPortlet";
        }
        else if( PORTLET_NAME.equals( propertyName ) || LIFERAY_PORTLET_NAME.equals( propertyName ) )
        {
            return getProperty( CLASS_NAME ).toString().toLowerCase().replaceAll( PORTLET_SUFFIX_PATTERN, "" );
        }
        else if( DISPLAY_NAME.equals( propertyName ) || TITLE.equals( propertyName ) ||
            SHORT_TITLE.equals( propertyName ) )
        {
            return getDisplayNameFromClassName( getProperty( CLASS_NAME ).toString() );
        }
        else if( KEYWORDS.equals( propertyName ) )
        {
            return "";
        }
        else if( INIT_PARAMS.equals( propertyName ) )
        {
            return getInitParams();
        }
        else if( VIEW_MODE.equals( propertyName ) )
        {
            return true;
        }
        else if( CREATE_JSPS.equals( propertyName ) )
        {
            return true;
        }
        else if( CREATE_JSPS_FOLDER.equals( propertyName ) )
        {
            if( getBooleanProperty( CREATE_NEW_PORTLET_CLASS ) )
            {
                return "/html/" +
                    getProperty( CLASS_NAME ).toString().toLowerCase().replaceAll( PORTLET_SUFFIX_PATTERN, "" );
            }
            else
            {
                return "/html/" +
                    getProperty( PORTLET_NAME ).toString().toLowerCase().replaceAll( PORTLET_SUFFIX_PATTERN, "" );
            }

        }
        else if( ICON_FILE.equals( propertyName ) )
        {
            return "/icon.png";
        }
        else if( CREATE_RESOURCE_BUNDLE_FILE.equals( propertyName ) )
        {
            return false;
        }
        else if( CREATE_RESOURCE_BUNDLE_FILE_PATH.equals( propertyName ) )
        {
            return "content/Language.properties";
        }
        else if( ALLOW_MULTIPLE.equals( propertyName ) )
        {
            return false;
        }
        else if( CSS_FILE.equals( propertyName ) )
        {
            return "/css/main.css";
        }
        else if( JAVASCRIPT_FILE.equals( propertyName ) )
        {
            return "/js/main.js";
        }
        else if( CSS_CLASS_WRAPPER.equals( propertyName ) )
        {
            return getProperty( PORTLET_NAME ).toString().toLowerCase() + "-portlet";
        }
        else if( ID.equals( propertyName ) )
        {
            return getProperty( PORTLET_NAME );
        }
        else if( CATEGORY.equals( propertyName ) )
        {
            return "category.sample";
        }
        else if( ENTRY_CATEGORY.equals( propertyName ) )
        {
            return "category.my";
        }
        else if( ENTRY_WEIGHT.equals( propertyName ) )
        {
            return "1.5";
        }
        else if( ENTRY_CLASS_NAME.equals( propertyName ) )
        {
            return getStringProperty( CLASS_NAME ) + "ControlPanelEntry";
        }
        else if( SHOW_NEW_CLASS_OPTION.equals( propertyName ) )
        {
            return true;
        }
        else if( CREATE_NEW_PORTLET_CLASS.equals( propertyName ) )
        {
            return true;
        }
        else if( USE_DEFAULT_PORTLET_CLASS.equals( propertyName ) )
        {
            return false;
        }
        else if( QUALIFIED_CLASS_NAME.equals( propertyName ) )
        {
            if( getBooleanProperty( USE_DEFAULT_PORTLET_CLASS ) )
            {
                return QUALIFIED_MVC_PORTLET;
            }
        }
        else if( PROJECT_NAME.equals( propertyName ) && initialProject != null )
        {
            return initialProject.getName();
        }
        else if( INIT_PARAMETER_NAME.equals( propertyName ) )
        {
            String initParameterName = "template";

            try
            {
                ILiferayRuntime portalRuntime = getLiferayRuntime();
                String version = portalRuntime.getPortalVersion();
                Version portalVersion = Version.parseVersion( version );

                if( CoreUtil.compareVersions( portalVersion, new Version( 6, 1, 0 ) ) < 0 )
                {
                    initParameterName = "jsp";
                }
            }
            catch( Exception e )
            {
            }

            return initParameterName;
        }

        return super.getDefaultProperty( propertyName );
    }

    protected Properties getEntryCategories()
    {
        if( entryCategories == null )
        {
            IProject project = (IProject) getProperty( PROJECT );

            if( project != null )
            {
                try
                {
                    ILiferayRuntime portalRuntime = getLiferayRuntime();

                    entryCategories = portalRuntime.getPortletEntryCategories();
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                }
            }
        }

        return entryCategories;
    }

    protected Object getInitParams()
    {
        List<ParamValue> initParams = new ArrayList<ParamValue>();

        // if the user is using MVCPortlet and creating JSPs then we need to
        // define init-params for each view mode that is checked
        if( /* getStringProperty(SUPERCLASS).equals(QUALIFIED_MVC_PORTLET) && */getBooleanProperty( CREATE_JSPS ) )
        {
            String[] modes = ALL_PORTLET_MODES;

            ParamValue[] paramVals = null;

            try
            {
                ILiferayRuntime portalRuntime = getLiferayRuntime();
                String version = portalRuntime.getPortalVersion();
                Version portalVersion = Version.parseVersion( version );

                if( CoreUtil.compareVersions( portalVersion, new Version( 6, 1, 0 ) ) >= 0 )
                {
                    paramVals = createDefaultParamValuesForModes( modes, initNames61, initValues );
                }
            }
            catch( Exception e )
            {
            }

            if( paramVals == null )
            {
                paramVals = createDefaultParamValuesForModes( modes, initNames60, initValues );
            }

            Collections.addAll( initParams, paramVals );
        }

        return initParams;
    }

    protected String getDisplayNameFromClassName( String oldName )
    {
        final String[] words = oldName.split( "(?<!^)(?=[A-Z])" );
        String newName = new String();

        for( int i = 0; i < words.length; i++ )
        {
            if( i > 0 )
            {
                newName = newName.concat( " " );
            }

            newName = newName.concat( words[i] );
        }

        return newName;
    }

    @Override
    public DataModelPropertyDescriptor getPropertyDescriptor( String propertyName )
    {
        if( VIEW_MODE.equals( propertyName ) )
        {
            return new DataModelPropertyDescriptor( getProperty( propertyName ), "view" );
        }
        else if( EDIT_MODE.equals( propertyName ) )
        {
            return new DataModelPropertyDescriptor( getProperty( propertyName ), "edit" );
        }
        else if( HELP_MODE.equals( propertyName ) )
        {
            return new DataModelPropertyDescriptor( getProperty( propertyName ), "help" );
        }
        /**
         * Values for liferay modes taking from LiferayPortletMode.java
         */
        else if( ABOUT_MODE.equals( propertyName ) )
        {
            return new DataModelPropertyDescriptor( getProperty( propertyName ), "about" );
        }
        else if( CONFIG_MODE.equals( propertyName ) )
        {
            return new DataModelPropertyDescriptor( getProperty( propertyName ), "config" );
        }
        else if( EDITDEFAULTS_MODE.equals( propertyName ) )
        {
            return new DataModelPropertyDescriptor( getProperty( propertyName ), "edit_defaults" );
        }
        else if( EDITGUEST_MODE.equals( propertyName ) )
        {
            return new DataModelPropertyDescriptor( getProperty( propertyName ), "edit_guest" );
        }
        else if( PREVIEW_MODE.equals( propertyName ) )
        {
            return new DataModelPropertyDescriptor( getProperty( propertyName ), "preview" );
        }
        else if( PRINT_MODE.equals( propertyName ) )
        {
            return new DataModelPropertyDescriptor( getProperty( propertyName ), "print" );
        }
        else if( CATEGORY.equals( propertyName ) )
        {
            if( getProperty( CATEGORY ).equals( "category.sample" ) )
            {
                return new DataModelPropertyDescriptor( "category.sample", "Sample" );
            }
        }
        else if( ENTRY_CATEGORY.equals( propertyName ) )
        {
            if( getProperty( ENTRY_CATEGORY ).equals( "category.my" ) )
            {
                return new DataModelPropertyDescriptor( "category.my", "My Account Section" );
            }
        }

        return super.getPropertyDescriptor( propertyName );
    }

    @Override
    public Set getPropertyNames()
    {
        Set propertyNames = super.getPropertyNames();
        // propertyNames.add(CREATE_CUSTOM_PORTLET_CLASS);
        // propertyNames.add(PORTLET_CLASS);

        propertyNames.add( TEMPLATE_STORE );
        propertyNames.add( CONTEXT_TYPE );

        propertyNames.add( INIT_OVERRIDE );
        propertyNames.add( DESTROY_OVERRIDE );
        propertyNames.add( DOVIEW_OVERRIDE );
        propertyNames.add( DOEDIT_OVERRIDE );
        propertyNames.add( DOHELP_OVERRIDE );
        propertyNames.add( DOABOUT_OVERRIDE );
        propertyNames.add( DOCONFIG_OVERRIDE );
        propertyNames.add( DOEDITDEFAULTS_OVERRIDE );
        propertyNames.add( DOEDITGUEST_OVERRIDE );
        propertyNames.add( DOPREVIEW_OVERRIDE );
        propertyNames.add( DOPRINT_OVERRIDE );
        propertyNames.add( PROCESSACTION_OVERRIDE );
        propertyNames.add( SERVERESOURCE_OVERRIDE );

        propertyNames.add( PORTLET_NAME );
        propertyNames.add( DISPLAY_NAME );
        propertyNames.add( TITLE );
        propertyNames.add( SHORT_TITLE );
        propertyNames.add( KEYWORDS );
        propertyNames.add( INIT_PARAMETER_NAME );
        propertyNames.add( INIT_PARAMS );

        propertyNames.add( VIEW_MODE );
        propertyNames.add( EDIT_MODE );
        propertyNames.add( HELP_MODE );

        propertyNames.add( ABOUT_MODE );
        propertyNames.add( CONFIG_MODE );
        propertyNames.add( EDITDEFAULTS_MODE );
        propertyNames.add( EDITGUEST_MODE );
        propertyNames.add( PREVIEW_MODE );
        propertyNames.add( PRINT_MODE );

        propertyNames.add( CREATE_RESOURCE_BUNDLE_FILE );
        propertyNames.add( CREATE_RESOURCE_BUNDLE_FILE_PATH );
        propertyNames.add( CREATE_JSPS );
        propertyNames.add( CREATE_JSPS_FOLDER );

        propertyNames.add( LIFERAY_PORTLET_NAME );
        propertyNames.add( ICON_FILE );
        propertyNames.add( ALLOW_MULTIPLE );
        propertyNames.add( CSS_FILE );
        propertyNames.add( JAVASCRIPT_FILE );
        propertyNames.add( CSS_CLASS_WRAPPER );
        propertyNames.add( ID );
        propertyNames.add( CATEGORY );
        propertyNames.add( ADD_TO_CONTROL_PANEL );
        propertyNames.add( ENTRY_CATEGORY );
        propertyNames.add( ENTRY_WEIGHT );
        propertyNames.add( CREATE_ENTRY_CLASS );
        propertyNames.add( ENTRY_CLASS_NAME );

        propertyNames.add( FACET_RUNTIME );
        propertyNames.add( REMOVE_EXISTING_ARTIFACTS );

        propertyNames.add( SHOW_NEW_CLASS_OPTION );
        propertyNames.add( CREATE_NEW_PORTLET_CLASS );
        propertyNames.add( USE_DEFAULT_PORTLET_CLASS );

        return propertyNames;
    }

    @Override
    public DataModelPropertyDescriptor[] getValidPropertyDescriptors( String propertyName )
    {
        if( SUPERCLASS.equals( propertyName ) )
        {
            String[] vals =
                new String[] { QUALIFIED_MVC_PORTLET, QUALIFIED_LIFERAY_PORTLET, QUALIFIED_GENERIC_PORTLET };

            return DataModelPropertyDescriptor.createDescriptors( vals, vals );
        }
        else if( CATEGORY.equals( propertyName ) )
        {
            Properties categories = getCategories();

            if( categories != null && categories.size() > 0 )
            {
                return DataModelPropertyDescriptor.createDescriptors(
                    categories.keySet().toArray( new Object[0] ), categories.values().toArray( new String[0] ) );
            }
        }
        else if( ENTRY_CATEGORY.equals( propertyName ) )
        {
            Properties entryCategories = getEntryCategories();

            if( entryCategories != null && entryCategories.size() > 0 )
            {
                Object[] keys = entryCategories.keySet().toArray();
                Arrays.sort( keys );

                String[] values = new String[keys.length];

                for( int i = 0; i < keys.length; i++ )
                {
                    values[i] = entryCategories.getProperty( keys[i].toString() );
                }

                return DataModelPropertyDescriptor.createDescriptors( keys, values );
            }
        }

        return super.getValidPropertyDescriptors( propertyName );
    }

    protected IFile getWorkspaceFile( IPath file )
    {
        IFile retval = null;

        try
        {
            retval = ResourcesPlugin.getWorkspace().getRoot().getFile( file );
        }
        catch( Exception e )
        {
            // best effort
        }

        return retval;
    }

    @Override
    public void init()
    {
        super.init();

        this.setProperty( TEMPLATE_STORE, this.templateStore );
        this.setProperty( CONTEXT_TYPE, this.contextType );
    }

    @Override
    public boolean isPropertyEnabled( String propertyName )
    {
        if( VIEW_MODE.equals( propertyName ) )
        {
            return false;
        }
        else if( INIT_OVERRIDE.equals( propertyName ) )
        {
            return false;
        }
        else if( ABOUT_MODE.equals( propertyName ) || CONFIG_MODE.equals( propertyName ) ||
            EDITDEFAULTS_MODE.equals( propertyName ) || EDITGUEST_MODE.equals( propertyName ) ||
            PREVIEW_MODE.equals( propertyName ) || PRINT_MODE.equals( propertyName ) )
        {

            if( this.fragment )
            {
                return true;
            }

            return PortletSupertypesValidator.isLiferayPortletSuperclass( getDataModel(), true );
        }
        else if( CLASS_NAME.equals( propertyName ) || JAVA_PACKAGE.equals( propertyName ) ||
            SUPERCLASS.equals( propertyName ) )
        {

            return getBooleanProperty( CREATE_NEW_PORTLET_CLASS );
        }

        return super.isPropertyEnabled( propertyName );
    }

    @Override
    public boolean propertySet( String propertyName, Object propertyValue )
    {
        if( VIEW_MODE.equals( propertyName ) )
        {
            setProperty( DOVIEW_OVERRIDE, propertyValue );
        }
        else if( EDIT_MODE.equals( propertyName ) )
        {
            setProperty( DOEDIT_OVERRIDE, propertyValue );
        }
        else if( HELP_MODE.equals( propertyName ) )
        {
            setProperty( DOHELP_OVERRIDE, propertyValue );
        }
        else if( ABOUT_MODE.equals( propertyName ) &&
            PortletSupertypesValidator.isLiferayPortletSuperclass( getDataModel() ) )
        {

            setProperty( DOABOUT_OVERRIDE, propertyValue );
        }
        else if( CONFIG_MODE.equals( propertyName ) )
        {
            setProperty( DOCONFIG_OVERRIDE, propertyValue );
        }
        else if( EDITDEFAULTS_MODE.equals( propertyName ) )
        {
            setProperty( DOEDITDEFAULTS_OVERRIDE, propertyValue );
        }
        else if( EDITGUEST_MODE.equals( propertyName ) )
        {
            setProperty( DOEDITGUEST_OVERRIDE, propertyValue );
        }
        else if( PREVIEW_MODE.equals( propertyName ) )
        {
            setProperty( DOPREVIEW_OVERRIDE, propertyValue );
        }
        else if( PRINT_MODE.equals( propertyName ) )
        {
            setProperty( DOPRINT_OVERRIDE, propertyValue );
        }
        else if( SUPERCLASS.equals( propertyName ) )
        {
            getDataModel().notifyPropertyChange( VIEW_MODE, IDataModel.ENABLE_CHG );
            getDataModel().notifyPropertyChange( EDIT_MODE, IDataModel.ENABLE_CHG );
            getDataModel().notifyPropertyChange( HELP_MODE, IDataModel.ENABLE_CHG );
            getDataModel().notifyPropertyChange( ABOUT_MODE, IDataModel.ENABLE_CHG );
            getDataModel().notifyPropertyChange( CONFIG_MODE, IDataModel.ENABLE_CHG );
            getDataModel().notifyPropertyChange( EDITDEFAULTS_MODE, IDataModel.ENABLE_CHG );
            getDataModel().notifyPropertyChange( EDITGUEST_MODE, IDataModel.ENABLE_CHG );
            getDataModel().notifyPropertyChange( PREVIEW_MODE, IDataModel.ENABLE_CHG );
            getDataModel().notifyPropertyChange( PRINT_MODE, IDataModel.ENABLE_CHG );
        }
        else if( PORTLET_NAME.equals( propertyName ) )
        {
            String portletName = getStringProperty( propertyName );

            getDataModel().setStringProperty( LIFERAY_PORTLET_NAME, portletName );
        }
        else if( LIFERAY_PORTLET_NAME.equals( propertyName ) )
        {
            String liferayPortletName = getStringProperty( propertyName );

            getDataModel().setStringProperty( PORTLET_NAME, liferayPortletName );
        }

        return super.propertySet( propertyName, propertyValue );
    }

    @Override
    public IStatus validate( String propertyName )
    {
        if( PORTLET_NAME.equals( propertyName ) )
        {
            // must have a valid portlet name
            String portletName = getStringProperty( PORTLET_NAME );

            if( portletName == null || portletName.length() == 0 )
            {
                return PortletCore.createErrorStatus( "Portlet name is empty." );
            }

            PortletDescriptorHelper portletDescriptorHelper = new PortletDescriptorHelper( getTargetProject() );
            String[] portletNames = portletDescriptorHelper.getAllPortletNames();
            for(String name: portletNames)
            {
                if(name.equals( portletName ))
                {
                    return PortletCore.createErrorStatus( "Portlet name already exists." );
                }
            }
        }
        else if( CREATE_RESOURCE_BUNDLE_FILE_PATH.equals( propertyName ) )
        {
            if( !getBooleanProperty( CREATE_RESOURCE_BUNDLE_FILE ) )
            {
                return Status.OK_STATUS;
            }

            boolean validPath = false;
            boolean validFileName = false;

            String val = getStringProperty( propertyName );

            if( CoreUtil.isNullOrEmpty( val ) )
            {
                return PortletCore.createErrorStatus( "Resource bundle file path must be a valid path." );
            }

            try
            {
                IPath path = new Path( val );
                validPath = path.isValidPath( val );

                if( "properties".equals( path.getFileExtension() ) )
                {
                    validFileName = true;
                }
            }
            catch( Exception e )
            {
                // eat errors
            }

            if( !validPath )
            {
                return PortletCore.createErrorStatus( "Resource bundle file path must be a valid path." );
            }

            if( validFileName )
            {
                return super.validate( propertyName );
            }
            else
            {
                return PortletCore.createWarningStatus( "Resource bundle file path should end with .properties" );
            }
        }
        else if( CREATE_JSPS_FOLDER.equals( propertyName ) )
        {
            if( !getBooleanProperty( CREATE_JSPS ) )
            {
                return Status.OK_STATUS;
            }

            String folderValue = getStringProperty( propertyName );

            if( CoreUtil.isNullOrEmpty( folderValue ) )
            {
                return PortletCore.createErrorStatus( "JSP folder cannot be empty." );
            }

            IProject targetProject = getTargetProject();

            if( ( !CoreUtil.isNullOrEmpty( folderValue ) ) && targetProject != null )
            {
                IFolder defaultDocroot = CoreUtil.getDefaultDocrootFolder( targetProject );
                String errorMsg = FileUtil.validateNewFolder( defaultDocroot, folderValue );

                if( errorMsg != null )
                {
                    return PortletCore.createErrorStatus( errorMsg );
                }

                // make sure path first segment isn't the same as the portlet name
                String path = new Path( folderValue ).segment( 0 );
                if( !CoreUtil.isNullOrEmpty( path ) && path.equals( getStringProperty( PORTLET_NAME ) ) )
                {
                    return PortletCore.createErrorStatus( "JSP folder cannot match portlet name" );
                }
            }
        }
        else if( ( SUPERCLASS.equals( propertyName ) || SOURCE_FOLDER.equals( propertyName ) ) && this.fragment )
        {
            if( SUPERCLASS.equals( propertyName ) )
            {
                String superclass = getStringProperty( propertyName );

                if( CoreUtil.isNullOrEmpty( superclass ) )
                {
                    return PortletCore.createErrorStatus( "Must specify a portlet superclass." );
                }
            }

            return Status.OK_STATUS;
        }
        else if( SUPERCLASS.equals( propertyName ) && !this.fragment )
        {
            String superclass = getStringProperty( propertyName );
            if( CoreUtil.isNullOrEmpty( superclass ) )
            {
                return PortletCore.createErrorStatus( "Must specify a portlet superclass." );
            }
        }
        else if( ENTRY_WEIGHT.equals( propertyName ) )
        {
            String entryweight = getStringProperty( propertyName );

            if( !CoreUtil.isNumeric( entryweight ) )
            {
                return PortletCore.createErrorStatus( "Must specify a valid double for entry weight." );
            }

            return Status.OK_STATUS;
        }
        else if( ENTRY_CLASS_NAME.equals( propertyName ) )
        {
            String entryclasswrapper = getStringProperty( propertyName );

            if( validateJavaClassName( entryclasswrapper ).getSeverity() != IStatus.ERROR )
            {
                IStatus existsStatus = canCreateTypeInClasspath( entryclasswrapper );

                if( existsStatus.matches( IStatus.ERROR | IStatus.WARNING ) )
                {
                    return existsStatus;
                }
            }

            return validateJavaClassName( entryclasswrapper );
        }

        return super.validate( propertyName );
    }

}
