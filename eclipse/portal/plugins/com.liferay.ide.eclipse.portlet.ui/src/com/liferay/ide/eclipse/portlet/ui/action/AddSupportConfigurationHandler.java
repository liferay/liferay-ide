/**
 * 
 */

package com.liferay.ide.eclipse.portlet.ui.action;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;

/**
 * @author kamesh.sampath
 */
public class AddSupportConfigurationHandler extends SapphireActionHandler {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
	 */
	@Override
	protected Object run( SapphireRenderingContext context ) {
		MessageDialog.openInformation( context.getShell(), "Liferay IDE", "am yet to be implemented" );
		return null;
	}
}
