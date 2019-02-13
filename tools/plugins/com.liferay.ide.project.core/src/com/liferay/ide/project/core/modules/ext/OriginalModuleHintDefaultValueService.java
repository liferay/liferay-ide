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

package com.liferay.ide.project.core.modules.ext;

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringPool;

import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;

import org.osgi.framework.Version;

/**
 * @author Charles Wu
 */
public class OriginalModuleHintDefaultValueService extends DerivedValueService {

	@Override
	public void dispose() {
		super.dispose();

		NewModuleExtOp moduleExtOp = _op();

		if (moduleExtOp != null) {
			SapphireUtil.detachListener(moduleExtOp.getOriginalModuleName(), _listener);
			SapphireUtil.detachListener(moduleExtOp.getOriginalModuleVersion(), _listener);
		}
	}

	@Override
	protected String compute() {
		NewModuleExtOp moduleExtOp = _op();

		String name = SapphireUtil.getContent(moduleExtOp.getOriginalModuleName());
		Version version = SapphireUtil.getContent(moduleExtOp.getOriginalModuleVersion());

		if ((name != null) && (version != null)) {
			return name + ":" + version;
		}

		return StringPool.EMPTY;
	}

	protected void initDerivedValueService() {
		super.initDerivedValueService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewModuleExtOp moduleExtOp = _op();

		SapphireUtil.attachListener(moduleExtOp.getOriginalModuleName(), _listener);
		SapphireUtil.attachListener(moduleExtOp.getOriginalModuleVersion(), _listener);
	}

	private NewModuleExtOp _op() {
		return context(NewModuleExtOp.class);
	}

	private Listener _listener;

}