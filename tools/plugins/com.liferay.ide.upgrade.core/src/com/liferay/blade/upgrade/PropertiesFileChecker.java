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

package com.liferay.blade.upgrade;

import com.liferay.blade.api.SearchResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gregory Amerson
 */
public class PropertiesFileChecker {

	public PropertiesFileChecker(File file) {
		_file = file;

		try (InputStream inputStream = Files.newInputStream(file.toPath())) {
			_keyInfos = _parse(inputStream);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public List<SearchResult> findProperties(String key) {
		List<SearchResult> retval = new ArrayList<>();
		List<KeyInfo> infos = _keyInfos.get(key);

		if (infos != null) {
			for (KeyInfo info : infos) {
				retval.add(
					new SearchResult(
						_file, info.offset, info.offset + info.length, info.lineNumber, info.lineNumber, true));
			}
		}

		return retval;
	}

	public List<KeyInfo> getInfos(String key) {
		return _keyInfos.get(key);
	}

	public static class KeyInfo {

		public KeyInfo(int offset, int length, int lineNumber) {
			this.offset = offset;
			this.length = length;
			this.lineNumber = lineNumber;
		}

		public final int length;
		public final int lineNumber;
		public final int offset;
		public String value;

	}

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
								throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
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

	private Map<String, List<KeyInfo>> _parse(InputStream input) throws IOException {
		Map<String, List<KeyInfo>> keyInfos = new HashMap<>();

		try (InputStreamReader inputReader = new InputStreamReader(input)) {
			LineReader lr = new LineReader(inputReader);

			char[] convtBuf = new char[1024];
			int[] limit;
			int keyLen;
			int valueStart;
			char c;
			boolean hasSep;
			boolean precedingBackslash;
			int lineNumber = 0;

			while ((limit = lr.readLine())[0] >= 0) {
				lineNumber++;
				lineNumber += limit[2];
				c = 0;
				keyLen = 0;
				valueStart = limit[0];
				hasSep = false;

				// System.out.println("line=<" + new String(lineBuf, 0, limit) +
				// ">");

				precedingBackslash = false;

				while (keyLen < limit[0]) {
					c = lr._lineBuf[keyLen];

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
				while (valueStart < limit[0]) {
					c = lr._lineBuf[valueStart];

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

				String key = _loadConvert(lr._lineBuf, 0, keyLen, convtBuf);
				KeyInfo info = new KeyInfo(limit[1] - limit[0] - 1, keyLen, lineNumber);

				info.value = _loadConvert(lr._lineBuf, valueStart, limit[0] - valueStart, convtBuf);

				List<KeyInfo> infos = keyInfos.get(key);

				if (infos != null) {
					infos.add(info);
				}
				else {
					infos = new ArrayList<>();

					infos.add(info);

					keyInfos.put(key, infos);
				}
			}
		}

		return keyInfos;
	}

	private final File _file;
	private Map<String, List<KeyInfo>> _keyInfos = new HashMap<>();

	private class LineReader {

		public LineReader(Reader reader) {
			_reader = reader;

			_inCharBuf = new char[8192];
		}

		public int[] readLine() throws IOException {
			int len = 0;
			char c = 0;
			int emptylines = 0;

			boolean skipWhiteSpace = true;
			boolean commentLine = false;
			boolean newLine = true;
			boolean appendedLineBegin = false;
			boolean precedingBackslash = false;
			boolean skipLF = false;

			while (true) {
				if (_inOff >= _inLimit) {
					_inLimit = _reader.read(_inCharBuf);
					_inOff = 0;

					if (_inLimit <= 0) {
						if ((len == 0) || commentLine) {
							return new int[] {-1, _total, emptylines};
						}

						return new int[] {len, _total, emptylines};
					}
				}

				c = _inCharBuf[_inOff++];
				_total++;

				if (skipLF) {
					skipLF = false;

					if (c == '\n') {
						emptylines++;
						continue;
					}
				}

				if (skipWhiteSpace) {
					if ((c == ' ') || (c == '\t') || (c == '\f')) {
						continue;
					}

					if (!appendedLineBegin && ((c == '\r') || (c == '\n'))) {
						emptylines++;
						continue;
					}

					skipWhiteSpace = false;
					appendedLineBegin = false;
				}

				if (newLine) {
					newLine = false;

					if ((c == '#') || (c == '!')) {
						commentLine = true;
						emptylines++;
						continue;
					}
				}

				if ((c != '\n') && (c != '\r')) {
					_lineBuf[len++] = c;

					if (len == _lineBuf.length) {
						int newLength = _lineBuf.length * 2;

						if (newLength < 0) {
							newLength = Integer.MAX_VALUE;
						}

						char[] buf = new char[newLength];

						System.arraycopy(_lineBuf, 0, buf, 0, _lineBuf.length);
						_lineBuf = buf;
					}

					// flip the preceding backslash flag

					if (c == '\\') {
						precedingBackslash = !precedingBackslash;
						emptylines++;
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

					if (_inOff >= _inLimit) {
						_inLimit = _reader.read(_inCharBuf);
						_inOff = 0;

						if (_inLimit <= 0) {
							return new int[] {len, _total, emptylines};
						}
					}

					if (precedingBackslash) {
						len -= 1;

						// skip the leading whitespace characters in following
						// line

						skipWhiteSpace = true;
						appendedLineBegin = true;
						precedingBackslash = false;

						if (c == '\r') {
							skipLF = true;
						}
					}
					else {
						return new int[] {len, _total, emptylines};
					}
				}
			}
		}

		private char[] _inCharBuf;
		private int _inLimit = 0;
		private int _inOff = 0;
		private char[] _lineBuf = new char[1024];
		private Reader _reader;
		private int _total = 0;

	}

}