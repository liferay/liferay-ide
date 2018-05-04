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

import java.io.IOException;
import java.io.Writer;

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
public class CustomPropertiesConfigLayout extends PropertiesConfigurationLayout {

	public CustomPropertiesConfigLayout(PropertiesConfiguration config) {
		super(config);
	}

	public void save(Writer out) throws ConfigurationException {
		try {
			char delimiter;

			if (getConfiguration().isDelimiterParsingDisabled()) {
				delimiter = 0;
			}
			else {
				delimiter = getConfiguration().getListDelimiter();
			}

			try (PropertiesConfiguration.PropertiesWriter writer = new CustomPropertiesWriter(out, delimiter)) {
				if (getHeaderComment() != null) {
					writer.writeln(getCanonicalHeaderComment(true));
					writer.writeln(null);
				}

				for (Iterator it = getKeys().iterator(); it.hasNext();) {
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

						writer.writeProperty(key, getConfiguration().getProperty(key), singleLine);
					}
				}

				writer.flush();
			}
		}
		catch (IOException ioe) {
			throw new ConfigurationException(ioe);
		}
	}

	public static class CustomPropertiesWriter extends PropertiesWriter {

		public CustomPropertiesWriter(Writer writer, char delimiter) {
			super(writer, delimiter);

			_delimiter = delimiter;
		}

		public void writeProperty(String key, Object value, boolean forceSingleLine) throws IOException {
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
			else {
				v = _escapeValue(value);
			}

			write(_escapeKey(key));
			write("=");
			write(v);

			writeln(null);
		}

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
			if (!values.isEmpty()) {
				Iterator it = values.iterator();

				String lastValue = _escapeValue(it.next());

				StringBuffer buf = new StringBuffer(lastValue);

				while (it.hasNext()) {

					/*
					 * if the last value ended with an escape character, it has
					 * to be escaped itself; otherwise the list delimiter will
					 * be escaped
					 */
					if (lastValue.endsWith(_ESCAPE)) {
						StringBuffer bf = buf.append(_ESCAPE);

						bf.append(_ESCAPE);
					}

					buf.append(_delimiter);

					lastValue = _escapeValue(it.next());

					buf.append(lastValue);
				}

				return buf.toString();
			}
			else {
				return null;
			}
		}

		private char _delimiter;

	}

	private static final String _ESCAPE = "\\";

	private static final char[] _SEPARATORS = {'=', ':'};

	//The white space characters used as key/value separators.
	private static final char[] _WHITE_SPACE = {' ', '\t', '\f'};

}