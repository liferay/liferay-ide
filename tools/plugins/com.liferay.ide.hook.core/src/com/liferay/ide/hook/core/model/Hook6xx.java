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

package com.liferay.ide.hook.core.model;

import com.liferay.ide.hook.core.model.internal.HookRootElementController;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
@CustomXmlRootBinding(HookRootElementController.class)
@XmlBinding(path = "hook")
public interface Hook6xx extends Hook {

	public ElementType TYPE = new ElementType(Hook6xx.class);

}