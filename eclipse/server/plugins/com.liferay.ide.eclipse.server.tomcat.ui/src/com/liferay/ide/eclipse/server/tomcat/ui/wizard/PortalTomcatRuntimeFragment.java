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

package com.liferay.ide.eclipse.server.tomcat.ui.wizard;

import com.liferay.ide.eclipse.server.tomcat.ui.PortalTomcatUIPlugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatRuntimeWizardFragment;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class PortalTomcatRuntimeFragment extends TomcatRuntimeWizardFragment {

	public PortalTomcatRuntimeFragment() {
		super();
	}

	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new PortalTomcatRuntimeComposite(parent, wizard);
		
		wizard.setImageDescriptor(ImageDescriptor.createFromURL(PortalTomcatUIPlugin.getDefault().getBundle().getEntry(
			"/icons/wizban/server_wiz.png")));
		
		return comp;
	}

}
