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
 * Dispatches {@link Event} instances to all registered {@link EventListener} instances.
 *
 * @author Etienne Studer
 * @author Donát Csikós
 */
public interface ListenerRegistry {

	/**
	 * Registers the given event listener.
	 *
	 * @param listener the listener to register
	 */
	public void addEventListener(EventListener listener);

	/**
	 * Dispatches the given event to all registered listeners.
	 *
	 * @param event the event to dispatch
	 */
	public void dispatch(Event event);

	/**
	 * Unregisters the given event listener.
	 *
	 * @param listener the listener to unregister
	 */
	public void removeEventListener(EventListener listener);

}