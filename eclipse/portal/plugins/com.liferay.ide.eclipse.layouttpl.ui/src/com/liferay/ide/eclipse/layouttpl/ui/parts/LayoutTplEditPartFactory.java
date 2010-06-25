package com.liferay.ide.eclipse.layouttpl.ui.parts;

import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletLayout;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;


public class LayoutTplEditPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object modelElement) {
		// get EditPart for model element
		EditPart part = getPartForElement(modelElement);

		// store model element in EditPart
		part.setModel(modelElement);

		return part;
	}

	protected EditPart getPartForElement(Object modelElement) {
		if (modelElement instanceof LayoutTplDiagram) {
			return new LayoutTplDiagramEditPart();
		}

		if (modelElement instanceof PortletLayout) {
			return new PortletLayoutEditPart();
		}

		if (modelElement instanceof PortletColumn) {
			return new PortletColumnEditPart();
		}

		throw new RuntimeException("Can't create part for model element: " +
			((modelElement != null) ? modelElement.getClass().getName() : "null"));
	}

}
