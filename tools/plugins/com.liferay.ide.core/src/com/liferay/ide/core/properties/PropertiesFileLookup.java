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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gregory Amerson
 *
 *         Most of the code in this file was copied from java.util.Properties
 *         class so it is not properly formatted
 */
public class PropertiesFileLookup {

	public PropertiesFileLookup(InputStream input) {
		try {
			_parse(input, null, false);
		}
		catch (IOException ioe) {
		}
		finally {
			try {
				input.close();
			}
			catch (IOException ioe) {
			}
		}
	}

	public PropertiesFileLookup(InputStream input, String initialLookup, boolean loadValues) {
		try {
			_parse(input, initialLookup, loadValues);
		}
		catch (IOException ioe) {
		}
		finally {
			try {
				input.close();
			}
			catch (IOException ioe) {
			}
		}
	}

	public KeyInfo getKeyInfo(String keyValue) {
		return lookup.get(keyValue);
	}

	public final Map<String, KeyInfo> lookup = new HashMap<>();

	public static class KeyInfo {

		public KeyInfo(int offset, int length) {
			this.offset = offset;
			this.length = length;
		}

		public final int length;
		public final int offset;
		public String value;

	}

	/**
	 * copied from java.util.properties$LineReader
	 */
	public class LineReader {

		public LineReader(Reader reader) {
			this.reader = reader;
			inCharBuf = new char[8192];
		}

		public int[] readLine() throws IOException {
			int len = 0;
			char c = 0;

			boolean skipWhiteSpace = true;
			boolean commentLine = false;
			boolean newLine = true;
			boolean appendedLineBegin = false;
			boolean precedingBackslash = false;
			boolean skipLF = false;

			while (true) {
				if (inOff >= inLimit) {
					inLimit = reader.read(inCharBuf);

					inOff = 0;

					if (inLimit <= 0) {
						if ((len == 0) || commentLine) {
							return new int[] {-1, total};
						}

						return new int[] {len, total};
					}
				}

				c = inCharBuf[inOff++];

				total++;

				if (skipLF) {
					skipLF = false;

					if (c == '\n') {
						continue;
					}
				}

				if (skipWhiteSpace) {
					if ((c == ' ') || (c == '\t') || (c == '\f')) {
						continue;
					}

					if (!appendedLineBegin && ((c == '\r') || (c == '\n'))) {
						continue;
					}

					skipWhiteSpace = false;

					appendedLineBegin = false;
				}

				if (newLine) {
					newLine = false;

					if ((c == '#') || (c == '!')) {
						commentLine = true;

						continue;
					}
				}

				if ((c != '\n') && (c != '\r')) {
					lineBuf[len++] = c;

					if (len == lineBuf.length) {
						int newLength = lineBuf.length * 2;

						if (newLength < 0) {
							newLength = Integer.MAX_VALUE;
						}

						char[] buf = new char[newLength];

						System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);

						lineBuf = buf;
					}

					// flip the preceding backslash flag

					if (c == '\\') {
						precedingBackslash = !precedingBackslash;
					}
					else {
						precedingBackslash = false;
					}
				}
				else {

					// reached EOL

					if (commentLine || (len == 0)) {
						commentLine = false;
						newLine = true;
						skipWhiteSpace = true;
						len = 0;

						continue;
					}

					if (inOff >= inLimit) {
						inLimit = reader.read(inCharBuf);

						inOff = 0;

						if (inLimit <= 0) {
							return new int[] {len, total};
						}
					}

					if (precedingBackslash) {
						len -= 1;

						// skip the leading whitespace characters in following line

						skipWhiteSpace = true;
						appendedLineBegin = true;
						precedingBackslash = false;

						if (c == '\r') {
							skipLF = true;
						}
					}
					else {
						return new int[] {len, total};
					}
				}
			}
		}

		public char[] inCharBuf;
		public int inLimit = 0;
		public int inOff = 0;
		public char[] lineBuf = new char[1024];
		public Reader reader;
		public int total = 0;

	}

	/**
	 * Copied from java.util.Properties
	 */
	private String _loadConvert(char[] in, int off, int len, char[] convtBuf) {
		if (convtBuf.length < len) {
			int newLen = len * 2;

			if (newLen < 0) {
				newLen = Integer.MAX_VALUE;
			}

			convtBuf = new char[newLen];
		}

		char aChar;
		char[] out = convtBuf;
		int outLen = 0;
		int end = off + len;

		while (off < end) {
			aChar = in[off++];

			if (aChar == '\\') {
				aChar = in[off++];

				if (aChar == 'u') {

					// Read the xxxx

					int value = 0;

					for (int i = 0; i < 4; i++) {
						aChar = in[off++];

						switch (aChar) {
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + aChar - '0';

								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + 10 + aChar - 'a';

								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + 10 + aChar - 'A';

								break;
							default:
								throw new IllegalArgumentException("Malformed \\uxxxx encoding");
						}
					}

					out[outLen++] = (char)value;
				}
				else {
					if (aChar == 't') {
						aChar = '\t';
					}
					else if (aChar == 'r') {
						aChar = '\r';
					}
					else if (aChar == 'n') {
						aChar = '\n';
					}
					else if (aChar == 'f') {
						aChar = '\f';
					}

					out[outLen++] = aChar;
				}
			}
			else {
				out[outLen++] = aChar;
			}
		}

		return new String(out, 0, outLen);
	}

	private void _parse(InputStream input, String initialLookup, boolean loadValues) throws IOException {
		try (Reader reader = new InputStreamReader(input)) {
			LineReader lr = new LineReader(reader);

			char[] convtBuf = new char[1024];
			int keyLen;
			int valueStart;
			char c;
			boolean hasSep;
			boolean precedingBackslash;

			int[] limit = lr.readLine();

			while (limit[0] >= 0) {
				c = 0;
				keyLen = 0;
				valueStart = limit[0];
				hasSep = false;

				precedingBackslash = false;

				while (keyLen < limit[0]) {
					c = lr.lineBuf[keyLen];

					// need check if escaped.

					if (((c == '=') || (c == ':')) && !precedingBackslash) {
						valueStart = keyLen + 1;

						hasSep = true;

						break;
					}
					else if (((c == ' ') || (c == '\t') || (c == '\f')) && !precedingBackslash) {
						valueStart = keyLen + 1;

						break;
					}

					if (c == '\\') {
						precedingBackslash = !precedingBackslash;
					}
					else {
						precedingBackslash = false;
					}

					keyLen++;
				}

				if (loadValues) {
					while (valueStart < limit[0]) {
						c = lr.lineBuf[valueStart];

						if ((c != ' ') && (c != '\t') && (c != '\f')) {
							if (!hasSep && ((c == '=') || (c == ':'))) {
								hasSep = true;
							}
							else {
								break;
							}
						}

						valueStart++;
					}
				}

				String key = _loadConvert(lr.lineBuf, 0, keyLen, convtBuf);

				KeyInfo info = new KeyInfo(limit[1] - limit[0] - 1, keyLen);

				if (loadValues) {
					info.value = _loadConvert(lr.lineBuf, valueStart, limit[0] - valueStart, convtBuf);
				}

				lookup.put(key, info);

				if (key.equals(initialLookup)) {
					return;
				}

				limit = lr.readLine();
			}
		}
	}

}