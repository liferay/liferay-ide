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

package com.liferay.ide.layouttpl.core.model;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.layouttpl.core.LayoutTplCore;
import com.liferay.ide.layouttpl.core.util.LayoutTplUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.ElementList;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class LayoutTplElementsFactory implements SapphireContentAccessor {

	public static final LayoutTplElementsFactory INSTANCE = new LayoutTplElementsFactory();

	public void initPortletColumnFromElement(PortletColumnElement portletColumn, IDOMElement domElement) {
		if (domElement == null) {
			return;
		}

		String existingClassName = domElement.getAttribute("class");

		if (CoreUtil.isNotNullOrEmpty(existingClassName) &&
			!existingClassName.equals(get(portletColumn.getClassName())) &&
			existingClassName.contains("portlet-column")) {

			portletColumn.setClassName(existingClassName);
		}

		portletColumn.setWeight(LayoutTplUtil.getWeightValue(domElement, -1));

		IDOMElement[] portletLayoutDOMElements = LayoutTplUtil.findChildElementsByClassName(
			domElement, "div", "portlet-layout");

		if (ListUtil.isNotEmpty(portletLayoutDOMElements)) {
			for (IDOMElement portletLayoutDOMElement : portletLayoutDOMElements) {
				ElementList<PortletLayoutElement> layouts = portletColumn.getPortletLayouts();

				PortletLayoutElement portletLayout = layouts.insert();

				initPortletLayoutFromElement(portletLayout, portletLayoutDOMElement);
			}
		}
	}

	public void initPortletLayoutFromElement(PortletLayoutElement portletLayout, IDOMElement domElement) {
		if (domElement == null) {
			return;
		}

		String existingClassName = domElement.getAttribute("class");

		if (CoreUtil.isNotNullOrEmpty(existingClassName) &&
			existingClassName.contains(get(portletLayout.getClassName()))) {

			portletLayout.setClassName(existingClassName);
		}

		IDOMElement[] portletColumnDOMElements = LayoutTplUtil.findChildElementsByClassName(
			domElement, "div", "portlet-column");

		for (IDOMElement portletColumnElement : portletColumnDOMElements) {
			ElementList<PortletColumnElement> columns = portletLayout.getPortletColumns();

			PortletColumnElement portletColumn = columns.insert();

			initPortletColumnFromElement(portletColumn, portletColumnElement);
		}
	}

	public LayoutTplElement newLayoutTplFromFile(IFile file, Boolean bootstrapStyle, Boolean is62) {
		if (FileUtil.notExists(file)) {
			return null;
		}

		LayoutTplElement layoutTpl = null;
		IDOMModel domModel = null;

		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();

			domModel = (IDOMModel)modelManager.getModelForRead(file);

			layoutTpl = newLayoutTplFromModel(domModel, bootstrapStyle, is62);
		}
		catch (Exception e) {
			LayoutTplCore.logError("Unable to read layout template file " + file.getName(), e);
		}
		finally {
			if (domModel != null) {
				domModel.releaseFromRead();
			}
		}

		return layoutTpl;
	}

	public LayoutTplElement newLayoutTplFromModel(IDOMModel model, Boolean bootstrapStyle, Boolean is62) {
		if (model == null) {
			return null;
		}

		IDOMElement mainContentElement = LayoutTplUtil.findMainContentElement(model.getDocument());

		if (mainContentElement == null) {
			return null;
		}

		LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();

		layoutTpl.setBootstrapStyle(bootstrapStyle);
		layoutTpl.setClassName(mainContentElement.getAttribute("class"));
		layoutTpl.setIs62(is62);

		IDOMElement[] portletLayoutElements = LayoutTplUtil.findChildElementsByClassName(
			mainContentElement, "div", "portlet-layout");

		if (ListUtil.isNotEmpty(portletLayoutElements)) {
			for (IDOMElement portletLayoutElement : portletLayoutElements) {
				ElementList<PortletLayoutElement> layouts = layoutTpl.getPortletLayouts();

				PortletLayoutElement portletLayout = layouts.insert();

				initPortletLayoutFromElement(portletLayout, portletLayoutElement);
			}
		}

		return layoutTpl;
	}

}