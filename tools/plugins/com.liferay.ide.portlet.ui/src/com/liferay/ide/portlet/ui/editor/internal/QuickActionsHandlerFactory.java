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

package com.liferay.ide.portlet.ui.editor.internal;

import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.def.ActionHandlerFactoryDef;
import org.eclipse.sapphire.ui.forms.MasterDetailsContentNodePart;
import org.eclipse.sapphire.ui.forms.MasterDetailsContentOutline;
import org.eclipse.sapphire.ui.forms.MasterDetailsEditorPagePart;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class QuickActionsHandlerFactory extends SapphireActionHandlerFactory {

	/**
	 * (non-Javadoc)
	 *
	 * @see SapphireActionHandlerFactory#create()
	 */
	@Override
	public List<SapphireActionHandler> create() {
		List<SapphireActionHandler> listOfHandlers = new ArrayList<>();

		for (String property : _modelProperties) {
			if ((property != null) && "Portlets".equalsIgnoreCase(property) && _isPartInLiferayProject()) {
				SapphireActionHandler handler = new CreateLiferayPortletActionHandler();

				handler.init(getAction(), null);
				handler.addImage(Portlet.TYPE.image());
				handler.setLabel(_getActionLabel(Msgs.portlets));

				listOfHandlers.add(handler);
			}
			else {
				listOfHandlers.add(new Handler(property));
			}
		}

		// System.out.println( "QuickActionsHandlerFactory.created" +
		// listOfHandlers.size() + " handlers " );

		return listOfHandlers;
	}

	@Override
	public void init(SapphireAction action, ActionHandlerFactoryDef def) {
		super.init(action, def);

		String strModelElementNames = def.getParam("MODEL_PROPERTIES");

		if (strModelElementNames != null) {
			_modelProperties = strModelElementNames.split(",");
		}
		else {
			throw new IllegalStateException(NLS.bind(Msgs.message, "MODEL_PROPERTIES"));
		}
	}

	/**
	 * This is make a compact and singular label text
	 */
	private static String _getActionLabel(String labelText) {
		if (labelText.endsWith("s")) {
			labelText = labelText.substring(0, labelText.lastIndexOf("s"));
		}

		if (labelText.equals(Msgs.portlet)) {
			labelText += "...";
		}

		return labelText;
	}

	private boolean _isPartInLiferayProject() {
		SapphireEditor editor = getPart().nearest(SapphireEditor.class);

		if ((editor != null) && ProjectUtil.isLiferayFacetedProject(editor.getProject())) {
			return true;
		}

		return false;
	}

	private String[] _modelProperties;

	private static final class Handler extends SapphireActionHandler {

		public Handler(String property) {
			_strProperty = property;
		}

		@Override
		public void init(SapphireAction action, ActionHandlerDef def) {
			super.init(action, def);

			ISapphirePart part = action.getPart();

			Element rootModel = part.getModelElement();

			ElementType type = rootModel.type();

			PropertyDef property = type.property(_strProperty);

			String labelText = property.getLabel(false, CapitalizationType.FIRST_WORD_ONLY, true);

			String actionLabel = _getActionLabel(labelText);

			setLabel(actionLabel);

			ElementType propModelElementType = property.getType();

			addImage(propModelElementType.image());
		}

		/**
		 * (non-Javadoc)
		 *
		 * @see
		 * SapphireActionHandler#run(org.eclipse.sapphire.ui.
		 * SapphireRenderingContext)
		 */
		@Override
		protected Object run(Presentation context) {
			SapphirePart part = context.part();

			Element rootModel = part.getModelElement();

			ElementType type = rootModel.type();

			PropertyDef property = type.property(_strProperty);

			Object obj = rootModel.property(property);

			Element mElement = null;

			if (obj instanceof ElementList<?>) {
				ElementList<?> list = (ElementList<?>)obj;

				mElement = list.insert();
			}
			else {
				throw new UnsupportedOperationException(Msgs.bind(Msgs.unsuportedOperation, _strProperty));
			}

			// Select the node

			MasterDetailsEditorPagePart page = getPart().nearest(MasterDetailsEditorPagePart.class);

			MasterDetailsContentOutline outline = page.outline();

			MasterDetailsContentNodePart root = outline.getRoot();

			MasterDetailsContentNodePart node = root.findNode(mElement);

			if (node != null) {
				node.select();
			}

			return mElement;
		}

		private String _strProperty;

	}

	private static class Msgs extends NLS {

		public static String message;
		public static String portlet;
		public static String portlets;
		public static String unsuportedOperation;

		static {
			initializeMessages(QuickActionsHandlerFactory.class.getName(), Msgs.class);
		}

	}

}