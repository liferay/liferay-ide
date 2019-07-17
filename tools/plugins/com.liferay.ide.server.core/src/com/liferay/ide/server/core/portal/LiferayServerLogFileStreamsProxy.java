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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IStreamMonitor;

/**
 * @author Simon Jiang
 */
public class LiferayServerLogFileStreamsProxy {

	public LiferayServerLogFileStreamsProxy(PortalRuntime runtime, ILaunch curLaunch) {
		this(runtime, curLaunch, new LiferayServerOutputStreamMonitor(), new LiferayServerOutputStreamMonitor());
	}

	public LiferayServerLogFileStreamsProxy(
		PortalRuntime runtime, ILaunch curLaunch, LiferayServerOutputStreamMonitor systemOut,
		LiferayServerOutputStreamMonitor systemErr) {

		_launch = null;

		if (runtime == null) {
			return;
		}

		PortalBundle portalBundle = runtime.getPortalBundle();

		_launch = curLaunch;

		try {
			IPath defaultLogPath = portalBundle.getLogPath();

			sysoutFile = defaultLogPath.toOSString();

			if (systemOut != null) {
				sysOut = systemOut;
			}
			else {
				sysOut = new LiferayServerOutputStreamMonitor();
			}

			startMonitoring();
		}
		catch (Exception e) {
		}
	}

	public IStreamMonitor getErrorStreamMonitor() {
		return null;
	}

	public ILaunch getLaunch() {
		return _launch;
	}

	public IStreamMonitor getOutputStreamMonitor() {
		return sysOut;
	}

	public boolean isTerminated() {
		return _done;
	}

	public void terminate() {
		if (_bufferOut != null) {
			try {
				_bufferOut.close();

				_bufferOut = null;
			}
			catch (Exception e) {
			}
		}

		_done = true;
	}

	public void write(String input) throws IOException {
	}

	protected void readToNow(BufferedReader br) throws IOException {
		String s = "";

		while (s != null) {
			s = br.readLine();
		}
	}

	protected final boolean shouldReloadFileReader(long originalFileSize, long newFileSize) {
		boolean reloadFileReader = true;

		if (originalFileSize <= newFileSize) {
			reloadFileReader = false;
		}

		return reloadFileReader;
	}

	protected void startMonitoring() {
		if (_streamThread != null) {
			return;
		}

		_streamThread = new Thread("Liferay Portal Log Monitor Thread") {

			public void run() {
				boolean outInitialized = false;
				boolean outFileEmpty = false;

				while (!_done && !outInitialized) {
					try {
						_fpOut = (sysoutFile != null) ? new File(sysoutFile) : null;

						if (!outInitialized) {
							if (!_fpOut.exists()) {
								outFileEmpty = true;
							}
							else {
								outInitialized = true;
							}
						}
					}
					catch (Exception e) {
					}

					if (outInitialized) {
						continue;
					}

					try {
						sleep(200L);
					}
					catch (Exception e) {
					}
				}

				try {
					if (outInitialized) {
						_bufferOut = new BufferedReader(new FileReader(_fpOut));

						if (!outFileEmpty) {
							readToNow(_bufferOut);
						}
					}
				}
				catch (Exception e) {
				}

				long originalFpOutSize = _fpOut.length();

				while (!_done) {
					try {
						sleep(500L);
					}
					catch (Exception e) {
					}

					try {
						String s = "";

						while ((s != null) && !_done) {
							long newFpOutSize = _fpOut.length();

							if (shouldReloadFileReader(originalFpOutSize, newFpOutSize)) {
								if (_bufferOut != null) {
									_bufferOut.close();
								}

								_bufferOut = new BufferedReader(new FileReader(_fpOut));
							}

							originalFpOutSize = newFpOutSize;

							if (_bufferOut != null) {
								s = _bufferOut.readLine();

								if (s != null) {
									sysOut.append(s + "\n");
								}
							}
						}
					}
					catch (Exception e) {
					}
				}

				_streamThread = null;
			}

		};

		_streamThread.setPriority(1);

		_streamThread.setDaemon(true);

		_streamThread.start();
	}

	protected LiferayServerOutputStreamMonitor sysOut;
	protected String sysoutFile;

	private BufferedReader _bufferOut = null;
	private boolean _done = false;
	private File _fpOut = null;
	private ILaunch _launch;
	private Thread _streamThread;

}