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

package com.liferay.ide.server.ui;

import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Greg Amerson
 */
public class PortletPluginClasspathConatinerPage extends WizardPage implements IClasspathContainerPage
{

    public PortletPluginClasspathConatinerPage()
    {
        super( "portlet.container" ); //$NON-NLS-1$
    }

    public void createControl( Composite parent )
    {
        Composite topComposite = SWTUtil.createTopComposite( parent, 1 );

        SWTUtil.createLabel( topComposite, Msgs.test, 1 );
    }

    public boolean finish()
    {
        return false;
    }

    public IClasspathEntry getSelection()
    {
        return null;
    }

    public void setSelection( IClasspathEntry containerEntry )
    {
    }

    private static class Msgs extends NLS
    {
        public static String test;

        static
        {
            initializeMessages( PortletPluginClasspathConatinerPage.class.getName(), Msgs.class );
        }
    }
}
