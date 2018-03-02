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
import com.liferay.ide.layouttpl.core.LayoutTplCore;
import com.liferay.ide.layouttpl.core.util.LayoutTplUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class LayoutTplElementsFactory {

	public static final LayoutTplElementsFactory INSTANCE = new LayoutTplElementsFactory();

	public void initPortletColumnFromElement(PortletColumnElement portletColumn, IDOMElement domElement) {
		if (domElement == null) {
			return;
		}

		String existingClassName = domElement.getAttribute("class");

		if (!CoreUtil.isNullOrEmpty(existingClassName) &&
			!existingClassName.equals(portletColumn.getClassName().content()) &&
			existingClassName.contains("portlet-column")) {

			portletColumn.setClassName(existingClassName);
		}

		portletColumn.setWeight(LayoutTplUtil.getWeightValue(domElement, -1));

		IDOMElement[] portletLayoutDOMElements = LayoutTplUtil.findChildElementsByClassName(
			domElement, "div", "portlet-layout");

		if (ListUtil.isNotEmpty(portletLayoutDOMElements)) {
			for (IDOMElement portletLayoutDOMElement : portletLayoutDOMElements) {
				PortletLayoutElement portletLayout = portletColumn.getPortletLayouts().insert();

				initPortletLayoutFromElement(portletLayout, portletLayoutDOMElement);
			}
		}
	}

	public void initPortletLayoutFromElement(PortletLayoutElement portletLayout, IDOMElement domElement) {
		if (domElement == null) {
			return;
		}

		String existingClassName = domElement.getAttribute("class");

		if (!CoreUtil.isNullOrEmpty(existingClassName) &&
			existingClassName.contains(portletLayout.getClassName().content())) {

			portletLayout.setClassName(existingClassName);
		}

		IDOMElement[] portletColumnDOMElements = LayoutTplUtil.findChildElementsByClassName(
			domElement, "div", "portlet-column");

		for (IDOMElement portletColumnElement : portletColumnDOMElements) {
			PortletColumnElement portletColumn = portletLayout.getPortletColumns().insert();

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
			domModel = (IDOMModel)StructuredModelManager.getModelManager().getModelForRead(file);

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

		LayoutTplElement layoutTpl = null;

		IDOMDocument rootDocument = model.getDocument();

		IDOMElement mainContentElement = LayoutTplUtil.findMainContentElement(rootDocument);

		if (mainContentElement != null) {
			layoutTpl = LayoutTplElement.TYPE.instantiate();

			layoutTpl.setBootstrapStyle(bootstrapStyle);
			layoutTpl.setClassName(mainContentElement.getAttribute("class"));
			layoutTpl.setIs62(is62);

			IDOMElement[] portletLayoutElements = LayoutTplUtil.findChildElementsByClassName(
				mainContentElement, "div", "portlet-layout");

			if (ListUtil.isNotEmpty(portletLayoutElements)) {
				for (IDOMElement portletLayoutElement : portletLayoutElements) {
					PortletLayoutElement portletLayout = layoutTpl.getPortletLayouts().insert();

					initPortletLayoutFromElement(portletLayout, portletLayoutElement);
				}
			}
		}

		return layoutTpl;
	}

}