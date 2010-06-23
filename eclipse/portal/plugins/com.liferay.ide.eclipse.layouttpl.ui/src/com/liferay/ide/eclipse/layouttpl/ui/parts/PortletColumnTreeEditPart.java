
package com.liferay.ide.eclipse.layouttpl.ui.parts;

import com.liferay.ide.eclipse.layouttpl.ui.model.ModelElement;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;


public class PortletColumnTreeEditPart extends AbstractTreeEditPart implements PropertyChangeListener {


	public PortletColumnTreeEditPart(PortletColumn model) {
		super(model);
	}


	public void activate() {
		if (!isActive()) {
			super.activate();
			((ModelElement) getModel()).addPropertyChangeListener(this);
		}
	}


	protected void createEditPolicies() {
		// allow removal of the associated model element
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new PortletColumnComponentEditPolicy());
	}


	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ModelElement) getModel()).removePropertyChangeListener(this);
		}
	}

	private PortletColumn getCastedModel() {
		return (PortletColumn) getModel();
	}


	protected Image getImage() {
		return getCastedModel().getIcon();
	}


	protected String getText() {
		return getCastedModel().toString();
	}


	public void propertyChange(PropertyChangeEvent evt) {
		refreshVisuals(); // this will cause an invocation of getImage() and
		// getText(), see below
	}
}
