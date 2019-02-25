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

package com.liferay.ide.layouttpl.core.util;

import com.liferay.ide.core.templates.ITemplateOperation;
import com.liferay.ide.core.templates.TemplatesCore;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.layouttpl.core.LayoutTplCore;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author Gregory Amerson
 */
public interface LayoutTemplateAccessor extends SapphireContentAccessor {

	public default String getTemplateSource(LayoutTplElement layouttpl) {
		StringBuffer buffer = new StringBuffer();

		try {
			ITemplateOperation templateOperation = null;

			if (get(layouttpl.getBootstrapStyle())) {
				templateOperation = TemplatesCore.getTemplateOperation(
					"com.liferay.ide.layouttpl.core.layoutTemplate.bootstrap");
			}
			else {
				templateOperation = TemplatesCore.getTemplateOperation(
					"com.liferay.ide.layouttpl.core.layoutTemplate.legacy");
			}

			LayoutTplUtil.createLayoutTplContext(templateOperation, layouttpl);

			templateOperation.setOutputBuffer(buffer);
			templateOperation.execute(new NullProgressMonitor());
		}
		catch (Exception ex) {
			LayoutTplCore.logError("Error getting template source.", ex);
		}

		return buffer.toString();
	}

}