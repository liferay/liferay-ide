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

package com.liferay.ide.server.util;

import com.liferay.ide.core.util.FileUtil;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author Brian Stansberry
 */
public class LayeredModulePathFactory {

	/**
	 * Load the overlays for each layer.
	 *
	 * @param layeringRoot the layer root
	 * @param path the module path
	 */
	public static void loadOverlays(File layeringRoot, List<File> path) {
		File overlays = new File(layeringRoot, _OVERLAYS);

		if (FileUtil.exists(overlays)) {
			File refs = new File(overlays, _OVERLAYS);

			if (FileUtil.exists(refs)) {
				try {
					for (String overlay : readRefs(refs)) {
						File root = new File(overlays, overlay);

						path.add(root);
					}
				}
				catch (IOException ioe) {
					throw new RuntimeException(ioe);
				}
			}
		}

		path.add(layeringRoot);
	}

	public static boolean readLine(InputStream is, StringBuffer buffer) throws IOException {
		buffer.setLength(0);
		int c;

		while (true) {
			c = is.read();

			switch (c) {
				case '\t':
				case '\r':

					break;
				case -1: return false;
				case '\n': return true;
				default: buffer.append((char)c);
			}
		}
	}

	public static List<String> readRefs(File file) throws IOException {
		if (FileUtil.notExists(file)) {
			return Collections.emptyList();
		}

		InputStream is = Files.newInputStream(file.toPath());

		try {
			return readRefs(is);
		}
		finally {
			if (is != null) {
				try {
					is.close();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public static List<String> readRefs(InputStream is) throws IOException {
		List<String> refs = new ArrayList<>();
		StringBuffer buffer = new StringBuffer();

		do {
			if (buffer.length() > 0) {
				String bs = buffer.toString();

				String ref = bs.trim();

				if (ref.length() > 0) {
					refs.add(ref);
				}
			}
		} while (readLine(is, buffer));

		return refs;
	}

	/**
	 * Inspects each element in the given {@code modulePath} to see if it includes a {@code layers.conf} file
	 * and/or a standard directory structure with child directories {@code system/layers} and, optionally,
	 * {@code system/add-ons}. If so, the layers identified in {@code layers.conf} are added to the module path
	 *
	 * @param modulePath the filesystem locations that make up the standard module path, each of which is to be
	 *                   checked for the presence of layers and add-ons
	 *
	 * @return a new module path, including any layers and add-ons, if found
	 */
	public static File[] resolveLayeredModulePath(File... modulePath) {
		boolean foundLayers = false;
		List<File> layeredPath = new ArrayList<>();

		for (File file : modulePath) {

			// Always add the root, as the user may place modules directly in it

			layeredPath.add(file);

			LayersConfig layersConfig = _getLayersConfig(file);

			File layersDir = new File(file, layersConfig._getLayersPath());

			if (FileUtil.notExists(layersDir)) {
				if (layersConfig._isConfigured()) {

					// Bad config from user

					throw new IllegalStateException("No layers directory found at " + layersDir);
				}

				// else this isn't a root that has layers and add-ons

				continue;
			}

			boolean validLayers = true;
			List<File> layerFiles = new ArrayList<>();

			for (String layerName : layersConfig._getLayers()) {
				File layer = new File(layersDir, layerName);

				if (FileUtil.notExists(layer)) {
					if (layersConfig._isConfigured()) {

						// Bad config from user

						throw new IllegalStateException(
							String.format("Cannot find layer %s under directory %s", layerName, layersDir));
					}

					// else this isn't a standard layers and add-ons structure

					validLayers = false;

					break;
				}

				loadOverlays(layer, layerFiles);
			}

			if (validLayers) {
				foundLayers = true;
				layeredPath.addAll(layerFiles);

				// Now add-ons

				File[] addOns = new File(file, layersConfig._getAddOnsPath()).listFiles();

				if (addOns != null) {
					for (File addOn : addOns) {
						if (addOn.isDirectory()) {
							loadOverlays(addOn, layeredPath);
						}
					}
				}
			}
		}

		if (foundLayers) {
			return layeredPath.toArray(new File[layeredPath.size()]);
		}

		return modulePath;
	}

	public static void safeClose(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			}
			catch (Throwable ignored) {
			}
		}
	}

	private static LayersConfig _getLayersConfig(File repoRoot) {
		File layersList = new File(repoRoot, "layers.conf");

		if (FileUtil.notExists(layersList)) {
			return new LayersConfig();
		}

		Reader reader = null;

		try {
			reader = new InputStreamReader(Files.newInputStream(layersList.toPath()), "UTF-8");

			Properties props = new Properties();

			props.load(reader);

			return new LayersConfig(props);
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		finally {
			safeClose(reader);
		}
	}

	private static final String _OVERLAYS = ".overlays";

	private static class LayersConfig {

		private LayersConfig() {
			_configured = false;
			_layersPath = _DEFAULT_LAYERS_PATH;
			_addOnsPath = _DEFAULT_ADD_ONS_PATH;
			_layers = Collections.singletonList("base");
		}

		private LayersConfig(Properties properties) {
			_configured = true;
			_layersPath = _DEFAULT_LAYERS_PATH;
			_addOnsPath = _DEFAULT_ADD_ONS_PATH;
			boolean excludeBase = false;
			String layersProp = (String)properties.get("layers");

			if ((layersProp == null) || (layersProp = layersProp.trim()).length() == 0) {
				if (excludeBase) {
					_layers = Collections.emptyList();
				}
				else {
					_layers = Collections.singletonList("base");
				}
			}
			else {
				String[] layerNames = layersProp.split(",");
				_layers = new ArrayList<>();
				boolean hasBase = false;

				for (String layerName : layerNames) {
					if ("base".equals(layerName)) {
						hasBase = true;
					}

					_layers.add(layerName);
				}

				if (!hasBase && !excludeBase) {
					_layers.add("base");
				}
			}
		}

		private String _getAddOnsPath() {
			return _addOnsPath;
		}

		private List<String> _getLayers() {
			return _layers;
		}

		private String _getLayersPath() {
			return _layersPath;
		}

		private boolean _isConfigured() {
			return _configured;
		}

		private static final String _DEFAULT_ADD_ONS_PATH = "system/add-ons";

		private static final String _DEFAULT_LAYERS_PATH = "system/layers";

		private final String _addOnsPath;
		private final boolean _configured;
		private final List<String> _layers;
		private final String _layersPath;

	}

}