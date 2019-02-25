/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.layouttpl.ui.actions;

import com.liferay.ide.layouttpl.core.model.CanAddPortletLayouts;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.ui.util.LayoutTemplatesFactory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.def.ActionHandlerFactoryDef;

/**
 * @author Kuo Zhang
 */
public class AddRowTemplateActionHandlerFactory extends SapphireActionHandlerFactory {

	public AddRowTemplateActionHandlerFactory() {
	}

	@Override
	public List<SapphireActionHandler> create() {
		if (_ADD_ROW_TEMPLATE_ACTION_ID.equals(getAction().getId())) {
			ArrayList<SapphireActionHandler> actionHandlers = new ArrayList<>();

			actionHandlers.add(new Add_Row_1_ActionHandler());
			actionHandlers.add(new Add_Row_2_I_ActionHandler());
			actionHandlers.add(new Add_Row_2_II_ActionHandler());
			actionHandlers.add(new Add_Row_2_III_ActionHandler());
			actionHandlers.add(new Add_Row_3_ActionHandler());

			return actionHandlers;
		}

		return null;
	}

	@Override
	public void init(SapphireAction action, ActionHandlerFactoryDef def) {
		super.init(action, def);

		LayoutTplElement layoutElement = getModelElement().nearest(LayoutTplElement.class);

		Value<Boolean> bootstrapStyle = layoutElement.getBootstrapStyle();

		_bootstrapStyle = bootstrapStyle.content();
	}

	private static final String _ADD_ROW_1_ACTION_HANDLER_ID = "Add.Row.1.ActionHandler";

	private static final String _ADD_ROW_2_I_ACTION_HANDLER_ID = "Add.Row.2_I.ActionHandler";

	private static final String _ADD_ROW_2_II_ACTION_HANDLER_ID = "Add.Row.2_II.ActionHandler";

	private static final String _ADD_ROW_2_III_ACTION_HANDLER_ID = "Add.Row.2_III.ActionHandler";

	private static final String _ADD_ROW_3_ACTION_HANDLER_ID = "Add.Row.3.ActionHandler";

	private static final String _ADD_ROW_TEMPLATE_ACTION_ID = "LayoutTpl.Add.RowTemplate";

	private boolean _bootstrapStyle;

	private class Add_Row_1_ActionHandler extends SapphireActionHandler {

		@Override
		public void init(SapphireAction action, ActionHandlerDef def) {
			super.init(action, def);

			setId(_ADD_ROW_1_ACTION_HANDLER_ID);
			setLabel();
		}

		@Override
		protected Object run(Presentation context) {
			CanAddPortletLayouts element = getModelElement().nearest(CanAddPortletLayouts.class);

			new LayoutTemplatesFactory().add_Row_1(element);

			return null;
		}

		protected void setLabel() {
			String prefix = "Row with 1 Column ";

			if (_bootstrapStyle) {
				super.setLabel(prefix + "(12)");
			}
			else {
				super.setLabel(prefix + "(100)");
			}
		}

	}

	private class Add_Row_2_I_ActionHandler extends SapphireActionHandler {

		@Override
		public void init(SapphireAction action, ActionHandlerDef def) {
			super.init(action, def);

			setId(_ADD_ROW_2_I_ACTION_HANDLER_ID);
			setLabel();
		}

		@Override
		protected Object run(Presentation context) {
			CanAddPortletLayouts element = getModelElement().nearest(CanAddPortletLayouts.class);

			new LayoutTemplatesFactory().add_Row_2_I(element);

			return null;
		}

		protected void setLabel() {
			String prefix = "Row with 2 Columns ";

			if (_bootstrapStyle) {
				super.setLabel(prefix + "(6, 6)");
			}
			else {
				super.setLabel(prefix + "(50, 50)");
			}
		}

	}

	private class Add_Row_2_II_ActionHandler extends SapphireActionHandler {

		@Override
		public void init(SapphireAction action, ActionHandlerDef def) {
			super.init(action, def);

			setId(_ADD_ROW_2_II_ACTION_HANDLER_ID);
			setLabel();
		}

		@Override
		protected Object run(Presentation context) {
			CanAddPortletLayouts element = getModelElement().nearest(CanAddPortletLayouts.class);

			new LayoutTemplatesFactory().add_Row_2_II(element);

			return null;
		}

		protected void setLabel() {
			String prefix = "Row with 2 Columns ";

			if (_bootstrapStyle) {
				super.setLabel(prefix + "(4, 8)");
			}
			else {
				super.setLabel(prefix + "(30, 70)");
			}
		}

	}

	private class Add_Row_2_III_ActionHandler extends SapphireActionHandler {

		@Override
		public void init(SapphireAction action, ActionHandlerDef def) {
			super.init(action, def);

			setId(_ADD_ROW_2_III_ACTION_HANDLER_ID);
			setLabel();
		}

		@Override
		protected Object run(Presentation context) {
			CanAddPortletLayouts element = getModelElement().nearest(CanAddPortletLayouts.class);

			LayoutTemplatesFactory.add_Row_2_III(element);

			return null;
		}

		protected void setLabel() {
			String prefix = "Row with 2 Columns ";

			if (_bootstrapStyle) {
				super.setLabel(prefix + "(8, 4)");
			}
			else {
				super.setLabel(prefix + "(70, 30)");
			}
		}

	}

	private class Add_Row_3_ActionHandler extends SapphireActionHandler {

		@Override
		public void init(SapphireAction action, ActionHandlerDef def) {
			super.init(action, def);

			setId(_ADD_ROW_3_ACTION_HANDLER_ID);
			setLabel();
		}

		@Override
		protected Object run(Presentation context) {
			CanAddPortletLayouts element = getModelElement().nearest(CanAddPortletLayouts.class);

			new LayoutTemplatesFactory().add_Row_3(element);

			return null;
		}

		protected void setLabel() {
			String prefix = "Row with 3 Columns ";

			if (_bootstrapStyle) {
				super.setLabel(prefix + "(4, 4, 4)");
			}
			else {
				super.setLabel(prefix + "(33, 33, 33)");
			}
		}

	}

}