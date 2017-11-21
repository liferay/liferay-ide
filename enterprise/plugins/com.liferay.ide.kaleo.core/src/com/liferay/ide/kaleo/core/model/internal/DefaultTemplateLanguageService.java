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

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.TemplateLanguageType;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.Element;

/**
 * @author Gregory Amerson
 */
public class DefaultTemplateLanguageService extends DefaultValueService {

	@Override
	protected String compute() {
		return KaleoModelUtil.getDefaultValue(
			context(Element.class), KaleoCore.DEFAULT_TEMPLATE_LANGUAGE_KEY, TemplateLanguageType.FREEMARKER);
	}

}