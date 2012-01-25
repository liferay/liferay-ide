/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.server.jboss.ui.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.jboss.ide.eclipse.as.ui.wizards.JBoss7ServerWizardFragment;

import com.liferay.ide.eclipse.server.jboss.ui.LiferayJBossUIPlugin;

/**
 * @author kamesh
 */
public class LiferayJBoss7ServerWizardFragment extends JBoss7ServerWizardFragment
{

	public LiferayJBoss7ServerWizardFragment()
	{

	}

	/* (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.ui.wizards.JBossServerWizardFragment#createComposite(org.eclipse.swt.widgets.Composite, org.eclipse.wst.server.ui.wizard.IWizardHandle)
	 */
	@Override
	public Composite createComposite( Composite parent, IWizardHandle handle )
	{
		Composite composite = super.createComposite( parent, handle );
		handle.setTitle( "Liferay JBoss Server" );
		handle.setDescription( "Liferay Portal Bundled JBoss Application Server 7.0" );
		handle.setImageDescriptor( ImageDescriptor.createFromURL( LiferayJBossUIPlugin.getDefault().getBundle().getEntry(
						"/icons/wizban/server_wiz.png" ) ) );
		return composite;
	}
	
	
	
	
	
}
