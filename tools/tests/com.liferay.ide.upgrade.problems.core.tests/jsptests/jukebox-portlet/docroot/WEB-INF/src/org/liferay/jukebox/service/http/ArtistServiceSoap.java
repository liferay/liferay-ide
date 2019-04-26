/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package org.liferay.jukebox.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import org.liferay.jukebox.service.ArtistServiceUtil;

import java.rmi.RemoteException;

/**
 * Provides the SOAP utility for the
 * {@link org.liferay.jukebox.service.ArtistServiceUtil} service utility. The
 * static methods of this class calls the same methods of the service utility.
 * However, the signatures are different because it is difficult for SOAP to
 * support certain types.
 *
 * <p>
 * ServiceBuilder follows certain rules in translating the methods. For example,
 * if the method in the service utility returns a {@link java.util.List}, that
 * is translated to an array of {@link org.liferay.jukebox.model.ArtistSoap}.
 * If the method in the service utility returns a
 * {@link org.liferay.jukebox.model.Artist}, that is translated to a
 * {@link org.liferay.jukebox.model.ArtistSoap}. Methods that SOAP cannot
 * safely wire are skipped.
 * </p>
 *
 * <p>
 * The benefits of using the SOAP utility is that it is cross platform
 * compatible. SOAP allows different languages like Java, .NET, C++, PHP, and
 * even Perl, to call the generated services. One drawback of SOAP is that it is
 * slow because it needs to serialize all calls into a text format (XML).
 * </p>
 *
 * <p>
 * You can see a list of services at http://localhost:8080/api/axis. Set the
 * property <b>axis.servlet.hosts.allowed</b> in portal.properties to configure
 * security.
 * </p>
 *
 * <p>
 * The SOAP utility is only generated for remote services.
 * </p>
 *
 * @author Julio Camarero
 * @see ArtistServiceHttp
 * @see org.liferay.jukebox.model.ArtistSoap
 * @see org.liferay.jukebox.service.ArtistServiceUtil
 * @generated
 */
public class ArtistServiceSoap {
	public static org.liferay.jukebox.model.ArtistSoap deleteArtist(
		long artistId, com.liferay.portal.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			org.liferay.jukebox.model.Artist returnValue = ArtistServiceUtil.deleteArtist(artistId,
					serviceContext);

			return org.liferay.jukebox.model.ArtistSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static org.liferay.jukebox.model.ArtistSoap[] getArtists(
		long groupId) throws RemoteException {
		try {
			java.util.List<org.liferay.jukebox.model.Artist> returnValue = ArtistServiceUtil.getArtists(groupId);

			return org.liferay.jukebox.model.ArtistSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static org.liferay.jukebox.model.ArtistSoap[] getArtists(
		long groupId, int start, int end) throws RemoteException {
		try {
			java.util.List<org.liferay.jukebox.model.Artist> returnValue = ArtistServiceUtil.getArtists(groupId,
					start, end);

			return org.liferay.jukebox.model.ArtistSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static org.liferay.jukebox.model.ArtistSoap[] getArtists(
		long groupId, java.lang.String keywords) throws RemoteException {
		try {
			java.util.List<org.liferay.jukebox.model.Artist> returnValue = ArtistServiceUtil.getArtists(groupId,
					keywords);

			return org.liferay.jukebox.model.ArtistSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static int getArtistsCount(long groupId) throws RemoteException {
		try {
			int returnValue = ArtistServiceUtil.getArtistsCount(groupId);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static int getArtistsCount(long groupId, java.lang.String keywords)
		throws RemoteException {
		try {
			int returnValue = ArtistServiceUtil.getArtistsCount(groupId,
					keywords);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	private static Log _log = LogFactoryUtil.getLog(ArtistServiceSoap.class);
}