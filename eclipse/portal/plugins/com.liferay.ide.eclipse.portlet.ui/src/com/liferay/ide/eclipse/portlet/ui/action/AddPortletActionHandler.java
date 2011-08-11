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
public class AddPortletActionHandler extends SapphireActionHandler {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
	 */
	@Override
	protected Object run( SapphireRenderingContext context ) {
		/*
		 * TODO Invoke a common dialog which is not dependent on Liferay New Project Wizard, to make this Editor
		 * compatible for portlet.xml editing
		 */
		NewPortletWizard newPortletWizard = new NewPortletWizard();
		WizardDialog wizardDialog = new WizardDialog( context.getShell(), newPortletWizard );
		wizardDialog.create();
		wizardDialog.open();
		return null;
	}
}
