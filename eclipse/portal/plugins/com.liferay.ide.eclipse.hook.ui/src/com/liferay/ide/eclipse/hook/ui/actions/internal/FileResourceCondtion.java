
package com.liferay.ide.eclipse.hook.ui.actions.internal;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.SapphireCondition;

public class FileResourceCondtion extends SapphireCondition {

	public FileResourceCondtion() {
		super();
	}

	@Override
	protected boolean evaluate() {
		IModelElement modelElement = getPart().getModelElement();
		boolean isNotValid = !modelElement.validate().ok();
		return true;
	}
}
