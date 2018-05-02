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

package com.liferay.ide.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.PropertiesUtil;

import java.lang.ref.WeakReference;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;

/**
 * @author Kuo Zhang
 */
public class LiferayLanguagePropertiesValidator {

	public static final String ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT = "language-properties-encoding-not-defalut";

	public static final String LIFERAY_LANGUAGE_PROPERTIES_MARKER_TYPE =
		"com.liferay.ide.core.LiferayLanguagePropertiesMarker";

	public static final String LOCATION_ENCODING = "Properties/Resource/Text file encoding";

	public static final String MESSAGE_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFALUT =
		Msgs.languagePropertiesEncodingNotDefault;

	/**
	 * This is for the case where the workspace is closed accidently, clear those alive but incorrect markers.
	 */
	public static void clearAbandonedMarkers() {
		try {
			IMarker[] markers = CoreUtil.getWorkspaceRoot().findMarkers(
				LIFERAY_LANGUAGE_PROPERTIES_MARKER_TYPE, true, IResource.DEPTH_INFINITE);

			for (IMarker marker : markers) {
				if (FileUtil.notExists(marker.getResource())) {
					marker.delete();
				}
				else {
					if (marker.getResource().getType() == IResource.FILE) {
						getValidator((IFile)marker.getResource()).validateEncoding();
					}
				}
			}
		}
		catch (CoreException ce) {
		}
	}

	public static void clearUnusedValidatorsAndMarkers(IProject project) throws CoreException {
		synchronized (_filesAndValidators) {
			Set<IFile> files = _filesAndValidators.keySet();

			for (Iterator<IFile> iterator = files.iterator(); iterator.hasNext();) {
				IFile file = iterator.next();

				if (!PropertiesUtil.isLanguagePropertiesFile(file)) {
					iterator.remove();
				}
			}

			IWorkspaceRoot root = project.getWorkspace().getRoot();

			IMarker[] markers = root.findMarkers(
				LIFERAY_LANGUAGE_PROPERTIES_MARKER_TYPE, true, IResource.DEPTH_INFINITE);

			for (IMarker marker : markers) {
				if (FileUtil.notExists(marker.getResource())) {
					marker.delete();
				}
				else {
					if (marker.getResource().getType() == IResource.FILE) {
						if (!files.contains((IFile)marker.getResource())) {
							marker.delete();
						}
					}
				}
			}
		}
	}

	public static LiferayLanguagePropertiesValidator getValidator(IFile file) {
		synchronized (_filesAndValidators) {
			try {
				if (_filesAndValidators.get(file).get() != null) {
					return _filesAndValidators.get(file).get();
				}
				else {
					throw new NullPointerException();
				}
			}
			catch (NullPointerException npe) {
				LiferayLanguagePropertiesValidator validator = new LiferayLanguagePropertiesValidator(file);

				_filesAndValidators.put(file, new WeakReference<LiferayLanguagePropertiesValidator>(validator));

				return validator;
			}
		}
	}

	public void validateEncoding() {
		if (!PropertiesUtil.isLanguagePropertiesFile(_file)) {
			_clearMarker(ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT);

			return;
		}

		try {
			if (ILiferayConstants.LANGUAGE_PROPERTIES_FILE_ENCODING_CHARSET.equals(_file.getCharset())) {
				_clearMarker(ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT);

				return;
			}

			_setMarker(
				LIFERAY_LANGUAGE_PROPERTIES_MARKER_TYPE, ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT,
				IMarker.SEVERITY_WARNING, LOCATION_ENCODING,
				NLS.bind(MESSAGE_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFALUT, new Object[] {_file.getName()}));
		}
		catch (Exception e) {
			LiferayCore.logError(e);
		}
	}

	private LiferayLanguagePropertiesValidator(IFile file) {
		_file = file;

		if (FileUtil.exists(file)) {
			try {
				IMarker[] markers = file.findMarkers(
					LIFERAY_LANGUAGE_PROPERTIES_MARKER_TYPE, false, IResource.DEPTH_INFINITE);

				for (IMarker marker : markers) {
					if (ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT.equals(marker.getAttribute(IMarker.SOURCE_ID))) {
						_markers.add(marker);
					}
				}
			}
			catch (CoreException ce) {
				LiferayCore.logError(ce);
			}
		}
	}

	private void _clearMarker(String markerSourceId) {
		try {
			synchronized (_markers) {
				for (IMarker marker : _markers) {
					if ((marker != null) && marker.exists() &&
						markerSourceId.equals(marker.getAttribute(IMarker.SOURCE_ID))) {

						_markers.remove(marker);

						marker.delete();
					}
				}
			}
		}
		catch (CoreException ce) {
			LiferayCore.logError(ce);
		}
	}

	private void _setMarker(
			String markerType, String markerSourceId, int markerSeverity, String location, String markerMsg)
		throws CoreException, InterruptedException {

		synchronized (_markers) {
			for (IMarker marker : _markers) {
				if ((marker != null) && marker.exists() &&
					markerSourceId.equals(marker.getAttribute(IMarker.SOURCE_ID))) {

					return;
				}
			}

			IMarker marker = _file.createMarker(markerType);

			marker.setAttribute(IMarker.SEVERITY, markerSeverity);
			marker.setAttribute(IMarker.MESSAGE, markerMsg);
			marker.setAttribute(IMarker.SOURCE_ID, markerSourceId);
			marker.setAttribute(IMarker.LOCATION, location);

			_markers.add(marker);
		}
	}

	private static WeakHashMap<IFile, WeakReference<LiferayLanguagePropertiesValidator>> _filesAndValidators =
		new WeakHashMap<>();

	private IFile _file;
	private Set<IMarker> _markers = new HashSet<>();

	private static class Msgs extends NLS {

		public static String languagePropertiesEncodingNotDefault;

		static {
			initializeMessages(LiferayLanguagePropertiesValidator.class.getName(), Msgs.class);
		}

	}

}