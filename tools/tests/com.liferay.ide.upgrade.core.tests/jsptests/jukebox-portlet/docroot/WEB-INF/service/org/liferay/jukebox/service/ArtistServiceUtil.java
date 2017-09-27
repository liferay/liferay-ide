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

package org.liferay.jukebox.service;

import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.util.ReferenceRegistry;
import com.liferay.portal.service.InvokableService;

/**
 * Provides the remote service utility for Artist. This utility wraps
 * {@link org.liferay.jukebox.service.impl.ArtistServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on a remote server. Methods of this service are expected to have security
 * checks based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Julio Camarero
 * @see ArtistService
 * @see org.liferay.jukebox.service.base.ArtistServiceBaseImpl
 * @see org.liferay.jukebox.service.impl.ArtistServiceImpl
 * @generated
 */
public class ArtistServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link org.liferay.jukebox.service.impl.ArtistServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	public static java.lang.String getBeanIdentifier() {
		return getService().getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	public static void setBeanIdentifier(java.lang.String beanIdentifier) {
		getService().setBeanIdentifier(beanIdentifier);
	}

	public static java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return getService().invokeMethod(name, parameterTypes, arguments);
	}

	public static org.liferay.jukebox.model.Artist addArtist(
		java.lang.String name, java.lang.String bio,
		java.io.InputStream inputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().addArtist(name, bio, inputStream, serviceContext);
	}

	public static org.liferay.jukebox.model.Artist deleteArtist(long artistId,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().deleteArtist(artistId, serviceContext);
	}

	public static java.util.List<org.liferay.jukebox.model.Artist> getArtists(
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getArtists(groupId);
	}

	public static java.util.List<org.liferay.jukebox.model.Artist> getArtists(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getArtists(groupId, start, end);
	}

	public static java.util.List<org.liferay.jukebox.model.Artist> getArtists(
		long groupId, java.lang.String keywords)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getArtists(groupId, keywords);
	}

	public static int getArtistsCount(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getArtistsCount(groupId);
	}

	public static int getArtistsCount(long groupId, java.lang.String keywords)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getArtistsCount(groupId, keywords);
	}

	public static org.liferay.jukebox.model.Artist updateArtist(long artistId,
		java.lang.String name, java.lang.String bio,
		java.io.InputStream inputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .updateArtist(artistId, name, bio, inputStream,
			serviceContext);
	}

	public static void clearService() {
		_service = null;
	}

	public static ArtistService getService() {
		if (_service == null) {
			InvokableService invokableService = (InvokableService)PortletBeanLocatorUtil.locate(ClpSerializer.getServletContextName(),
					ArtistService.class.getName());

			if (invokableService instanceof ArtistService) {
				_service = (ArtistService)invokableService;
			}
			else {
				_service = new ArtistServiceClp(invokableService);
			}

			ReferenceRegistry.registerReference(ArtistServiceUtil.class,
				"_service");
		}

		return _service;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setService(ArtistService service) {
	}

	private static ArtistService _service;
}