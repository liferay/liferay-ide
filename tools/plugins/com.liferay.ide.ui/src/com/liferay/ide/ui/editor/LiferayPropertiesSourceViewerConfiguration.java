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

package com.liferay.ide.ui.editor;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.properties.PortalPropertiesConfiguration;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.LiferayUIPlugin;
import com.liferay.ide.ui.editor.LiferayPropertiesContentAssistProcessor.PropKey;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfigurationLayout;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.IPropertiesFilePartitions;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileSourceViewerConfiguration;
import org.eclipse.jface.internal.text.html.HTMLTextPresenter;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class LiferayPropertiesSourceViewerConfiguration extends PropertiesFileSourceViewerConfiguration {

	public LiferayPropertiesSourceViewerConfiguration(ITextEditor editor) {
		super(
			JavaPlugin.getDefault().getJavaTextTools().getColorManager(),
			JavaPlugin.getDefault().getCombinedPreferenceStore(), editor,
			IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING);
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		if (_propKeys == null) {
			IEditorInput input = getEditor().getEditorInput();

			// first fine runtime location to get properties definitions

			IPath appServerPortalDir = _getAppServerPortalDir(input);
			String propertiesEntry = _getPropertiesEntry(input);

			PropKey[] keys = null;

			if (FileUtil.exists(appServerPortalDir)) {
				IPath portalImplPath = appServerPortalDir.append("WEB-INF/lib/portal-impl.jar");

				if (FileUtil.exists(portalImplPath)) {
					try (JarFile jar = new JarFile(portalImplPath.toFile())) {
						ZipEntry lang = jar.getEntry(propertiesEntry);

						keys = _parseKeys(jar.getInputStream(lang));
					}
					catch (Exception e) {
						LiferayUIPlugin.logError("Unable to get portal properties file", e);
					}
				}
			}
			else {
				return _assitant;
			}

			Object adapter = input.getAdapter(IFile.class);

			if (adapter instanceof IFile && _isHookProject(((IFile)adapter).getProject())) {
				ILiferayProject liferayProject = LiferayCore.create(
					ILiferayProject.class, ((IFile)adapter).getProject());

				ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

				if (portal != null) {
					Set<String> hookProps = new HashSet<>();

					Collections.addAll(hookProps, portal.getHookSupportedProperties());

					List<PropKey> filtered = new ArrayList<>();

					for (PropKey pk : keys) {
						if (hookProps.contains(pk.getKey())) {
							filtered.add(pk);
						}
					}

					keys = filtered.toArray(new PropKey[0]);
				}
			}

			_propKeys = keys;
		}

		if (ListUtil.isNotEmpty(_propKeys) && (_assitant == null)) {
			ContentAssistant ca = new ContentAssistant() {

				@Override
				public IContentAssistProcessor getContentAssistProcessor(String contentType) {
					return new LiferayPropertiesContentAssistProcessor(_propKeys, contentType);
				}

			};

			ca.setInformationControlCreator(getInformationControlCreator(sourceViewer));

			_assitant = ca;
		}

		return _assitant;
	}

	@Override
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {

			@Override
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, new HTMLTextPresenter(true));
			}

		};
	}

	private IPath _getAppServerPortalDir(IEditorInput input) {
		IPath retval = null;

		IFile iFile = input.getAdapter(IFile.class);

		if (iFile != null) {
			ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, iFile.getProject());

			if (liferayProject != null) {
				ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

				if (portal != null) {
					retval = portal.getAppServerPortalDir();
				}
			}
		}
		else {
			File file = input.getAdapter(File.class);

			if ((file == null) && input instanceof FileStoreEditorInput) {
				FileStoreEditorInput fInput = (FileStoreEditorInput)input;

				URI uri = fInput.getURI();

				file = new File(uri.getPath());
			}

			if (FileUtil.exists(file)) {
				try {
					File parent = file.getParentFile();

					IPath propsParentPath = new Path(parent.getCanonicalPath());

					if (propsParentPath != null) {
						for (IRuntime runtime : ServerCore.getRuntimes()) {
							if (propsParentPath.equals(runtime.getLocation()) ||
								propsParentPath.isPrefixOf(runtime.getLocation())) {

								ILiferayRuntime lr = ServerUtil.getLiferayRuntime(runtime);

								retval = lr.getAppServerPortalDir();

								break;
							}
						}
					}
				}
				catch (Exception e) {
					LiferayUIPlugin.logError("Unable to get portal language properties file", e);
				}
			}
		}

		return retval;
	}

	private String _getPropertiesEntry(IEditorInput input) {
		String retval = null;

		if ("system-ext.properties".equals(input.getName())) {
			retval = "system.properties";
		}
		else {
			retval = "portal.properties";
		}

		return retval;
	}

	private boolean _isHookProject(IProject project) {
		IWebProject webProject = LiferayCore.create(IWebProject.class, project);

		if ((webProject != null) && (webProject.getDescriptorFile(ILiferayConstants.LIFERAY_HOOK_XML_FILE) != null)) {
			return true;
		}

		return false;
	}

	private PropKey[] _parseKeys(InputStream inputStream) throws ConfigurationException, IOException {
		List<PropKey> parsed = new ArrayList<>();

		PortalPropertiesConfiguration config = new PortalPropertiesConfiguration();

		try {
			config.load(inputStream);
		}
		finally {
			inputStream.close();
		}

		Iterator<?> keys = config.getKeys();
		PropertiesConfigurationLayout layout = config.getLayout();

		while (keys.hasNext()) {
			Object o = keys.next();

			String key = o.toString();

			String comment = layout.getComment(key);

			parsed.add(new PropKey(key, comment == null ? null : comment.replaceAll("\n", "\n<br/>")));
		}

		PropKey[] parsedKeys = parsed.toArray(new PropKey[0]);

		Arrays.sort(
			parsedKeys,
			new Comparator<PropKey>() {

				@Override
				public int compare(PropKey o1, PropKey o2) {
					String o1key = o1.getKey();

					return o1key.compareTo(o2.getKey());
				}

			});

		return parsedKeys;
	}

	private IContentAssistant _assitant;
	private PropKey[] _propKeys;

}