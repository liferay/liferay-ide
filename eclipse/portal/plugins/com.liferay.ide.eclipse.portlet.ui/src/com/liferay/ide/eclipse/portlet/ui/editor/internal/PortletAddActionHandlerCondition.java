/**
 * 
 */

package com.liferay.ide.eclipse.portlet.ui.editor.internal;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.SapphireCondition;

import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;

/**
 * @author kamesh.sampath
 */
public class PortletAddActionHandlerCondition extends SapphireCondition {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireCondition#evaluate()
	 */
	@Override
	protected boolean evaluate() {
		IModelElement modelElement = getPart().getModelElement();
		// System.out.println( "PortletAddActionHandlerCondition.evaluate()" + modelElement );
		if ( modelElement instanceof IPortletApp ) {
			return true;
		}
		return false;
	}
}
