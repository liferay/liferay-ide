/*
 * Copyright (c) 2016 the original author or authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.liferay.ide.core.workspace;

import org.eclipse.core.resources.IProject;

/**
 * Project deleted event.
 *
 * @author Donat Csikos
 */
public final class ProjectMovedEvent extends BaseProjectChangedEvent {

	public ProjectMovedEvent(IProject project, String previousName) {
		super(project);

		_previousName = previousName;
	}

	public String getPreviousName() {
		return _previousName;
	}

	private final String _previousName;

}