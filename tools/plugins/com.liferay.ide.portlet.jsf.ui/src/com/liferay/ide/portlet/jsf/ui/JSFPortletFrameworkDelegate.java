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

package com.liferay.ide.portlet.jsf.ui;

import com.liferay.ide.portlet.jsf.core.IJSFPortletFrameworkProperties;
import com.liferay.ide.portlet.jsf.core.JSFPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.IPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.ui.AbstractPortletFrameworkDelegate;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
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

        final Group group = SWTUtil.createGroup( parent, Msgs.selectJSFComponentSuite, 2 );

        createComponentSuiteOption
        (
            group, 
            Msgs.jsfStandard,
            Msgs.jsfStandardDescription,
            "icons/e16/jsf-logo-16x16.png",  //$NON-NLS-1$
            "http://javaserverfaces.java.net/", //$NON-NLS-1$
            COMPONENT_SUITE_JSF_STANDARD
        );

        createComponentSuiteOption
        (
            group, 
            Msgs.liferayFacesAlloy,
            Msgs.liferayFacesAlloyDescription,
            "icons/e16/liferay_faces.png",  //$NON-NLS-1$
            "http://www.liferay.com/community/liferay-projects/liferay-faces/alloy", //$NON-NLS-1$
            COMPONENT_SUITE_LIFERAY_FACES_ALLOY
        );

        createComponentSuiteOption
        (
            group, 
            Msgs.iceFaces,
            Msgs.componentsJQuery,
            "icons/e16/icefaces_16x16.png",  //$NON-NLS-1$
            "http://www.icesoft.org/projects/ICEfaces", //$NON-NLS-1$
            COMPONENT_SUITE_ICEFACES
        );

        createComponentSuiteOption
        (
            group, 
            Msgs.primeFaces,
            Msgs.primeFacesDescription,
            "icons/e16/primefaces_16x16.png",  //$NON-NLS-1$
            "http://www.primefaces.org/", //$NON-NLS-1$
            COMPONENT_SUITE_PRIMEFACES
        );

        createComponentSuiteOption
        (
            group, 
            Msgs.richFaces,
            Msgs.richFacesDescription,
            "icons/e16/portlet_16x16.png",  //$NON-NLS-1$
            "http://www.jboss.org/richfaces", //$NON-NLS-1$
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

        final Link link = SWTUtil.createHyperLink( parent, SWT.WRAP, desc, 1, helpUrl );

        final GridData layoutData = new GridData( SWT.LEFT, SWT.TOP, true, false );
        layoutData.widthHint = 350;
        link.setLayoutData( layoutData );
    }

    @Override
    protected void updateFragmentEnabled( IDataModel dataModel )
    {
        String frameworkId = dataModel.getStringProperty( IPluginProjectDataModelProperties.PORTLET_FRAMEWORK_ID );

        IPortletFrameworkWizardProvider framework = LiferayProjectCore.getPortletFramework( frameworkId );

        if( framework instanceof JSFPortletFrameworkWizardProvider )
        {
            dataModel.setBooleanProperty( IPluginProjectDataModelProperties.PLUGIN_FRAGMENT_ENABLED, false );
        }
    }

    private static class Msgs extends NLS
    {
        public static String richFacesDescription;
        public static String primeFacesDescription;
        public static String componentsJQuery;
        public static String liferayFacesAlloyDescription;
        public static String jsfStandardDescription;
        public static String iceFaces;
        public static String jsfStandard;
        public static String liferayFacesAlloy;
        public static String primeFaces;
        public static String richFaces;
        public static String selectJSFComponentSuite;

        static
        {
            initializeMessages( JSFPortletFrameworkDelegate.class.getName(), Msgs.class );
        }
    }
}
