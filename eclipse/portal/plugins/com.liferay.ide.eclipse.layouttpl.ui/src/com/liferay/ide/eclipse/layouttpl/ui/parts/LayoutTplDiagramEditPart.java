
package com.liferay.ide.eclipse.layouttpl.ui.parts;

import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.ModelElement;
import com.liferay.ide.eclipse.layouttpl.ui.policies.LayoutTplDiagramLayoutEditPolicy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class LayoutTplDiagramEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

	public static final int DIAGRAM_MARGIN = 10;

	protected Panel diagramPanel;

	public void activate() {
		if (!isActive()) {
			super.activate();
			((ModelElement) getModel()).addPropertyChangeListener(this);
		}
	}

	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ModelElement) getModel()).removePropertyChangeListener(this);
		}
	}

	public int getContainerWidth() {
		return diagramPanel.getSize().width - (DIAGRAM_MARGIN * 2);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();

		if (LayoutTplDiagram.ROW_ADDED_PROP.equals(prop) || LayoutTplDiagram.ROW_REMOVED_PROP.equals(prop)) {
			refreshChildren();
		}
	}

	protected void createEditPolicies() {
		// disallows the removal of this edit part from its parent
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		// handles constraint changes (e.g. moving and/or resizing) of model
		// elements
		// and creation of new model elements
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new LayoutTplDiagramLayoutEditPolicy());
	}

	protected IFigure createFigure() {
		// Panel backgroundPanel = new Panel();
		// backgroundPanel.setBackgroundColor(new Color(null, 10, 10, 10));
		// backgroundPanel.setBorder(new MarginBorder(20));
		// backgroundPanel.setLayoutManager(new GridLayout(1, false));

		diagramPanel = new Panel() {

			@Override
			public void paint(Graphics graphics) {
				super.paint(graphics);
			}

		};
		diagramPanel.setBackgroundColor(new Color(null, 171, 171, 171));
		diagramPanel.setBorder(new MarginBorder(DIAGRAM_MARGIN));
		diagramPanel.setLayoutManager(new GridLayout(1, false));

		// backgroundPanel.add(layoutPanel, new GridData());

		return diagramPanel;
	}

	protected LayoutTplDiagram getCastedModel() {
		return (LayoutTplDiagram) getModel();
	}

	protected Display getDisplay() {
		return this.getViewer().getControl().getDisplay();
	}

	protected List<ModelElement> getModelChildren() {
		return getCastedModel().getRows(); // return a list of rows
	}

}
