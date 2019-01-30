/*
 * Copyright (c) 2015 the original author or authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Etienne Studer & Donát Csikós (Gradle Inc.) - initial API and implementation and initial documentation
 */

package com.liferay.ide.core;

import com.liferay.ide.core.workspace.ProjectChangedEvent;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * Listens to {@link Event} instances dispatched via {@link ListenerRegistry}.
 *
 * @author Etienne Studer
 * @author Donát Csikós
 * @author Charles Wu
 */
public interface EventListener {

	public default <T> boolean hasResourcesAffected(
		ProjectChangedEvent projectChangedEvent, IProject project, T[] resources) {

		if (project.equals(projectChangedEvent.getProject())) {
			Set<IPath> affectedFiles = projectChangedEvent.getAffectedFiles();

			for (T resource : resources) {
				if (affectedFiles.contains(resource)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Invoked when an event has been dispatched through the listener registry with which this
	 * listener is registered. More information about the actual event is available by checking
	 * its concrete sub-type.
	 *
	 * @param event the dispatched event
	 */
	public void onEvent(Event event);

}