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

package com.liferay.ide.project.core.modules.templates;

import com.liferay.ide.core.util.CoreUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/**
 * @author Simon Jiang
 */
public class BndProperties extends Properties {

	public static final long serialVersionUID = 1L;

	public void addKeyList(String key) {
		if (_keyList.contains(key)) {
			return;
		}

		_keyList.add(key);
	}

	public void addValue(String key, BndPropertiesValue newBdValue) {
		BndPropertiesValue bdValue = (BndPropertiesValue)get(key);

		addKeyList(key);

		if (bdValue != null) {
			String originalValue = bdValue.getOriginalValue();

			if ((originalValue != null) && !CoreUtil.isNullOrEmpty(originalValue)) {
				StringBuilder formatedValueBuilder = new StringBuilder(bdValue.getFormatedValue());
				StringBuilder originalValueBuilder = new StringBuilder(bdValue.getOriginalValue());

				String newOriginalValue = newBdValue.getOriginalValue();

				String[] newOriginalValues = newOriginalValue.split(",");

				BndPropertiesValue inputValue = new BndPropertiesValue();

				for (String newValue : newOriginalValues) {
					if (originalValue.contains(newValue)) {
						continue;
					}

					if (bdValue.isMultiLine()) {
						formatedValueBuilder.append(",\\");
						formatedValueBuilder.append(System.getProperty("line.separator"));
						formatedValueBuilder.append("\t");
						formatedValueBuilder.append(newValue);

						inputValue.setFormatedValue(formatedValueBuilder.toString());
					}
					else {
						originalValueBuilder.append(",");
						originalValueBuilder.append(newValue);

						inputValue.setFormatedValue(originalValueBuilder.toString());
					}
				}

				put(key, inputValue);
			}
			else {
				put(key, newBdValue);
			}
		}
		else {
			put(key, newBdValue);
		}
	}

	public String getPropertyValue(String key) {
		BndPropertiesValue bndPropertiesValue = (BndPropertiesValue)get(key);

		return bndPropertiesValue.getFormatedValue();
	}

	public String getReader(Reader a) throws IOException {
		StringWriter sw = new StringWriter();

		char[] buffer = new char[_page_size];

		int size = a.read(buffer);

		while (size > 0) {
			sw.write(buffer, 0, size);

			size = a.read(buffer);
		}

		return sw.toString();
	}

	public void load(File bndFile) throws IOException {
		try (InputStream in = Files.newInputStream(bndFile.toPath())) {
			load(in);
		}
		catch (Exception e) {
		}
	}

	@Override
	public void load(InputStream inStream) throws IOException {

		// The spec says that the file must be encoded using ISO-8859-1.

		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "ISO-8859-1"));

		String buffer;

		while ((buffer = reader.readLine()) != null) {
			String line = _convert(buffer.getBytes(), _utf8);

			BndPropertiesValue bnd = new BndPropertiesValue();

			char c = 0;

			int pos = 0;

			// Leading whitespaces must be deleted first.

			while ((pos < line.length()) && Character.isWhitespace(c = line.charAt(pos))) {
				pos++;
			}

			// If empty line or begins with a comment character, skip this line.

			if (((line.length() - pos) == 0) || (line.charAt(pos) == '#') || (line.charAt(pos) == '!')) {
				continue;
			}

			/*
			 *  The characters up to the next Whitespace, ':', or '=' describe the key.
			 *  But look for escape sequences. Try to short-circuit when there is no escape char.
			 */
			int start = pos;

			boolean needsEscape = false;

			if (line.indexOf('\\', pos) != -1) {
				needsEscape = true;
			}

			StringBuilder key = needsEscape ? new StringBuilder() : null;

			while ((pos < line.length()) && !Character.isWhitespace(c = line.charAt(pos++)) && (c != '=') &&
				   (c != ':')) {

				if (needsEscape && (c == '\\')) {
					if (pos == line.length()) {

						/*
						 * The line continues on the next line.
						 * If there is no next line, just treat it as a key with an empty value.
						 */
						line = reader.readLine();

						if (line == null) {
							line = "";
						}

						pos = 0;

						while ((pos < line.length()) && Character.isWhitespace(c = line.charAt(pos))) {
							pos++;
						}
					}
					else {
						c = line.charAt(pos++);

						switch (c) {
							case 'n':
								key.append('\n');

								break;
							case 't':
								key.append('\t');

								break;
							case 'r':
								key.append('\r');

								break;
							case 'u':
								if ((pos + 4) <= line.length()) {
									char uni = (char)Integer.parseInt(line.substring(pos, pos + 4), 16);

									key.append(uni);

									pos += 4;
								}
								else {

									// throw exception?

								}

								break;
							default:
								key.append(c);

								break;
						}
					}
				}
				else if (needsEscape) {
					key.append(c);
				}
			}

			boolean delim = false;

			if ((c == ':') || (c == '=')) {
				delim = true;
			}

			String keyString;

			if (needsEscape) {
				keyString = key.toString();
			}
			else if (delim || Character.isWhitespace(c)) {
				keyString = line.substring(start, pos - 1);
			}
			else {
				keyString = line.substring(start, pos);
			}

			while ((pos < line.length()) && Character.isWhitespace(c = line.charAt(pos))) {
				pos++;
			}

			if (!delim && ((c == ':') || (c == '='))) {
				pos++;

				while ((pos < line.length()) && Character.isWhitespace(c = line.charAt(pos))) {
					pos++;
				}
			}

			// Short-circuit if no escape chars found.

			if (!needsEscape) {
				bnd.setOriginalValue(line.substring(pos));

				bnd.setFormatedValue(line.substring(pos));

				addKeyList(keyString);

				put(keyString, bnd);

				continue;
			}

			// Escape char found so iterate through the rest of the line.

			StringBuilder element = new StringBuilder(line.length() - pos);
			StringBuilder formatedElement = new StringBuilder(line.substring(pos));

			// formatedElement.append( line );

			while (pos < line.length()) {
				c = line.charAt(pos++);

				if (c == '\\') {
					if (pos == line.length()) {
						bnd.setMultiLine(true);
						formatedElement.append(System.getProperty("line.separator"));

						// The line continues on the next line.

						line = reader.readLine();

						formatedElement.append(line);

						/*
						 *  We might have seen a backslash at the end of the file.
						 *  The JDK ignores the backslash in this case, so we follow for compatibility.
						 */
						if (line == null) {
							break;
						}

						pos = 0;

						while ((pos < line.length()) && Character.isWhitespace(c = line.charAt(pos))) {
							pos++;
						}

						element.ensureCapacity(line.length() - pos + element.length());
					}
					else {
						c = line.charAt(pos++);

						switch (c) {
							case 'n':
								element.append('\n');

								formatedElement.append('\n');

								break;
							case 't':
								element.append('\t');

								formatedElement.append('\t');

								break;
							case 'r':
								element.append('\r');

								formatedElement.append('\r');

								break;
							case 'u':
								if ((pos + 4) <= line.length()) {
									char uni = (char)Integer.parseInt(line.substring(pos, pos + 4), 16);

									element.append(uni);

									pos += 4;
								}
								else {

									// throw exception?

								}

								break;
							default:
								element.append(c);

								break;
						}
					}
				}
				else {
					element.append(c);
				}
			}

			bnd.setOriginalValue(element.toString());
			bnd.setFormatedValue(formatedElement.toString());
			addKeyList(keyString);
			put(keyString, bnd);
		}
	}

	@Override
	public void store(OutputStream out, String header) throws IOException {
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));

		if (header != null) {
			writer.println("#" + header);
			writer.println("#" + Calendar.getInstance().getTime());
		}

		// Reuse the same buffer.

		StringBuilder s = new StringBuilder();

		for (String keyString : _keyList) {
			_formatForOutput((String)keyString, s, true);
			s.append(": ");

			Object value = get(keyString);

			if (value instanceof BndPropertiesValue) {
				BndPropertiesValue bndValue = (BndPropertiesValue)value;

				writer.println(s.append(bndValue.getFormatedValue()));
			}
			else {
				_formatForOutput((String)value, s, false);
				writer.println(s);
			}
		}

		writer.flush();
	}

	private String _convert(byte[] buffer, Charset charset) throws IOException {
		CharsetDecoder decoder = charset.newDecoder();
		ByteBuffer bb = ByteBuffer.wrap(buffer);
		CharBuffer cb = CharBuffer.allocate(buffer.length * 4);

		CoderResult result = decoder.decode(bb, cb, true);

		if (!result.isError()) {
			return new String(cb.array(), 0, cb.position());
		}

		throw new CharacterCodingException();
	}

	private void _formatForOutput(String str, StringBuilder buffer, boolean key) {
		if (key) {
			buffer.setLength(0);
			buffer.ensureCapacity(str.length());
		}
		else {
			buffer.ensureCapacity(buffer.length() + str.length());
		}

		boolean head = true;
		int size = str.length();

		for (int i = 0; i < size; i++) {
			char c = str.charAt(i);

			switch (c) {
				case '\n':
					buffer.append("\\n");

					break;
				case '\r':
					buffer.append("\\r");

					break;
				case '\t':
					buffer.append("\\t");

					break;
				case ' ':
					buffer.append(head ? "\\ " : " ");

					break;
				case '!':
				case '#':
					buffer.append('\\').append(c);

					break;
				default:
					if ((c < ' ') || (c > '~')) {
						String hex = Integer.toHexString(c);

						buffer.append("\\u0000".substring(0, 6 - hex.length()));
						buffer.append(hex);
					}
					else {
						buffer.append(c);
					}
			}

			if (c != ' ') {
				head = key;
			}
		}
	}

	private final List<String> _keyList = new ArrayList<>();
	private final int _page_size = 4096;
	private final Charset _utf8 = Charset.forName("UTF-8");

}