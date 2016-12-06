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

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.ui.form.FormLayoutFactory;
import com.liferay.ide.ui.form.IDEFormPage;

import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Greg Amerson
 */
public class PluginPackageFormPage extends IDEFormPage
{

    protected ScrolledForm form;

    protected FormToolkit toolkit;

    public PluginPackageFormPage( PluginPackageEditor editor )
    {
        super( editor, "pluginPackage", Msgs.properties ); //$NON-NLS-1$
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

    @Override
    protected void createFormContent( IManagedForm managedForm )
    {
        super.createFormContent( managedForm );

        form = managedForm.getForm();

        FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading( form.getForm() );

        form.setText( Msgs.liferayPluginPackageProperties );
        form.setImage( PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "/icons/e16/plugin.png" ).createImage() ); //$NON-NLS-1$

        toolkit = managedForm.getToolkit();

        Composite body = form.getBody();
        body.setLayout( FormLayoutFactory.createFormGridLayout( true, 2 ) );

        Composite left, right;
        toolkit = managedForm.getToolkit();
        left = toolkit.createComposite( body, SWT.NONE );
        left.setLayout( FormLayoutFactory.createFormPaneGridLayout( false, 1 ) );
        left.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        right = toolkit.createComposite( body, SWT.NONE );
        right.setLayout( FormLayoutFactory.createFormPaneGridLayout( false, 1 ) );
        right.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        PluginPackageGeneralSection generalSection = new PluginPackageGeneralSection( this, left );
        managedForm.addPart( generalSection );

        IProject project = getFormEditor().getCommonProject();

        if( SDKUtil.isSDKProject( project ) )
        {
            PortalJarsSection jarsSection = new PortalJarsSection( this, right, getPortalSectionLabels() );
            managedForm.addPart( jarsSection );

            PortalDeployExcludesSection excludesSection = new PortalDeployExcludesSection( this, right, getPortalSectionLabels() );
            managedForm.addPart( excludesSection );

            PortalTldsSection tldsSection = new PortalTldsSection( this, right, getPortalSectionLabels() );
            managedForm.addPart( tldsSection );

            RequiredDeploymentContextsSection contextsSection =
                new RequiredDeploymentContextsSection( this, right, getContextsSectionLabels() );
            managedForm.addPart( contextsSection );
        }
    }

    private String[] getContextsSectionLabels()
    {
        return new String[] { Msgs.add, Msgs.remove, Msgs.up, Msgs.down };
    }

    private String[] getPortalSectionLabels()
    {
        return new String[] { Msgs.add, Msgs.remove };
    }

    private static class Msgs extends NLS
    {
        public static String add;
        public static String down;
        public static String liferayPluginPackageProperties;
        public static String properties;
        public static String remove;
        public static String up;

        static
        {
            initializeMessages( PluginPackageFormPage.class.getName(), Msgs.class );
        }
    }
}
