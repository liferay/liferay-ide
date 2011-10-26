/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 * Contributors:
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.hook.ui.internal;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireModelCondition;

import com.liferay.ide.eclipse.hook.core.model.IHook;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class HookVersionEvaluator extends SapphireModelCondition {

	private String parameter;

	@Override
	protected void initCondition( ISapphirePart part, String parameter ) {
		super.initCondition( part, parameter );
		this.parameter = parameter;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireCondition#evaluate()
	 */
	@Override
	protected boolean evaluate() {
		boolean canShow = false;
		final IModelElement element = getPart().getModelElement();
		if ( element instanceof IHook ) {
			IHook hook = (IHook) element;
			String dtdVersion = hook.getVersion().toString();
			canShow = !parameter.equalsIgnoreCase( dtdVersion );
		}
		return canShow;

	}

}
