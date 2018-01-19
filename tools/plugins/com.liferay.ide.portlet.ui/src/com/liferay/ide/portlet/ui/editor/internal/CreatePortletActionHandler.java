/**
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
 */

package com.liferay.ide.portlet.ui.editor.internal;

import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.PortletApp;
import com.liferay.ide.portlet.ui.editor.PortletXmlEditor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.MasterDetailsContentNodePart;
import org.eclipse.sapphire.ui.forms.MasterDetailsEditorPagePart;
import org.eclipse.sapphire.ui.forms.swt.SapphireDialog;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class CreatePortletActionHandler extends SapphireActionHandler {

	/**
	 * (non-Javadoc)
	 *
	 * @see
	 * SapphireActionHandler#run(org.eclipse.sapphire.ui.
	 * SapphireRenderingContext)
	 */
	@Override
	protected Object run(Presentation context) {
		PortletApp rootModel = (PortletApp)context.part().getModelElement();

		Portlet portlet = rootModel.getPortlets().insert();

		// Open the dialog to capture the mandatory properties

		SapphireDialog dialog = new SapphireDialog(
			((SwtPresentation)context).shell(), portlet, DefinitionLoader.sdef(PortletXmlEditor.class).dialog());

		if ((dialog != null) && (Dialog.OK == dialog.open())) {

			// Select the node

			MasterDetailsEditorPagePart page = getPart().nearest(MasterDetailsEditorPagePart.class);

			MasterDetailsContentNodePart root = page.outline().getRoot();

			MasterDetailsContentNodePart node = root.findNode(portlet);

			if (node != null) {
				node.select();
			}

			try {
				rootModel.resource().save();
			}
			catch (ResourceStoreException rse) {

				// Log it in PorletUI Plugin

			}

			return portlet;
		}
		else {
			rootModel.getPortlets().remove(portlet);
			portlet = null;
			try {
				rootModel.resource().save();
			}
			catch (ResourceStoreException rse) {

				// Log it in PorletUI Plugin

			}

			return null;
		}
	}

}