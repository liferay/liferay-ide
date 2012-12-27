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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.server.tomcat.ui.wizard;

import com.liferay.ide.server.tomcat.ui.LiferayTomcatUIPlugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatRuntimeWizardFragment;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayTomcatRuntimeOptionalFragment extends TomcatRuntimeWizardFragment
{

    public LiferayTomcatRuntimeOptionalFragment()
    {
        super();
    }

    public Composite createComposite( Composite parent, IWizardHandle wizard )
    {
        comp = new LiferayTomcatRuntimeOptionalComposite( parent, wizard );

        wizard.setImageDescriptor( ImageDescriptor.createFromURL( LiferayTomcatUIPlugin.getDefault().getBundle().getEntry(
            "/icons/wizban/server_wiz.png" ) ) ); //$NON-NLS-1$

        return comp;
    }

}
