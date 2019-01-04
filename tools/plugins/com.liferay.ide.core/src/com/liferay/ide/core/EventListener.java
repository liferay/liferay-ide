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

/**
 * Listens to {@link Event} instances dispatched via {@link ListenerRegistry}.
 *
 * @author Etienne Studer
 * @author Donát Csikós
 */
public interface EventListener {

	/**
	 * Invoked when an event has been dispatched through the listener registry with which this
	 * listener is registered. More information about the actual event is available by checking
	 * its concrete sub-type.
	 *
	 * @param event the dispatched event
	 */
	public void onEvent(Event event);

}