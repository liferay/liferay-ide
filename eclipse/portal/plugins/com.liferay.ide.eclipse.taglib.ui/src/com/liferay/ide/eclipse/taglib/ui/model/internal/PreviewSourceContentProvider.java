/*******************************************************************************
 * Copyright (c) 2010-2011 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.taglib.ui.model.internal;

import com.liferay.ide.eclipse.taglib.ui.model.IAttribute;
import com.liferay.ide.eclipse.taglib.ui.model.ITag;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.services.DerivedValueService;


public class PreviewSourceContentProvider extends DerivedValueService {

	@Override
	public String getDerivedValue() {
		boolean preview = "Preview".equals( context( ModelProperty.class ).getName() );

		ITag tag = (ITag) context( IModelElement.class );;

		StringBuffer buffer = new StringBuffer();

		String tagName = tag.getName().getContent();

		String prefix = tag.getPrefix().getContent();

		if (preview) {
			buffer.append("<span style='color:RGB(64,128,128)'>&lt;");
		}
		else {
			buffer.append("<");
		}

		buffer.append(prefix + ":" + tagName);

		if (preview) {
			buffer.append("</span>");
		}

		for (IAttribute attr : tag.getRequiredAttributes()) {
			appendAttr(attr, buffer, preview);
		}

		for (IAttribute attr : tag.getOtherAttributes()) {
			appendAttr(attr, buffer, preview);
		}

		for (IAttribute attr : tag.getEvents()) {
			appendAttr(attr, buffer, preview);
		}

		if (preview) {
			buffer.append("<span style='color:RGB(64,128,128)'>&gt;&lt;");
		}
		else {
			buffer.append("><");
		}

		buffer.append("/" + prefix + ":" + tagName);

		if (preview) {
			buffer.append("&gt;</span>");
		}
		else {
			buffer.append(">");
		}

		return buffer.toString();
	}

	protected void appendAttr(IAttribute attr, StringBuffer buffer, boolean preview) {
		String content = attr.getValue().getContent();

		if (content != null) {
			buffer.append(" ");

			if (preview) {
				buffer.append("<span style='color:RGB(127,0,127)'>");
			}

			buffer.append(attr.getName().getContent());

			if (preview) {
				buffer.append("</span>");
			}

			buffer.append("=");

			if (preview) {
				buffer.append("<span style='color:RGB(42,0,255);font-style:italic'>&quot;");
			}
			else {
				buffer.append("\"");
			}

			buffer.append(content);

			if (preview) {
				buffer.append("&quot;</span>");
			}
			else {
				buffer.append("\"");
			}
		}
	}

}
