/**
 * 
 */

package com.liferay.ide.eclipse.portlet.ui.action;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.swt.SapphireDialog;

import com.liferay.ide.eclipse.portlet.core.model.IPortlet;
import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;

/**
 * @author kamesh
 */
public class ConfigurePortletActionHandler extends SapphireActionHandler {

	private static final String DIALOG_DEFINITION_PATH =
		"com.liferay.ide.eclipse.portlet.config.ui/com/liferay/ide/eclipse/portlet/config/ui/portlet-app.sdef!configure.portlet.dialog";

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
	 */
	@Override
	protected Object run( SapphireRenderingContext context ) {
		IPortletApp rootModel = (IPortletApp) context.getPart().getModelElement();
		IPortlet iPortlet = rootModel.getPortlets().addNewElement();
		SapphireDialog sapphireDialog = new SapphireDialog( context.getShell(), iPortlet, DIALOG_DEFINITION_PATH );
		if ( Dialog.OK == sapphireDialog.open() ) {
			rootModel.refresh();
		}
		else if ( Dialog.CANCEL == sapphireDialog.open() ) {
			rootModel.getPortlets().remove( iPortlet );
		}
		return iPortlet;
	}
}
