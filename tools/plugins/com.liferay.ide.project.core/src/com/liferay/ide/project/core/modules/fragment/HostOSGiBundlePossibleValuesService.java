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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 * @author Andy Wu
 */
public class HostOSGiBundlePossibleValuesService extends PossibleValuesService implements SapphireContentAccessor {

	@Override
	public boolean ordered() {
		return true;
	}

	@Override
	protected void compute(Set<String> values) {
		if (_bundles != null) {
			values.addAll(_bundles);
		}
		else {
			NewModuleFragmentOp op = _op();

			if (!op.disposed()) {
				String runtimeName = get(op.getLiferayRuntimeName());

				IRuntime runtime = ServerUtil.getRuntime(runtimeName);

				if (runtime != null) {
					_bundles = ServerUtil.getModuleFileListFrom70Server(runtime);

					values.addAll(_bundles);
				}
			}
		}
	}

	@Override
	protected void initPossibleValuesService() {
		super.initPossibleValuesService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				_bundles = null;

				refresh();
			}

		};

		NewModuleFragmentOp op = _op();

		SapphireUtil.attachListener(op.property(NewModuleFragmentOp.PROP_LIFERAY_RUNTIME_NAME), _listener);
	}

	private NewModuleFragmentOp _op() {
		return context(NewModuleFragmentOp.class);
	}

	private List<String> _bundles = null;
	private FilteredListener<PropertyContentEvent> _listener;

}