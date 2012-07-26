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

package com.liferay.ide.portlet.jsf.ui;

import com.liferay.ide.portlet.jsf.core.IJSFPortletFrameworkProperties;
import com.liferay.ide.portlet.jsf.core.JSFPortletFrameworkWizardProvider;
import com.liferay.ide.project.ui.AbstractPortletFrameworkDelegate;
import com.liferay.ide.project.ui.ProjectUIPlugin;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.project.core.IPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelSynchHelper;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class JSFPortletFrameworkDelegate extends AbstractPortletFrameworkDelegate implements IJSFPortletFrameworkProperties
{
    private DataModelSynchHelper syncHelper;

    public JSFPortletFrameworkDelegate()
    {
        super();
    }

    public Composite createNewProjectOptionsComposite( Composite parent )
    {
        this.syncHelper = new DataModelSynchHelper( this.dataModel );
        
        final Group group = SWTUtil.createGroup( parent, "Select JSF component suite", 2 );
        
        createComponentSuiteOption
        (
            group, 
            "JSF standard", 
            "Standard UI components provided by the JSF runtime. <a>Learn more...</a>", 
            "icons/e16/jsf-logo-16x16.png", 
            "http://javaserverfaces.java.net/",
            COMPONENT_SUITE_JSF_STANDARD
        );
        
        createComponentSuiteOption
        (
            group, 
            "Liferay Faces Alloy", 
            "Components that utilize Liferay's Alloy UI technology based on YUI3. <a>Learn more...</a>", 
            "icons/e16/liferay_faces.png", 
            "http://www.liferay.com/community/liferay-projects/liferay-faces/alloy",
            COMPONENT_SUITE_LIFERAY_FACES_ALLOY
        );
        
        createComponentSuiteOption
        (
            group, 
            "ICEfaces", 
            "Components based in part on YUI and jQuery with automatic Ajax and Ajax Push support. <a>Learn more...</a>", 
            "icons/e16/icefaces_16x16.png", 
            "http://www.icesoft.org/projects/ICEfaces",
            COMPONENT_SUITE_ICEFACES
        );
        
        createComponentSuiteOption
        (
            group, 
            "PrimeFaces", 
            "Lightweight, zero-configuration JSF UI framework built on jQuery. <a>Learn more...</a>", 
            "icons/e16/primefaces_16x16.png", 
            "http://www.primefaces.org/",
            COMPONENT_SUITE_PRIMEFACES
        );
        
        createComponentSuiteOption
        (
            group, 
            "RichFaces", 
            "Next-generation JSF component framework by JBoss. <a>Learn more...</a>", 
            "icons/e16/portlet_16x16.png", 
            "http://www.jboss.org/richfaces",
            COMPONENT_SUITE_RICHFACES
        );
        
        return group;
    }
    
    private void createComponentSuiteOption(
        Composite parent, String label, String desc, String imagePath, final String helpUrl, String propertyName )
    {
        final Image image =
            ImageDescriptor.createFromURL( JSFUIPlugin.getDefault().getBundle().getEntry( imagePath ) ).createImage();

        final Button button = SWTUtil.createRadioButton( parent, label, image, false, 1 );
        
        this.syncHelper.synchRadio( button, propertyName, null );
        
        button.addDisposeListener
        ( 
            new DisposeListener()
            {
                public void widgetDisposed( DisposeEvent e )
                {
                    image.dispose();
                }
            }
        );
        
        final Link link = SWTUtil.createLink( parent, SWT.WRAP, desc, 1 );
        
        final GridData layoutData = new GridData( SWT.LEFT, SWT.TOP, true, false );
        layoutData.widthHint = 350;
        link.setLayoutData( layoutData );
        
        link.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    try 
                    {
                        PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(helpUrl) );
                    }
                    catch (Exception e1) {
                        ProjectUIPlugin.logError("Could not open external browser", e1);
                    }
                }
            }
        );
    }

    @Override
    protected void updateFragmentEnabled( IDataModel dataModel )
    {
        String frameworkId = dataModel.getStringProperty( IPluginProjectDataModelProperties.PORTLET_FRAMEWORK_ID );
        
        IPortletFrameworkWizardProvider framework = ProjectCorePlugin.getPortletFramework( frameworkId );

        if( framework instanceof JSFPortletFrameworkWizardProvider )
        {
            dataModel.setBooleanProperty( IPluginProjectDataModelProperties.PLUGIN_FRAGMENT_ENABLED, false );
        }
    }

}
