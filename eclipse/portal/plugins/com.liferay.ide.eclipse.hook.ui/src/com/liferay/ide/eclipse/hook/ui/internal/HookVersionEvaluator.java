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

import java.util.Arrays;
import java.util.List;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireModelCondition;

import com.liferay.ide.eclipse.hook.core.model.IHook;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class HookVersionEvaluator extends SapphireModelCondition {

	private List<String> versions;

	@Override
	protected void initCondition( ISapphirePart part, String parameter ) {
		String[] nonApplicableVersions = parameter.split( "," );
		versions = Arrays.asList( nonApplicableVersions );
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
			canShow = !versions.contains( dtdVersion );

		}
		return canShow;

	}

}
