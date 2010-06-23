
package com.liferay.ide.eclipse.layouttpl.ui.parts;

import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;


public class LayoutTplTreeEditPartFactory implements EditPartFactory {


	public EditPart createEditPart(EditPart context, Object model) {
		if (model instanceof PortletColumn) {
			return new PortletColumnTreeEditPart((PortletColumn) model);
		}

		if (model instanceof LayoutTplDiagram) {
			return new LayoutTplDiagramTreeEditPart((LayoutTplDiagram) model);
		}

		return null;
	}

}
