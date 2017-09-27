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

import org.liferay.jukebox.service.AlbumServiceUtil;

import java.rmi.RemoteException;

/**
 * Provides the SOAP utility for the
 * {@link org.liferay.jukebox.service.AlbumServiceUtil} service utility. The
 * static methods of this class calls the same methods of the service utility.
 * However, the signatures are different because it is difficult for SOAP to
 * support certain types.
 *
 * <p>
 * ServiceBuilder follows certain rules in translating the methods. For example,
 * if the method in the service utility returns a {@link java.util.List}, that
 * is translated to an array of {@link org.liferay.jukebox.model.AlbumSoap}.
 * If the method in the service utility returns a
 * {@link org.liferay.jukebox.model.Album}, that is translated to a
 * {@link org.liferay.jukebox.model.AlbumSoap}. Methods that SOAP cannot
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
 * @see AlbumServiceHttp
 * @see org.liferay.jukebox.model.AlbumSoap
 * @see org.liferay.jukebox.service.AlbumServiceUtil
 * @generated
 */
public class AlbumServiceSoap {
	public static org.liferay.jukebox.model.AlbumSoap deleteAlbum(
		long albumId, com.liferay.portal.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			org.liferay.jukebox.model.Album returnValue = AlbumServiceUtil.deleteAlbum(albumId,
					serviceContext);

			return org.liferay.jukebox.model.AlbumSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static org.liferay.jukebox.model.AlbumSoap[] getAlbums(long groupId)
		throws RemoteException {
		try {
			java.util.List<org.liferay.jukebox.model.Album> returnValue = AlbumServiceUtil.getAlbums(groupId);

			return org.liferay.jukebox.model.AlbumSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static org.liferay.jukebox.model.AlbumSoap[] getAlbums(
		long groupId, int start, int end) throws RemoteException {
		try {
			java.util.List<org.liferay.jukebox.model.Album> returnValue = AlbumServiceUtil.getAlbums(groupId,
					start, end);

			return org.liferay.jukebox.model.AlbumSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static org.liferay.jukebox.model.AlbumSoap[] getAlbums(
		long groupId, java.lang.String keywords) throws RemoteException {
		try {
			java.util.List<org.liferay.jukebox.model.Album> returnValue = AlbumServiceUtil.getAlbums(groupId,
					keywords);

			return org.liferay.jukebox.model.AlbumSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static org.liferay.jukebox.model.AlbumSoap[] getAlbumsByArtistId(
		long groupId, long artistId) throws RemoteException {
		try {
			java.util.List<org.liferay.jukebox.model.Album> returnValue = AlbumServiceUtil.getAlbumsByArtistId(groupId,
					artistId);

			return org.liferay.jukebox.model.AlbumSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static int getAlbumsCount(long groupId) throws RemoteException {
		try {
			int returnValue = AlbumServiceUtil.getAlbumsCount(groupId);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static int getAlbumsCount(long groupId, java.lang.String keywords)
		throws RemoteException {
		try {
			int returnValue = AlbumServiceUtil.getAlbumsCount(groupId, keywords);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static int getAlbumsCountByArtistId(long groupId, long artistId)
		throws RemoteException {
		try {
			int returnValue = AlbumServiceUtil.getAlbumsCountByArtistId(groupId,
					artistId);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static org.liferay.jukebox.model.AlbumSoap moveAlbumToTrash(
		long albumId) throws RemoteException {
		try {
			org.liferay.jukebox.model.Album returnValue = AlbumServiceUtil.moveAlbumToTrash(albumId);

			return org.liferay.jukebox.model.AlbumSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static org.liferay.jukebox.model.AlbumSoap restoreAlbumFromTrash(
		long albumId) throws RemoteException {
		try {
			org.liferay.jukebox.model.Album returnValue = AlbumServiceUtil.restoreAlbumFromTrash(albumId);

			return org.liferay.jukebox.model.AlbumSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	private static Log _log = LogFactoryUtil.getLog(AlbumServiceSoap.class);
}