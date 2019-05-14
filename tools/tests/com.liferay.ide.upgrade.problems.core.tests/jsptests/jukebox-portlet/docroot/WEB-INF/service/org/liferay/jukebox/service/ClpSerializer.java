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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ClassLoaderObjectInputStream;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.BaseModel;

import org.liferay.jukebox.model.AlbumClp;
import org.liferay.jukebox.model.ArtistClp;
import org.liferay.jukebox.model.SongClp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Julio Camarero
 */
public class ClpSerializer {
	public static String getServletContextName() {
		if (Validator.isNotNull(_servletContextName)) {
			return _servletContextName;
		}

		synchronized (ClpSerializer.class) {
			if (Validator.isNotNull(_servletContextName)) {
				return _servletContextName;
			}

			try {
				ClassLoader classLoader = ClpSerializer.class.getClassLoader();

				Class<?> portletPropsClass = classLoader.loadClass(
						"com.liferay.util.portlet.PortletProps");

				Method getMethod = portletPropsClass.getMethod("get",
						new Class<?>[] { String.class });

				String portletPropsServletContextName = (String)getMethod.invoke(null,
						"jukebox-portlet-deployment-context");

				if (Validator.isNotNull(portletPropsServletContextName)) {
					_servletContextName = portletPropsServletContextName;
				}
			}
			catch (Throwable t) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Unable to locate deployment context from portlet properties");
				}
			}

			if (Validator.isNull(_servletContextName)) {
				try {
					String propsUtilServletContextName = PropsUtil.get(
							"jukebox-portlet-deployment-context");

					if (Validator.isNotNull(propsUtilServletContextName)) {
						_servletContextName = propsUtilServletContextName;
					}
				}
				catch (Throwable t) {
					if (_log.isInfoEnabled()) {
						_log.info(
							"Unable to locate deployment context from portal properties");
					}
				}
			}

			if (Validator.isNull(_servletContextName)) {
				_servletContextName = "jukebox-portlet";
			}

			return _servletContextName;
		}
	}

	public static Object translateInput(BaseModel<?> oldModel) {
		Class<?> oldModelClass = oldModel.getClass();

		String oldModelClassName = oldModelClass.getName();

		if (oldModelClassName.equals(AlbumClp.class.getName())) {
			return translateInputAlbum(oldModel);
		}

		if (oldModelClassName.equals(ArtistClp.class.getName())) {
			return translateInputArtist(oldModel);
		}

		if (oldModelClassName.equals(SongClp.class.getName())) {
			return translateInputSong(oldModel);
		}

		return oldModel;
	}

	public static Object translateInput(List<Object> oldList) {
		List<Object> newList = new ArrayList<Object>(oldList.size());

		for (int i = 0; i < oldList.size(); i++) {
			Object curObj = oldList.get(i);

			newList.add(translateInput(curObj));
		}

		return newList;
	}

	public static Object translateInputAlbum(BaseModel<?> oldModel) {
		AlbumClp oldClpModel = (AlbumClp)oldModel;

		BaseModel<?> newModel = oldClpModel.getAlbumRemoteModel();

		newModel.setModelAttributes(oldClpModel.getModelAttributes());

		return newModel;
	}

	public static Object translateInputArtist(BaseModel<?> oldModel) {
		ArtistClp oldClpModel = (ArtistClp)oldModel;

		BaseModel<?> newModel = oldClpModel.getArtistRemoteModel();

		newModel.setModelAttributes(oldClpModel.getModelAttributes());

		return newModel;
	}

	public static Object translateInputSong(BaseModel<?> oldModel) {
		SongClp oldClpModel = (SongClp)oldModel;

		BaseModel<?> newModel = oldClpModel.getSongRemoteModel();

		newModel.setModelAttributes(oldClpModel.getModelAttributes());

		return newModel;
	}

	public static Object translateInput(Object obj) {
		if (obj instanceof BaseModel<?>) {
			return translateInput((BaseModel<?>)obj);
		}
		else if (obj instanceof List<?>) {
			return translateInput((List<Object>)obj);
		}
		else {
			return obj;
		}
	}

	public static Object translateOutput(BaseModel<?> oldModel) {
		Class<?> oldModelClass = oldModel.getClass();

		String oldModelClassName = oldModelClass.getName();

		if (oldModelClassName.equals("org.liferay.jukebox.model.impl.AlbumImpl")) {
			return translateOutputAlbum(oldModel);
		}

		if (oldModelClassName.equals(
					"org.liferay.jukebox.model.impl.ArtistImpl")) {
			return translateOutputArtist(oldModel);
		}

		if (oldModelClassName.equals("org.liferay.jukebox.model.impl.SongImpl")) {
			return translateOutputSong(oldModel);
		}

		return oldModel;
	}

	public static Object translateOutput(List<Object> oldList) {
		List<Object> newList = new ArrayList<Object>(oldList.size());

		for (int i = 0; i < oldList.size(); i++) {
			Object curObj = oldList.get(i);

			newList.add(translateOutput(curObj));
		}

		return newList;
	}

	public static Object translateOutput(Object obj) {
		if (obj instanceof BaseModel<?>) {
			return translateOutput((BaseModel<?>)obj);
		}
		else if (obj instanceof List<?>) {
			return translateOutput((List<Object>)obj);
		}
		else {
			return obj;
		}
	}

	public static Throwable translateThrowable(Throwable throwable) {
		if (_useReflectionToTranslateThrowable) {
			try {
				UnsyncByteArrayOutputStream unsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(unsyncByteArrayOutputStream);

				objectOutputStream.writeObject(throwable);

				objectOutputStream.flush();
				objectOutputStream.close();

				UnsyncByteArrayInputStream unsyncByteArrayInputStream = new UnsyncByteArrayInputStream(unsyncByteArrayOutputStream.unsafeGetByteArray(),
						0, unsyncByteArrayOutputStream.size());

				Thread currentThread = Thread.currentThread();

				ClassLoader contextClassLoader = currentThread.getContextClassLoader();

				ObjectInputStream objectInputStream = new ClassLoaderObjectInputStream(unsyncByteArrayInputStream,
						contextClassLoader);

				throwable = (Throwable)objectInputStream.readObject();

				objectInputStream.close();

				return throwable;
			}
			catch (SecurityException se) {
				if (_log.isInfoEnabled()) {
					_log.info("Do not use reflection to translate throwable");
				}

				_useReflectionToTranslateThrowable = false;
			}
			catch (Throwable throwable2) {
				_log.error(throwable2, throwable2);

				return throwable2;
			}
		}

		Class<?> clazz = throwable.getClass();

		String className = clazz.getName();

		if (className.equals(PortalException.class.getName())) {
			return new PortalException();
		}

		if (className.equals(SystemException.class.getName())) {
			return new SystemException();
		}

		if (className.equals("org.liferay.jukebox.AlbumNameException")) {
			return new org.liferay.jukebox.AlbumNameException();
		}

		if (className.equals("org.liferay.jukebox.ArtistNameException")) {
			return new org.liferay.jukebox.ArtistNameException();
		}

		if (className.equals("org.liferay.jukebox.DuplicatedAlbumException")) {
			return new org.liferay.jukebox.DuplicatedAlbumException();
		}

		if (className.equals("org.liferay.jukebox.DuplicatedArtistException")) {
			return new org.liferay.jukebox.DuplicatedArtistException();
		}

		if (className.equals("org.liferay.jukebox.DuplicatedSongException")) {
			return new org.liferay.jukebox.DuplicatedSongException();
		}

		if (className.equals("org.liferay.jukebox.NoSuchAlbumException")) {
			return new org.liferay.jukebox.NoSuchAlbumException();
		}

		if (className.equals("org.liferay.jukebox.NoSuchArtistException")) {
			return new org.liferay.jukebox.NoSuchArtistException();
		}

		if (className.equals("org.liferay.jukebox.NoSuchSongException")) {
			return new org.liferay.jukebox.NoSuchSongException();
		}

		if (className.equals("org.liferay.jukebox.SongNameException")) {
			return new org.liferay.jukebox.SongNameException();
		}

		if (className.equals("org.liferay.jukebox.NoSuchAlbumException")) {
			return new org.liferay.jukebox.NoSuchAlbumException();
		}

		if (className.equals("org.liferay.jukebox.NoSuchArtistException")) {
			return new org.liferay.jukebox.NoSuchArtistException();
		}

		if (className.equals("org.liferay.jukebox.NoSuchSongException")) {
			return new org.liferay.jukebox.NoSuchSongException();
		}

		return throwable;
	}

	public static Object translateOutputAlbum(BaseModel<?> oldModel) {
		AlbumClp newModel = new AlbumClp();

		newModel.setModelAttributes(oldModel.getModelAttributes());

		newModel.setAlbumRemoteModel(oldModel);

		return newModel;
	}

	public static Object translateOutputArtist(BaseModel<?> oldModel) {
		ArtistClp newModel = new ArtistClp();

		newModel.setModelAttributes(oldModel.getModelAttributes());

		newModel.setArtistRemoteModel(oldModel);

		return newModel;
	}

	public static Object translateOutputSong(BaseModel<?> oldModel) {
		SongClp newModel = new SongClp();

		newModel.setModelAttributes(oldModel.getModelAttributes());

		newModel.setSongRemoteModel(oldModel);

		return newModel;
	}

	private static Log _log = LogFactoryUtil.getLog(ClpSerializer.class);
	private static String _servletContextName;
	private static boolean _useReflectionToTranslateThrowable = true;
}