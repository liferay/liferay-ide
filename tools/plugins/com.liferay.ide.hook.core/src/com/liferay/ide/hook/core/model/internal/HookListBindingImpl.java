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

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.hook.core.model.Hook;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.LayeredListPropertyBinding;

/**
 * @author Gregory Amerson
 */
public abstract class HookListBindingImpl extends LayeredListPropertyBinding {

	protected Hook hook() {
		return property().nearest(Hook.class);
	}

	protected IProject project() {
		return hook().adapt(IProject.class);
	}

}