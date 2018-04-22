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

package com.liferay.ide.core.properties;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;

import java.io.IOException;
import java.io.Writer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration.PropertiesWriter;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("rawtypes")
public class PortalPropertiesConfigurationLayout extends PropertiesConfigurationLayout {

	public static final String[] sortedKeys = {
		"name", "module-group-id", "module-incremental-version", "tags", "short-description", "change-log", "page-url",
		"author", "licenses", "portal-dependency-jars", "portal-dependency-tlds"
	};

	public PortalPropertiesConfigurationLayout(PropertiesConfiguration config) {
		super(config);

		setForceSingleLine(true);
	}

	public boolean isWrappedProperty(String key) {
		return false;
	}

	public void save(Writer out) throws ConfigurationException {
		try {
			char delimiter =
				getConfiguration().isDelimiterParsingDisabled() ? 0 : getConfiguration().getListDelimiter();

			PluginPropertiesWriter writer = new PluginPropertiesWriter(out, delimiter);

			if (getHeaderComment() != null) {
				writer.writeln(getCanonicalHeaderComment(true));
				writer.writeln(null);
			}

			List<Object> keyList = Arrays.asList(getKeys().toArray());

			Collections.sort(
				keyList,
				new Comparator<Object>() {

					public int compare(Object o1, Object o2) {
						int index1 = Integer.MAX_VALUE;
						int index2 = Integer.MAX_VALUE;

						for (int i = 0; i < sortedKeys.length; i++) {
							if (sortedKeys[i].equals(o1)) {
								index1 = i;
							}

							if (sortedKeys[i].equals(o2)) {
								index2 = i;
							}
						}

						if (index1 < index2) {
							return -1;
						}
						else if (index1 > index2) {
							return 1;
						}

						return 0;
					}

				});

			for (Iterator it = keyList.iterator(); it.hasNext();) {
				String key = (String)it.next();

				if (getConfiguration().containsKey(key)) {

					// Output blank lines before property

					for (int i = 0; i < getBlancLinesBefore(key); i++) {
						writer.writeln(null);
					}

					// Output the comment

					if (getComment(key) != null) {
						writer.writeln(getCanonicalComment(key, true));
					}

					// Output the property and its value

					boolean singleLine = false;

					if ((isForceSingleLine() || isSingleLine(key)) &&
						!getConfiguration().isDelimiterParsingDisabled()) {

						singleLine = true;
					}

					boolean wrappedProperty = isWrappedProperty(key);

					writer.writeProperty(key, getConfiguration().getProperty(key), singleLine, wrappedProperty);
				}
			}

			writer.flush();
			writer.close();
		}
		catch (IOException ioe) {
			throw new ConfigurationException(ioe);
		}
	}

	public static class PluginPropertiesWriter extends PropertiesWriter {

		// Constant for the escaping character.

		public PluginPropertiesWriter(Writer writer, char delimiter) {
			super(writer, delimiter);

			_delimiter = delimiter;
		}

		// The list of possible key/value separators

		public void writeProperty(String key, Object value, boolean forceSingleLine, boolean wrappedProperty)
			throws IOException {

			String v;

			if (value instanceof List) {
				List values = (List)value;

				if (forceSingleLine) {
					v = _makeSingleLineValue(values);
				}
				else {
					writeProperty(key, values);
					return;
				}
			}
			else if (wrappedProperty) {
				String[] values = value.toString().split(StringPool.COMMA);

				if (values.length == 1) {
					v = _escapeValue(values[0]);
				}
				else {
					StringBuffer buf = new StringBuffer();

					for (String val : values) {
						if (CoreUtil.isNullOrEmpty(buf.toString())) {
							buf.append("\\\n");
							buf.append("    " + _escapeValue(val));
						}
						else {
							buf.append(",\\\n    " + _escapeValue(val));
						}
					}

					v = buf.toString();
				}
			}
			else {
				v = _escapeValue(value);
			}

			write(_escapeKey(key));
			write(StringPool.EQUALS);
			write(v);

			writeln(null);
		}

		// The white space characters used as key/value separators.

		private String _escapeKey(String key) {
			StringBuffer newkey = new StringBuffer();

			for (int i = 0; i < key.length(); i++) {
				char c = key.charAt(i);

				if (ArrayUtils.contains(_SEPARATORS, c) || ArrayUtils.contains(_WHITE_SPACE, c)) {

					// escape the separator

					newkey.append('\\');
					newkey.append(c);
				}
				else {
					newkey.append(c);
				}
			}

			return newkey.toString();
		}

		private String _escapeValue(Object value) {
			String escapedValue = StringEscapeUtils.escapeJava(String.valueOf(value));

			if (_delimiter != 0) {
				escapedValue = StringUtils.replace(escapedValue, String.valueOf(_delimiter), _ESCAPE + _delimiter);
			}

			return escapedValue;
		}

		private String _makeSingleLineValue(List values) {
			if (ListUtil.isEmpty(values)) {
				return null;
			}

			Iterator it = values.iterator();

			String lastValue = _escapeValue(it.next());

			StringBuffer buf = new StringBuffer(lastValue);

			while (it.hasNext()) {
				/*
				 * if the last value ended with an escape character, it has to be escaped
				 * itself; otherwise the list delimiter will be escaped
				 */
				if (lastValue.endsWith(_ESCAPE)) {
					buf.append(_ESCAPE).append(_ESCAPE);
				}

				buf.append(_delimiter);

				lastValue = _escapeValue(it.next());

				buf.append(lastValue);
			}

			return buf.toString();
		}

		private static final String _ESCAPE = "\\";

		private static final char[] _SEPARATORS = {'=', ':'};

		private static final char[] _WHITE_SPACE = {' ', '\t', '\f'};

		private char _delimiter;

	}

}