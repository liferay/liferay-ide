/*
 * Copyright (c) 2016 the original author or authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.liferay.ide.core.workspace;

import com.liferay.ide.core.Event;

import org.eclipse.core.resources.IProject;

/**
 * Common base class for project add/delete/move events.
 *
 * @author Donat Csikos
 */
public abstract class BaseProjectChangedEvent implements Event {

	public BaseProjectChangedEvent(IProject project) {
		_project = project;
	}

	public IProject getProject() {
		return _project;
	}

	private final IProject _project;

}