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

	public LiferayServerLogFileStreamsProxy(PortalRuntime portalRuntime, ILaunch launch) {
		this(portalRuntime, launch, new LiferayServerOutputStreamMonitor(), new LiferayServerOutputStreamMonitor());
	}

	public LiferayServerLogFileStreamsProxy(
		PortalRuntime portalRuntime, ILaunch launch, LiferayServerOutputStreamMonitor serverOut,
		LiferayServerOutputStreamMonitor serverErr) {

		_launch = null;

		if (portalRuntime == null) {
			return;
		}

		PortalBundle portalBundle = portalRuntime.getPortalBundle();

		_launch = launch;

		try {
			IPath defaultLogPath = portalBundle.getLogPath();

			_sysoutFile = defaultLogPath.toOSString();

			if (serverOut != null) {
				_sysOut = serverOut;
			}
			else {
				_sysOut = new LiferayServerOutputStreamMonitor();
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
		return _sysOut;
	}

	public boolean isTerminated() {
		return _done;
	}

	public void terminate() {
		if (_bufferedOut != null) {
			try {
				_bufferedOut.close();

				_bufferedOut = null;
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
		if (_monitorThread != null) {
			return;
		}

		_monitorThread = new Thread("Liferay Portal Log Monitor Thread") {

			public void run() {
				boolean outInitialized = false;
				boolean outFileEmpty = false;

				while (!_done && !outInitialized) {
					try {
						_logFile = (_sysoutFile != null) ? new File(_sysoutFile) : null;

						if (!outInitialized) {
							if (!_logFile.exists()) {
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
						_bufferedOut = new BufferedReader(new FileReader(_logFile));

						if (!outFileEmpty) {
							readToNow(_bufferedOut);
						}
					}
				}
				catch (Exception e) {
				}

				long originalLogFileSize = _logFile.length();

				while (!_done) {
					try {
						sleep(500L);
					}
					catch (Exception e) {
					}

					try {
						String s = "";

						while (!_done) {
							long newLogFileSize = _logFile.length();

							if (shouldReloadFileReader(originalLogFileSize, newLogFileSize)) {
								if (_bufferedOut != null) {
									_bufferedOut.close();
								}

								_bufferedOut = new BufferedReader(new FileReader(_logFile));
							}

							originalLogFileSize = newLogFileSize;

							if (_bufferedOut != null) {
								s = _bufferedOut.readLine();

								if (s != null) {
									_sysOut.append(s + "\n");
								}
							}
						}
					}
					catch (Exception e) {
					}
				}

				_monitorThread = null;
			}

		};

		_monitorThread.setPriority(1);

		_monitorThread.setDaemon(true);

		_monitorThread.start();
	}

	private BufferedReader _bufferedOut = null;
	private boolean _done = false;
	private ILaunch _launch;
	private File _logFile = null;
	private Thread _monitorThread;
	private LiferayServerOutputStreamMonitor _sysOut;
	private String _sysoutFile;

}