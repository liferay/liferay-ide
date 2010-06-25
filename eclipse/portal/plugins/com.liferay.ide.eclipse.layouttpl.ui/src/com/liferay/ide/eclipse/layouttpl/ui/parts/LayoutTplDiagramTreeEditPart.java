
package com.liferay.ide.eclipse.layouttpl.ui.parts;

import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.ModelElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

public class LayoutTplDiagramTreeEditPart extends AbstractTreeEditPart implements PropertyChangeListener {

	public LayoutTplDiagramTreeEditPart(LayoutTplDiagram model) {
		super(model);
	}


	public void activate() {
		if (!isActive()) {
			super.activate();
			((ModelElement) getModel()).addPropertyChangeListener(this);
		}
	}

	protected void createEditPolicies() {
		// If this editpart is the root content of the viewer, then disallow
		// removal
		if (getParent() instanceof RootEditPart) {
			installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		}
	}


	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ModelElement) getModel()).removePropertyChangeListener(this);
		}
	}

	private LayoutTplDiagram getCastedModel() {
		return (LayoutTplDiagram) getModel();
	}


	private EditPart getEditPartForChild(Object child) {
		return (EditPart) getViewer().getEditPartRegistry().get(child);
	}


	protected List<ModelElement> getModelChildren() {
		return getCastedModel().getRows();
	}


	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (LayoutTplDiagram.ROW_ADDED_PROP.equals(prop)) {
			// add a child to this edit part
			// causes an additional entry to appear in the tree of the outline
			// view
			addChild(createChild(evt.getNewValue()), -1);
		}
		else if (LayoutTplDiagram.ROW_REMOVED_PROP.equals(prop)) {
			// remove a child from this edit part
			// causes the corresponding edit part to disappear from the tree in
			// the outline view
			removeChild(getEditPartForChild(evt.getNewValue()));
		}
		else {
			refreshVisuals();
		}
	}
}
