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

package com.liferay.ide.portlet.ui.action.filters;

import java.util.Objects;

import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFilter;

/**
 * @author Kamesh Sampath
 */
public class AddPortletActionFilter extends SapphireActionHandlerFilter {

	/**
	 * (non-Javadoc)
	 *
	 * @see SapphireActionHandlerFilter#check(org.eclipse.
	 * sapphire.ui.SapphireActionHandler)
	 */
	@Override
	public boolean check(SapphireActionHandler handler) {

		// Element Element = handler.getModelElement();

		// System.out.println( String.format(
		// "AddPortletActionFilter.check() - Action Handler[Action-ID=%s,
		// Handler-ID=%s,Model-Element=%s]",
		// handler.getAction().getId(), handler.getId(), Element.getClass().getName() )
		// );

		SapphireAction action = handler.getAction();

		if (Objects.equals("Sapphire.Add.IPortlet", handler.getId()) &&
			Objects.equals("Sapphire.Add", action.getId())) {

			return false;
		}

		return true;
	}

}