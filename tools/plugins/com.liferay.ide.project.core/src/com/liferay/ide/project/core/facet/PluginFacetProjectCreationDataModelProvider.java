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

package com.liferay.ide.project.core.facet;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.IPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.ISDKTemplate;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jst.j2ee.internal.web.archive.operations.WebFacetProjectCreationDataModelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;
import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( { "unchecked", "restriction", "rawtypes" } )
public class PluginFacetProjectCreationDataModelProvider extends WebFacetProjectCreationDataModelProvider
    implements IPluginProjectDataModelProperties
{
    protected Map<String, ISDKTemplate> sdkTemplates = new HashMap<String, ISDKTemplate>();

    public PluginFacetProjectCreationDataModelProvider()
    {
        super();

        final ISDKTemplate[] templates = LiferayProjectCore.getSDKTemplates();

        for( ISDKTemplate tmpl : templates )
        {
            if( tmpl.getFacetId() != null )
            {
                sdkTemplates.put( tmpl.getFacetId(), tmpl );
            }
        }
    }

    @Override
    public Object getDefaultProperty( String propertyName )
    {
        if( LIFERAY_SDK_NAME.equals( propertyName ) )
        {
            SDK sdk = SDKManager.getInstance().getDefaultSDK();

            if( sdk != null )
            {
                return sdk.getName();
            }
            else
            {
                return IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE;
            }
        }
        else if( LIFERAY_USE_SDK_LOCATION.equals( propertyName ) )
        {
            return true;
        }
        else if( USE_DEFAULT_LOCATION.equals( propertyName ) )
        {
            return false;
        }
        else if( LIFERAY_USE_CUSTOM_LOCATION.equals( propertyName ) )
        {
            return false;
        }
        else if( PLUGIN_TYPE_PORTLET.equals( propertyName ) )
        {
            return true;
        }
        else if( PORTLET_NAME.equals( propertyName ) )
        {
            return getProperty( PROJECT_NAME );
        }
        else if( DISPLAY_NAME.equals( propertyName ) )
        {
            return ProjectUtil.convertToDisplayName( getStringProperty( PROJECT_NAME ) );
        }
        else if( HOOK_NAME.equals( propertyName ) )
        {
            return getProperty( PROJECT_NAME );
        }
        else if( EXT_NAME.equals( propertyName ) )
        {
            return getProperty( PROJECT_NAME );
        }
        else if( THEME_NAME.equals( propertyName ) )
        {
            return getProperty( PROJECT_NAME );
        }
        else if( LAYOUTTPL_NAME.equals( propertyName ) )
        {
            return getProperty( PROJECT_NAME );
        }
        else if( CREATE_PROJECT_OPERATION.equals( propertyName ) )
        {
            return true;
        }
        else if( PORTLET_FRAMEWORK_ID.equals( propertyName ) )
        {
            return LiferayProjectCore.getPortletFrameworks()[0].getId();
        }

        for( IPortletFrameworkWizardProvider portletFramework : LiferayProjectCore.getPortletFrameworks() )
        {
            if( portletFramework.hasPropertyName( propertyName ) )
            {
                return portletFramework.getDefaultProperty( propertyName );
            }
        }

        return super.getDefaultProperty( propertyName );
    }

    protected IFacetedProjectWorkingCopy getFacetedProjectWorkingCopy()
    {
        return (IFacetedProjectWorkingCopy) this.model.getProperty( FACETED_PROJECT_WORKING_COPY );
    }

    protected IDataModel getModel()
    {
        return this.model;
    }

    protected IDataModel getNestedModel()
    {
        return getDataModel().getNestedModel( NESTED_PROJECT_DM );
    }

    protected String getProjectLocation()
    {
        IPath sdkLoc = getSDKLocation();

        if( sdkLoc == null )
        {
            return null;
        }

        if( getBooleanProperty( PLUGIN_TYPE_PORTLET ) )
        {
            return sdkLoc.append( ISDKConstants.PORTLET_PLUGIN_PROJECT_FOLDER ).toOSString();
        }
        else if( getBooleanProperty( PLUGIN_TYPE_HOOK ) )
        {
            return sdkLoc.append( ISDKConstants.HOOK_PLUGIN_PROJECT_FOLDER ).toOSString();
        }
        else if( getBooleanProperty( PLUGIN_TYPE_EXT ) )
        {
            return sdkLoc.append( ISDKConstants.EXT_PLUGIN_PROJECT_FOLDER ).toOSString();
        }
        else if( getBooleanProperty( PLUGIN_TYPE_THEME ) )
        {
            return sdkLoc.append( ISDKConstants.THEME_PLUGIN_PROJECT_FOLDER ).toOSString();
        }
        else if( getBooleanProperty( PLUGIN_TYPE_LAYOUTTPL ) )
        {
            return sdkLoc.append( ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_FOLDER ).toOSString();
        }

        return null;
    }

    protected String getProjectSuffix()
    {
        if( getBooleanProperty( PLUGIN_TYPE_PORTLET ) )
        {
            return ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
        }
        else if( getBooleanProperty( PLUGIN_TYPE_HOOK ) )
        {
            return ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX;
        }
        else if( getBooleanProperty( PLUGIN_TYPE_EXT ) )
        {
            return ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX;
        }
        else if( getBooleanProperty( PLUGIN_TYPE_THEME ) )
        {
            return ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX;
        }
        else if( getBooleanProperty( PLUGIN_TYPE_LAYOUTTPL ) )
        {
            return ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;
        }
        return null;
    }

    @Override
    public DataModelPropertyDescriptor getPropertyDescriptor( String propertyName )
    {
        if( LIFERAY_SDK_NAME.equals( propertyName ) )
        {
            Object val = getProperty( propertyName );

            if( IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE.equals( val ) || StringPool.EMPTY.equals( val ) )
            {
                return new DataModelPropertyDescriptor(
                    getProperty( propertyName ), IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE_DESCRIPTION );
            }
            else
            {
                return new DataModelPropertyDescriptor( val, val.toString() );
            }
        }

        return super.getPropertyDescriptor( propertyName );
    }

    @Override
    public Set getPropertyNames()
    {
        Set propNames = super.getPropertyNames();

        propNames.add( LIFERAY_SDK_NAME );
        propNames.add( LIFERAY_USE_SDK_LOCATION );
        propNames.add( LIFERAY_USE_CUSTOM_LOCATION );
        propNames.add( PLUGIN_TYPE_PORTLET );
        propNames.add( PLUGIN_TYPE_HOOK );
        propNames.add( PLUGIN_TYPE_EXT );
        propNames.add( PLUGIN_TYPE_THEME );
        propNames.add( PLUGIN_TYPE_LAYOUTTPL );
        propNames.add( DISPLAY_NAME );
        propNames.add( PORTLET_NAME );
        propNames.add( HOOK_NAME );
        propNames.add( EXT_NAME );
        propNames.add( THEME_NAME );
        propNames.add( LAYOUTTPL_NAME );
        propNames.add( CREATE_PROJECT_OPERATION );
        propNames.add( PLUGIN_FRAGMENT_ENABLED );
        propNames.add( PLUGIN_FRAGMENT_DM );
        propNames.add( PORTLET_FRAMEWORK_ID );

        for( IPortletFrameworkWizardProvider portletFramework : LiferayProjectCore.getPortletFrameworks() )
        {
            propNames.addAll( portletFramework.getPropertyNames() );
        }

        return propNames;
    }

    protected IPath getSDKLocation()
    {
        SDK sdk = SDKManager.getInstance().getSDK( (String) getProperty( LIFERAY_SDK_NAME ) );
        return sdk != null ? sdk.getLocation() : null;
    }

    @Override
    public DataModelPropertyDescriptor[] getValidPropertyDescriptors( String propertyName )
    {
        if( LIFERAY_SDK_NAME.equals( propertyName ) )
        {
            SDK[] validSDKs = SDKManager.getInstance().getSDKs();
            String[] values = null;
            String[] descriptions = null;

            if( validSDKs.length == 0 )
            {
                values = new String[] { IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE };
                descriptions = new String[] { IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE_DESCRIPTION };
            }
            else
            {
                values = new String[validSDKs.length];
                descriptions = new String[validSDKs.length];

                for( int i = 0; i < validSDKs.length; i++ )
                {
                    values[i] = validSDKs[i].getName();
                    descriptions[i] = validSDKs[i].getName();
                }
            }

            return DataModelPropertyDescriptor.createDescriptors( values, descriptions );
        }

        return super.getValidPropertyDescriptors( propertyName );
    }

    @Override
    public void init()
    {
        super.init();

        getDataModel().setProperty( LIFERAY_USE_SDK_LOCATION, true );

        DataModelPropertyDescriptor[] validDescriptors = getDataModel().getValidPropertyDescriptors( FACET_RUNTIME );

        for( DataModelPropertyDescriptor desc : validDescriptors )
        {
            Object runtime = desc.getPropertyValue();

            if( runtime instanceof BridgedRuntime && ServerUtil.isLiferayRuntime( (BridgedRuntime) runtime ) )
            {
                getDataModel().setProperty( FACET_RUNTIME, runtime );
                break;
            }
        }

        LiferayProjectCore.getPortletFrameworks( true );
    }

    @Override
    public boolean propertySet( final String propertyName, final Object propertyValue )
    {
        String portletFrameworkId = getStringProperty( PORTLET_FRAMEWORK_ID );

        IPortletFrameworkWizardProvider portletFramework = LiferayProjectCore.getPortletFramework( portletFrameworkId );

        if( FACET_PROJECT_NAME.equals( propertyName ) || LIFERAY_SDK_NAME.equals( propertyName ) )
        {
            updateProjectLocation();
        }
        else if( PLUGIN_TYPE_PORTLET.equals( propertyName ) && getBooleanProperty( PLUGIN_TYPE_PORTLET ) )
        {
            setupProject( IPluginFacetConstants.LIFERAY_PORTLET_FACET_ID );
        }
        else if( PLUGIN_TYPE_HOOK.equals( propertyName ) && getBooleanProperty( PLUGIN_TYPE_HOOK ) )
        {
            setupProject( IPluginFacetConstants.LIFERAY_HOOK_FACET_ID );
        }
        else if( PLUGIN_TYPE_EXT.equals( propertyName ) && getBooleanProperty( PLUGIN_TYPE_EXT ) )
        {
            setupProject( IPluginFacetConstants.LIFERAY_EXT_FACET_ID );
        }
        else if( PLUGIN_TYPE_THEME.equals( propertyName ) && getBooleanProperty( PLUGIN_TYPE_THEME ) )
        {
            setupProject( IPluginFacetConstants.LIFERAY_THEME_FACET_ID );
        }
        else if( PLUGIN_TYPE_LAYOUTTPL.equals( propertyName ) && getBooleanProperty( PLUGIN_TYPE_LAYOUTTPL ) )
        {
            setupProject( IPluginFacetConstants.LIFERAY_LAYOUTTPL_FACET_ID );
        }
        else if( DISPLAY_NAME.equals( propertyName ) )
        {
            String displayName = ProjectUtil.removePluginSuffix( propertyValue.toString() );

            return super.propertySet( DISPLAY_NAME, displayName );
        }
        else if( PORTLET_FRAMEWORK_ID.equals( propertyName ) && portletFramework != null )
        {
            setupPortletFramework( portletFramework );
        }

        if( portletFramework != null )
        {
            portletFramework.propertySet( propertyName, propertyValue );
        }

        return super.propertySet( propertyName, propertyValue );
    }

    protected void removeFacet( IFacetedProjectWorkingCopy facetedProject, IProjectFacet facet )
    {
        if( facetedProject == null || facet == null )
        {
            return;
        }

        Set<IProjectFacetVersion> newFacetSet = new HashSet<IProjectFacetVersion>();
        Set<IProjectFacetVersion> facets = facetedProject.getProjectFacets();

        for( IProjectFacetVersion fv : facets )
        {
            if( !( fv.getProjectFacet().equals( facet ) ) )
            {
                newFacetSet.add( fv );
            }
        }

        facetedProject.setProjectFacets( newFacetSet );
    }

    protected void setupPortletFramework( IPortletFrameworkWizardProvider portletFramework )
    {
        IPortletFrameworkWizardProvider[] portletFrameworks = LiferayProjectCore.getPortletFrameworks();

        for( IPortletFrameworkWizardProvider framework : portletFrameworks )
        {
            if( !framework.equals( portletFramework ) )
            {
                IProjectFacet[] facets = framework.getFacets();

                for( IProjectFacet facet : facets )
                {
                    removeFacet( getFacetedProjectWorkingCopy(), facet );
                }
            }
        }

        portletFramework.configureNewProject( getDataModel(), getFacetedProjectWorkingCopy() );
    }

    protected void setupProject( String facetId )
    {
        updateProjectLocation();

        final ISDKTemplate sdkTemplate = sdkTemplates.get( facetId );

        if( sdkTemplate != null )
        {
            IFacetedProjectWorkingCopy facetedProject = getFacetedProjectWorkingCopy();

            for( ISDKTemplate tmpl : sdkTemplates.values() )
            {
                removeFacet( facetedProject, tmpl.getFacet() );
            }

            IFacetedProjectTemplate template = sdkTemplate.getFacetedProjectTemplate();

            if( template != null )
            {
                facetedProject.setFixedProjectFacets( Collections.unmodifiableSet( template.getFixedProjectFacets() ) );
            }

            sdkTemplate.setupNewProject( getDataModel(), facetedProject );
        }
    }

    protected void updateProjectLocation()
    {
        String projectLocation = getProjectLocation();

        if( projectLocation == null )
        {
            getNestedModel().setProperty( USE_DEFAULT_LOCATION, true );
        }
        else
        {
            getNestedModel().setProperty( USE_DEFAULT_LOCATION, false );
            getNestedModel().setProperty( USER_DEFINED_BASE_LOCATION, projectLocation );
        }
    }

    @Override
    public IStatus validate( String propertyName )
    {
        String frameworkId = getStringProperty( PORTLET_FRAMEWORK_ID );

        IPortletFrameworkWizardProvider framework = LiferayProjectCore.getPortletFramework( frameworkId );

        if( FACET_PROJECT_NAME.equals( propertyName ) )
        {
            String projectName = getNestedModel().getStringProperty( PROJECT_NAME );

            if( CoreUtil.isNullOrEmpty( projectName ) )
            {
                return super.validate( propertyName );
            }

            String testProjectName = projectName + getProjectSuffix();

            if( ProjectUtil.getProject( testProjectName ).exists() )
            {
                return LiferayProjectCore.createErrorStatus( Msgs.projectExists );
            }

            // before we do a basic java validation we need to strip "-" and "." characters

            String nameValidation = testProjectName.replaceAll( "-", StringPool.EMPTY ).replaceAll( "\\.", StringPool.EMPTY ); //$NON-NLS-1$ //$NON-NLS-2$

            IStatus status =
                JavaConventions.validateIdentifier(
                    nameValidation, CompilerOptions.VERSION_1_5, CompilerOptions.VERSION_1_5 );

            if( !status.isOK() )
            {
                return LiferayProjectCore.createErrorStatus( Msgs.projectNameInvalid );
            }
        }
        else if( LIFERAY_SDK_NAME.equals( propertyName ) )
        {
            Object sdkVal = getModel().getProperty( LIFERAY_SDK_NAME );

            if( sdkVal instanceof String && IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE.equals( sdkVal ) )
            {
                return LiferayProjectCore.createErrorStatus( Msgs.configurePluginSDK );
            }
            else if( !CoreUtil.isNullOrEmpty( sdkVal.toString() ) )
            {
                SDK sdk = SDKManager.getInstance().getSDK( sdkVal.toString() );

                if( sdk == null || !sdk.isValid() )
                {
                    return LiferayProjectCore.createErrorStatus( Msgs.pluginSDKInvalid );
                }
                else
                {
                    return Status.OK_STATUS;
                }
            }
        }
        else if( FACET_RUNTIME.equals( propertyName ) )
        {
            // validate the sdk first
            IStatus status = validate( LIFERAY_SDK_NAME );

            if( !status.isOK() )
            {
                return status;
            }

            Object facetRuntime = getProperty( FACET_RUNTIME );

            if( facetRuntime == null )
            {
                return LiferayProjectCore.createErrorStatus( Msgs.configureLiferayPortalRuntime );
            }
            else if( facetRuntime instanceof BridgedRuntime )
            {
                if( ServerUtil.isLiferayRuntime( (BridgedRuntime) facetRuntime ) )
                {
                    return Status.OK_STATUS;
                }
                else
                {
                    return LiferayProjectCore.createErrorStatus( Msgs.selectLiferayPortalRuntime );
                }
            }
        }
        else if( PLUGIN_TYPE_PORTLET.equals( propertyName ) || PLUGIN_TYPE_HOOK.equals( propertyName ) ||
            PLUGIN_TYPE_EXT.equals( propertyName ) || PLUGIN_TYPE_THEME.equals( propertyName ) ||
            PLUGIN_TYPE_LAYOUTTPL.equals( propertyName ) )
        {

            return validate( FACET_PROJECT_NAME );
        }
        else if( PORTLET_FRAMEWORK_ID.equals( propertyName ) && getBooleanProperty( PLUGIN_TYPE_PORTLET ) &&
            framework != null )
        {
            // check to make sure that the current SDK has the propery version
            String sdkName = getStringProperty( LIFERAY_SDK_NAME );
            SDK selectedSDK = SDKManager.getInstance().getSDK( sdkName );

            if( selectedSDK == null )
            {
                return LiferayProjectCore.createErrorStatus( NLS.bind( Msgs.unableDetermineSDKVersion, sdkName ) );
            }

            Version sdkVersion = new Version( selectedSDK.getVersion() );

            Version requiredSDKVersion = new Version( framework.getRequiredSDKVersion() );

            if( CoreUtil.compareVersions( sdkVersion, requiredSDKVersion ) < 0 )
            {
                return framework.getUnsupportedSDKErrorMsg();
            }
            else
            {
                return Status.OK_STATUS;
            }
        }

        if( framework != null && framework.hasPropertyName( propertyName ) )
        {
            return framework.validate( getDataModel(), propertyName );
        }

        return super.validate( propertyName );
    }

    private static class Msgs extends NLS
    {
        public static String configureLiferayPortalRuntime;
        public static String configurePluginSDK;
        public static String pluginSDKInvalid;
        public static String projectExists;
        public static String projectNameInvalid;
        public static String selectLiferayPortalRuntime;
        public static String unableDetermineSDKVersion;

        static
        {
            initializeMessages( PluginFacetProjectCreationDataModelProvider.class.getName(), Msgs.class );
        }
    }
}
