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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.action.NewPluginProjectDropDownAction;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Cindy Li
 * @author Kuo Zhang
 */
public class ValidProjectChecker
{

    private static final String ATT_ID = "id"; //$NON-NLS-1$

    private static final String ATT_NAME = "name"; //$NON-NLS-1$

    private static final String ATT_VALID_PROJECT_TYPES = "validProjectTypes"; //$NON-NLS-1$

    private static final String TAG_NEW_WIZARDS = "newWizards"; //$NON-NLS-1$

    private static final String TAG_PARAMETER = "parameter"; //$NON-NLS-1$

    private static final String TAG_VALUE = "value"; //$NON-NLS-1$

    private static final String TAG_WIZARD = "wizard"; //$NON-NLS-1$

    protected boolean isJsfPortlet = false;
    protected String validProjectTypes = null;
    protected String wizardId = null;
    protected String wizardName = null;

    public ValidProjectChecker( String wizardId )
    {
        this.wizardId = wizardId;
        init();
    }

    public void checkValidProjectTypes()
    {
        IProject[] projects = CoreUtil.getAllProjects();
        boolean hasValidProjectTypes = false;

        boolean hasJsfFacet = false;

        for( IProject project : projects )
        {
            if( ProjectUtil.isLiferayFacetedProject( project ) )
            {
                Set<IProjectFacetVersion> facets = ProjectUtil.getFacetedProject( project ).getProjectFacets();

                if( validProjectTypes != null && facets != null )
                {
                    String[] validTypes = validProjectTypes.split( StringPool.COMMA );

                    for( String validProjectType : validTypes )
                    {
                        for( IProjectFacetVersion facet : facets )
                        {
                            String id = facet.getProjectFacet().getId();

                            if( isJsfPortlet && id.equals( "jst.jsf" ) ) //$NON-NLS-1$
                            {
                                hasJsfFacet = true;
                            }

                            if( id.startsWith( "liferay." ) && id.equals( "liferay." + validProjectType ) ) //$NON-NLS-1$ //$NON-NLS-2$
                            {
                                hasValidProjectTypes = true;
                            }
                        }
                    }
                }
            }
        }

        if( isJsfPortlet )
        {
            hasValidProjectTypes = hasJsfFacet && hasValidProjectTypes;
        }

        if( ! hasValidProjectTypes )
        {
            final Shell activeShell = Display.getDefault().getActiveShell();

            Boolean openNewLiferayProjectWizard = 
                MessageDialog.openQuestion( activeShell, NLS.bind( Msgs.newElement, wizardName ),
                    NLS.bind( Msgs.noSuitableLiferayProjects, wizardName ) );

            if( openNewLiferayProjectWizard )
            {
                final Action defaultAction = NewPluginProjectDropDownAction.getPluginProjectAction();

                if( defaultAction != null )
                {
                    defaultAction.run();

                    this.checkValidProjectTypes();
                }
            }
        }
    }

    private String getValidProjectTypesFromConfig( IConfigurationElement config )
    {
        IConfigurationElement[] classElements = config.getChildren();

        if( classElements.length > 0 )
        {
            for( IConfigurationElement classElement : classElements )
            {
                IConfigurationElement[] paramElements = classElement.getChildren( TAG_PARAMETER );

                for( IConfigurationElement paramElement : paramElements )
                {
                    if( ATT_VALID_PROJECT_TYPES.equals( paramElement.getAttribute( ATT_NAME ) ) )
                    {
                        return paramElement.getAttribute( TAG_VALUE );
                    }
                }
            }
        }

        return null;
    }

    protected void init()
    {
        if( wizardId != null && wizardId.equals( "com.liferay.ide.eclipse.portlet.jsf.ui.wizard.portlet" ) ) //$NON-NLS-1$
        {
            setJsfPortlet( true );
        }

        IExtensionPoint extensionPoint =
            Platform.getExtensionRegistry().getExtensionPoint( PlatformUI.PLUGIN_ID, TAG_NEW_WIZARDS );

        if( extensionPoint != null )
        {
            IConfigurationElement[] elements = extensionPoint.getConfigurationElements();

            for( IConfigurationElement element : elements )
            {
                if( element.getName().equals( TAG_WIZARD ) && element.getAttribute( ATT_ID ).equals( wizardId ) )
                {
                    // getValidProjectTypesFromConfig( element )!=null && isLiferayArtifactWizard(element,
                    // "liferay_artifact")
                    setValidProjectTypes( getValidProjectTypesFromConfig( element ) );
                    wizardName = element.getAttribute( ATT_NAME );
                    break;
                }
            }
        }
    }

    public void setJsfPortlet( boolean isJsfPortlet )
    {
        this.isJsfPortlet = isJsfPortlet;
    }

    public void setValidProjectTypes( String validProjectTypes )
    {
        this.validProjectTypes = validProjectTypes;
    }

    private static class Msgs extends NLS
    {
        public static String newElement;
        public static String noSuitableLiferayProjects;

        static
        {
            initializeMessages( ValidProjectChecker.class.getName(), Msgs.class );
        }
    }
}
