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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.ui.IPortletFrameworkDelegate;
import com.liferay.ide.project.ui.ProjectUIPlugin;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jst.j2ee.internal.wizard.J2EEComponentFacetCreationWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class NewPortletPluginProjectPage extends J2EEComponentFacetCreationWizardPage
    implements IPluginProjectDataModelProperties
{
    protected Button[] frameworkButtons;
    protected Composite[] optionsComposites;
    protected Button pluginFragmentButton;

    public NewPortletPluginProjectPage( NewPluginProjectWizard wizard, IDataModel model )
    {
        super( model, "advanced.page" );

        setWizard( wizard );
        setImageDescriptor( wizard.getDefaultPageImageDescriptor() );
        setTitle( "Liferay Portlet Plugin Project" );
        setDescription( "Choose from available portlet frameworks depending on which technology is most appropriate for this project." );
    }

    @Override
    public boolean canFlipToNextPage()
    {
        return getModel().getBooleanProperty( PLUGIN_FRAGMENT_ENABLED );
    }

    protected void createFrameworkGroup( Composite parent )
    {
        Group group = SWTUtil.createGroup( parent, "Select portlet framework", 2 );
        GridData layoutData = new GridData( SWT.FILL, SWT.TOP, true, false, 2, 1 );
        layoutData.widthHint = 500;// make sure the width doesn't grow the dialog
        group.setLayoutData( layoutData );
        ( (GridLayout) group.getLayout() ).verticalSpacing = 10;

        IPortletFrameworkWizardProvider[] portletFrameworks = ProjectCorePlugin.getPortletFrameworks();

        if( !CoreUtil.isNullOrEmpty( portletFrameworks ) )
        {
            List<Button> buttons = new ArrayList<Button>();

            for( final IPortletFrameworkWizardProvider framework : portletFrameworks )
            {
                final IPortletFrameworkDelegate delegate = getWizard().getPortletFrameworkDelegate( framework.getId() );

                String iconUrl = null;
                String bundleId = null;

                if( delegate != null )
                {
                    iconUrl = delegate.getIconUrl();
                    bundleId = delegate.getBundleId();
                }
                else
                {
                    iconUrl = "icons/e16/jsp-template.png";
                    bundleId = ProjectUIPlugin.PLUGIN_ID;
                }

                Button templateButton =
                    SWTUtil.createRadioButton(
                        group, framework.getDisplayName(),
                        UIUtil.getPluginImageDescriptor( bundleId, iconUrl ).createImage(), false, 1 );
                templateButton.setData( framework );
                ( (GridData) templateButton.getLayoutData() ).verticalAlignment = SWT.TOP;

                templateButton.addSelectionListener( new SelectionAdapter()
                {

                    @Override
                    public void widgetSelected( SelectionEvent e )
                    {
                        getDataModel().setProperty( PORTLET_FRAMEWORK_ID, framework.getId() );
                        getDataModel().setBooleanProperty( PLUGIN_FRAGMENT_ENABLED, delegate.isFragmentEnabled() );
                    }

                } );

                buttons.add( templateButton );

                final URL helpUrl = framework.getHelpUrl();
                Link descriptionWithLink =
                    SWTUtil.createLink( group, SWT.WRAP, framework.getDescription() +
                        ( helpUrl != null ? " <a>Learn more....</a>" : "" ), 1 );
                descriptionWithLink.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );

                if( helpUrl != null )
                {
                    descriptionWithLink.addSelectionListener( new SelectionAdapter()
                    {

                        @Override
                        public void widgetSelected( SelectionEvent e )
                        {
                            try
                            {
                                PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL( helpUrl );
                            }
                            catch( Exception e1 )
                            {
                                ProjectUIPlugin.logError( "Could not open external browser", e1 );
                            }
                        }

                    } );
                }

            }

            frameworkButtons = buttons.toArray( new Button[0] );
            frameworkButtons[0].setSelection( true );
        }
    }

    protected void createTemplateOptionsGroup( Composite parent )
    {
        if( CoreUtil.isNullOrEmpty( frameworkButtons ) )
        {
            return;
        }

        final Composite stackComposite = new Composite( parent, SWT.NONE );
        stackComposite.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 2, 1 ) );

        final StackLayout optionsLayout = new StackLayout();
        stackComposite.setLayout( optionsLayout );

        List<Composite> composites = new ArrayList<Composite>();

        for( Button templateButton : frameworkButtons )
        {
            IPortletFrameworkWizardProvider template = (IPortletFrameworkWizardProvider) templateButton.getData();
            IPortletFrameworkDelegate delegate = getWizard().getPortletFrameworkDelegate( template.getId() );

            final Composite[] optionsComposite = new Composite[1];

            if( delegate != null )
            {
                optionsComposite[0] = delegate.createNewProjectOptionsComposite( stackComposite, getDataModel() );
            }

            templateButton.addSelectionListener( new SelectionAdapter()
            {

                @Override
                public void widgetSelected( SelectionEvent e )
                {
                    optionsLayout.topControl = optionsComposite[0];
                    stackComposite.layout();
                }

            } );

            if( optionsComposite[0] == null )
            {
                optionsComposite[0] = SWTUtil.createComposite( stackComposite, 1, 1, SWT.FILL );
                ( (GridData) optionsComposite[0].getLayoutData() ).verticalAlignment = SWT.TOP;
            }

            composites.add( optionsComposite[0] );
        }

        optionsComposites = composites.toArray( new Composite[0] );

        optionsLayout.topControl = optionsComposites[0];
        stackComposite.layout();
    }

    @Override
    protected Composite createTopLevelComposite( Composite parent )
    {
        Composite top = SWTUtil.createTopComposite( parent, 1 );

        createFrameworkGroup( top );
        // createPluginFragmentButton(top);
        createTemplateOptionsGroup( top );

        getModel().addListener( new IDataModelListener()
        {

            public void propertyChanged( DataModelEvent event )
            {
                // if the user has switched back to first page and changed either to or from a portlet type need to
                // validate again in case we need to clear out any errors from selecting a portlet framework
                if( PLUGIN_TYPE_PORTLET.equals( event.getPropertyName() ) )
                {
                    validatePage( false );
                }
            }
        } );

        return top;
    }

    protected IDataModel getModel()
    {
        return model;
    }

    @Override
    protected String getModuleFacetID()
    {
        return IModuleConstants.JST_WEB_MODULE;
    }

    public IPortletFrameworkWizardProvider getSelectedPortletFramework()
    {
        IPortletFrameworkWizardProvider retval = null;

        if( !CoreUtil.isNullOrEmpty( frameworkButtons ) )
        {
            for( Button templateButton : frameworkButtons )
            {
                if( templateButton.getSelection() )
                {
                    retval = (IPortletFrameworkWizardProvider) templateButton.getData();
                }
            }
        }

        return retval;
    }

    @Override
    protected String[] getValidationPropertyNames()
    {
        final List<String> validationPropertyNames = new ArrayList<String>();

        final String[] propNames = new String[] { PLUGIN_FRAGMENT_ENABLED, PORTLET_FRAMEWORK_ID };

        Collections.addAll( validationPropertyNames, propNames );

        for( IPortletFrameworkWizardProvider portletFramework : ProjectCorePlugin.getPortletFrameworks() )
        {
            validationPropertyNames.addAll( portletFramework.getPropertyNames() );
        }

        return validationPropertyNames.toArray( new String[0] );
    }

    @Override
    public NewPluginProjectWizard getWizard()
    {
        return (NewPluginProjectWizard) super.getWizard();
    }

}
