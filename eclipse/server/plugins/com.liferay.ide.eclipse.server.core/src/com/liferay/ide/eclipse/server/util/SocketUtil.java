/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.eclipse.server.util;

import com.liferay.ide.eclipse.server.core.LiferayServerCorePlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Greg Amerson
 */
public class SocketUtil {

	public static IStatus canConnect(String host, String port) {
		return canConnect(new Socket(), host, port);
	}

	public static IStatus canConnect(Socket socket, String host, String port) {
		IStatus status = null;

		InputStream in = null;

		try {
			InetSocketAddress address = new InetSocketAddress(host, Integer.valueOf(port));
			InetSocketAddress local = new InetSocketAddress(0);
			socket.bind(local);
			socket.connect(address);
			in = socket.getInputStream();
			status = Status.OK_STATUS;
		}
		catch (Exception e) {
			status = LiferayServerCorePlugin.createErrorStatus( "Could not connect." );
			// e.printStackTrace();
		}
		finally {
			if (socket != null) {
				try {
					socket.close();
				}
				catch (IOException e) {
					// best effort
				}
			}

			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) {
					// best effort
				}
			}
		}

		return status;
	}

}
