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

import java.net.URI;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Charles Wu
 */
public class OriginalModuleHintValidationService extends ValidationService {

	@Override
	public void dispose() {
		NewModuleExtOp moduleExtOp = _op();

		if (moduleExtOp != null) {
			SapphireUtil.detachListener(moduleExtOp.getOriginalModuleName(), _listener);
			SapphireUtil.detachListener(moduleExtOp.getOriginalModuleVersion(), _listener);
		}

		super.dispose();
	}

	@Override
	protected Status compute() {
		NewModuleExtOp moduleExtOp = _op();

		URI uri = SapphireUtil.getContent(moduleExtOp.getSourceFileUri());

		if (uri == null) {
			if (moduleExtOp instanceof NewModuleExtFilesOp) {
				return Status.createErrorStatus(
					"Unable to identify original module in current context, did you refresh project with Gradle?");
			}
			else {
				return Status.createWarningStatus("Unable to identify original module in current context.");
			}
		}

		return Status.createOkStatus();
	}

	protected void initValidationService() {
		super.initValidationService();

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