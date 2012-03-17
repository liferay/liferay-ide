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
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.TaskModel;
import org.jboss.ide.eclipse.as.ui.Messages;
import org.jboss.ide.eclipse.as.ui.wizards.JBoss7RuntimeWizardFragment;

import com.liferay.ide.eclipse.server.jboss.ui.LiferayJBossUIPlugin;

/**
 * @author Kamesh
 */

public class LiferayJBoss7RuntimeFragment extends JBoss7RuntimeWizardFragment
{

	public LiferayJBoss7RuntimeFragment()
	{
		super();
	}

	@Override
	protected void updateWizardHandle( Composite parent )
	{
		super.updateWizardHandle( parent );
		
		handle.setTitle( "Liferay JBoss Runtime" );
		IRuntime r = (IRuntime) getTaskModel().getObject( TaskModel.TASK_RUNTIME );
		String version = r.getRuntimeType().getVersion();
		String description = NLS.bind( isEAP() ? Messages.JBEAP_version : Messages.JBAS_version, version );
		nameText.setText( r.getRuntimeType().getName() );
		handle.setDescription( "Liferay " + description );
		handle.setImageDescriptor( ImageDescriptor.createFromURL( LiferayJBossUIPlugin.getDefault().getBundle().getEntry(
			"/icons/wizban/server_wiz.png" ) ) );
	}

}
