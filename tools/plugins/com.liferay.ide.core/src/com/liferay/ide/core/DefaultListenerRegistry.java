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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Default implementation of {@link ListenerRegistry}.
 *
 * @author Etienne Studer
 * @author Donát Csikós
 */
public final class DefaultListenerRegistry implements ListenerRegistry {

	@Override
	public void addEventListener(EventListener listener) {
		synchronized (_lock) {
			_listeners.add(listener);
		}
	}

	@Override
	public void dispatch(Event event) {
		Collection<EventListener> listeners;
		synchronized (_lock) {
			listeners = Collections.unmodifiableCollection(_listeners);
		}

		for (EventListener listener : listeners) {
			listener.onEvent(event);
		}
	}

	@Override
	public void removeEventListener(EventListener listener) {
		synchronized (_lock) {
			_listeners.remove(listener);
		}
	}

	private final Set<EventListener> _listeners = new LinkedHashSet<>();
	private final Object _lock = new Object();

}