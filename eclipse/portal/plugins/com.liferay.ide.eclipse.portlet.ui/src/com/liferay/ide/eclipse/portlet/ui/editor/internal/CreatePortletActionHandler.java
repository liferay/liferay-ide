/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Pvt Ltd., All rights reserved.
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
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.portlet.ui.editor.internal;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsContentNode;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPagePart;
import org.eclipse.sapphire.ui.swt.SapphireDialog;

import com.liferay.ide.eclipse.portlet.core.model.IPortlet;
import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class CreatePortletActionHandler extends SapphireActionHandler {

	private static final String NEW_PORTLET_WIZARD_DEF =
		"com.liferay.ide.eclipse.portlet.ui/com/liferay/ide/eclipse/portlet/ui/editor/portlet-app.sdef!new.portlet.wizard";

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
	 */
	@Override
	protected Object run( SapphireRenderingContext context ) {
		IPortletApp rootModel = (IPortletApp) context.getPart().getModelElement();
		IPortlet portlet = rootModel.getPortlets().addNewElement();
		// Open the dialog to capture the mandatory properties
		final SapphireDialog dialog = new SapphireDialog( context.getShell(), portlet, NEW_PORTLET_WIZARD_DEF );
		if ( dialog != null && Dialog.OK == dialog.open() ) {
			// Select the node
			final MasterDetailsEditorPagePart page = getPart().nearest( MasterDetailsEditorPagePart.class );
			final MasterDetailsContentNode root = page.getContentOutline().getRoot();
			final MasterDetailsContentNode node = root.findNodeByModelElement( portlet );
			if ( node != null ) {
				node.select();
			}
			try {
				rootModel.resource().save();
			}
			catch ( ResourceStoreException e ) {
				// Log it in PorletUI Plugin
			}
			return portlet;
		}
		else {
			rootModel.getPortlets().remove( portlet );
			portlet = null;
			try {
				rootModel.resource().save();
			}
			catch ( ResourceStoreException e ) {
				// Log it in PorletUI Plugin
			}
			return null;
		}

	}
}
