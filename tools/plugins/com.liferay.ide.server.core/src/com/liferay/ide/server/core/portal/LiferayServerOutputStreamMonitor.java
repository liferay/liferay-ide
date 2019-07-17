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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IFlushableStreamMonitor;

/**
 * @author Simon Jiang
 */
public class LiferayServerOutputStreamMonitor implements IFlushableStreamMonitor {

	public LiferayServerOutputStreamMonitor() {
		_contentsBuffer = new StringBuffer();
	}

	public void addListener(IStreamListener listener) {
		_listeners.add(listener);
	}

	public void append(byte[] b, int start, int length) {
		if ((b == null) || (start < 0)) {
			return;
		}

		append(new String(b, start, length));
	}

	public void append(String text) {
		if (text == null) {
			return;
		}

		if (isBuffered()) {
			_contentsBuffer.append(text);
		}

		new StreamNotifier().notifyAppend(text);
	}

	public void flushContents() {
		_contentsBuffer.setLength(0);
	}

	public String getContents() {
		return _contentsBuffer.toString();
	}

	public boolean isBuffered() {
		return _buffered;
	}

	public void removeListener(IStreamListener listener) {
		_listeners.remove(listener);
	}

	public void setBuffered(boolean buffer) {
		_buffered = buffer;
	}

	protected void close() {
		_listeners.removeAll();
	}

	private boolean _buffered = true;
	private StringBuffer _contentsBuffer;
	private StreamListenerList _listeners = new StreamListenerList(1);

	private class StreamNotifier implements ISafeRunnable {

		public void handleException(Throwable exception) {
			LiferayServerCore.logError(exception);
		}

		public void notifyAppend(String text) {
			if (text == null) {
				return;
			}

			_text = text;
			Object[] listeners = _listeners.getListeners();

			for (int i = 0; i < listeners.length; ++i) {
				_streamListener = (IStreamListener)listeners[i];
				SafeRunner.run(this);
			}

			_streamListener = null;
			_text = null;
		}

		public void run() throws Exception {
			_streamListener.streamAppended(_text, LiferayServerOutputStreamMonitor.this);
		}

		private IStreamListener _streamListener;
		private String _text;

	}

}