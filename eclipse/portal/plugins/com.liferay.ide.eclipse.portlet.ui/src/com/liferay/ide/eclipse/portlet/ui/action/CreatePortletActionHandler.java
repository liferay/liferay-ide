/**
 * 
 */

package com.liferay.ide.eclipse.portlet.ui.action;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;

import com.liferay.ide.eclipse.portlet.ui.wizard.NewPortletWizard;

/**
 * @author kamesh
 */
public class CreatePortletActionHandler extends SapphireActionHandler {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
	 */
	@Override
	protected Object run( SapphireRenderingContext context ) {

		NewPortletWizard newPortletWizard = new NewPortletWizard();
		WizardDialog wizardDialog = new WizardDialog( context.getShell(), newPortletWizard );
		wizardDialog.create();
		wizardDialog.open();
		return null;
	}
}
