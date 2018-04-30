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

import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.ui.util.LayoutTemplatesFactory;
import com.liferay.ide.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.def.ActionHandlerFactoryDef;

/**
 * @author Kuo Zhang
 */
public class AddLayoutTemplateActionHandlerFactory extends SapphireActionHandlerFactory {

	public AddLayoutTemplateActionHandlerFactory() {
	}

	@Override
	public List<SapphireActionHandler> create() {
		if (_ADD_LAYOUT_TEMPLATE_ACTION_ID.equals(getAction().getId())) {
			ArrayList<SapphireActionHandler> actionHandlers = new ArrayList<>();

			actionHandlers.add(new Add_Layout_1_2_I_ActionHandler());
			actionHandlers.add(new Add_Layout_1_2_II_ActionHandler());
			actionHandlers.add(new Add_Layout_2_2_ActionHandler());
			actionHandlers.add(new Add_Layout_1_2_1_ActionHandler());

			return actionHandlers;
		}

		return null;
	}

	@Override
	public void init(SapphireAction action, ActionHandlerFactoryDef def) {
		super.init(action, def);

		LayoutTplElement tplElement = getModelElement().nearest(LayoutTplElement.class);

		Value<Boolean> bootstrapStyle = tplElement.getBootstrapStyle();

		_bootstrapStyle = bootstrapStyle.content();
	}

	private boolean _canOverride() {
		return MessageDialog.openQuestion(UIUtil.getActiveShell(), "Warning", Msgs.addLayoutTplWarningMsg);
	}

	private static final String _ADD_LAYOUT_1_2_1_ACTION_HANDLER_ID = "Add.Layout.1_2_1.ActionHandler";

	private static final String _ADD_LAYOUT_1_2_I_ACTION_HANDLER_ID = "Add.Layout.1_2_I.ActionHandler";

	private static final String _ADD_LAYOUT_1_2_II_ACTION_HANDLER_ID = "Add.Layout.1_2_II.ActionHandler";

	private static final String _ADD_LAYOUT_2_2_ACTION_HANDLER_ID = "Add.Layout.2_2.ActionHandler";

	private static final String _ADD_LAYOUT_TEMPLATE_ACTION_ID = "LayoutTpl.Add.LayoutTemplate";

	private boolean _bootstrapStyle;

	private static class Msgs extends NLS {

		public static String addLayoutTplWarningMsg;

		static {
			initializeMessages(AddLayoutTemplateActionHandlerFactory.class.getName(), Msgs.class);
		}

	}

	private class Add_Layout_1_2_1_ActionHandler extends SapphireActionHandler {

		@Override
		public void init(SapphireAction action, ActionHandlerDef def) {
			super.init(action, def);

			setId(_ADD_LAYOUT_1_2_1_ACTION_HANDLER_ID);
			setLabel();
		}

		@Override
		protected Object run(Presentation context) {
			SapphirePart part = context.part();

			Element modelElement = part.getModelElement();

			LayoutTplElement element = modelElement.nearest(LayoutTplElement.class);

			if ((element.getPortletLayouts().size() == 0) || _canOverride()) {
				element.getPortletLayouts().clear();
				LayoutTemplatesFactory.add_Layout_1_2_1(element);
			}

			return null;
		}

		protected void setLabel() {
			String prefix = "Layout with 3 Rows ";

			if (_bootstrapStyle) {
				super.setLabel(prefix + "(12, (6, 6), 12))");
			}
			else {
				super.setLabel(prefix + "(100, (50, 50), 100)");
			}
		}

	}

	private class Add_Layout_1_2_I_ActionHandler extends SapphireActionHandler {

		@Override
		public void init(SapphireAction action, ActionHandlerDef def) {
			super.init(action, def);

			setId(_ADD_LAYOUT_1_2_I_ACTION_HANDLER_ID);
			setLabel();
		}

		@Override
		protected Object run(Presentation context) {
			SapphirePart part = context.part();

			Element modelElement = part.getModelElement();

			LayoutTplElement element = modelElement.nearest(LayoutTplElement.class);

			if ((element.getPortletLayouts().size() == 0) || _canOverride()) {
				element.getPortletLayouts().clear();
				LayoutTemplatesFactory.add_Layout_1_2_I(element);
			}

			return null;
		}

		protected void setLabel() {
			String prefix = "Layout with 2 Rows ";

			if (_bootstrapStyle) {
				super.setLabel(prefix + "(12, (4, 8))");
			}
			else {
				super.setLabel(prefix + "(100, (30, 70))");
			}
		}

	}

	private class Add_Layout_1_2_II_ActionHandler extends SapphireActionHandler {

		@Override
		public void init(SapphireAction action, ActionHandlerDef def) {
			super.init(action, def);

			setId(_ADD_LAYOUT_1_2_II_ACTION_HANDLER_ID);
			setLabel();
		}

		@Override
		protected Object run(Presentation context) {
			SapphirePart part = context.part();

			Element modelElement = part.getModelElement();

			LayoutTplElement element = modelElement.nearest(LayoutTplElement.class);

			if ((element.getPortletLayouts().size() == 0) || _canOverride()) {
				element.getPortletLayouts().clear();
				LayoutTemplatesFactory.add_Layout_1_2_II(element);
			}

			return null;
		}

		protected void setLabel() {
			String prefix = "Layout with 2 Rows ";

			if (_bootstrapStyle) {
				super.setLabel(prefix + "(12, (8, 4))");
			}
			else {
				super.setLabel(prefix + "(100, (70, 30))");
			}
		}

	}

	private class Add_Layout_2_2_ActionHandler extends SapphireActionHandler {

		@Override
		public void init(SapphireAction action, ActionHandlerDef def) {
			super.init(action, def);

			setId(_ADD_LAYOUT_2_2_ACTION_HANDLER_ID);
			setLabel();
		}

		@Override
		protected Object run(Presentation context) {
			SapphirePart part = context.part();

			Element modelElement = part.getModelElement();

			LayoutTplElement element = modelElement.nearest(LayoutTplElement.class);

			if ((element.getPortletLayouts().size() == 0) || _canOverride()) {
				element.getPortletLayouts().clear();
				LayoutTemplatesFactory.add_Layout_2_2(element);
			}

			return null;
		}

		protected void setLabel() {
			String prefix = "Layout with 2 Rows ";

			if (_bootstrapStyle) {
				super.setLabel(prefix + "((8, 4), (4, 8))");
			}
			else {
				super.setLabel(prefix + "((70, 30), (30, 70))");
			}
		}

	}

}