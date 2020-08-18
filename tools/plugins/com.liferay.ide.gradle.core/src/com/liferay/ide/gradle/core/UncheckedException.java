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

package com.liferay.ide.gradle.core;

import java.io.IOException;

import java.lang.reflect.InvocationTargetException;

import org.gradle.api.UncheckedIOException;

/**
 * Copied from Gradle sdk:
 * tooling-api/org/gradle/internal/UncheckedException.java
 *
 * @author Charles Wu
 */
public final class UncheckedException extends RuntimeException {

	public static RuntimeException throwAsUncheckedException(Throwable t) {
		return throwAsUncheckedException(t, false);
	}

	public static RuntimeException throwAsUncheckedException(Throwable t, boolean preserveMessage) {
		if (t instanceof RuntimeException) {
			throw (RuntimeException)t;
		}
		else if (t instanceof Error) {
			throw (Error)t;
		}
		else if (t instanceof IOException) {
			if (preserveMessage) {
				throw new UncheckedIOException(t.getMessage(), t);
			}

			throw new UncheckedIOException(t);
		}
		else if (preserveMessage) {
			throw new UncheckedException(t.getMessage(), t);
		}
		else {
			throw new UncheckedException(t);
		}
	}

	public static RuntimeException unwrapAndRethrow(InvocationTargetException e) {
		return throwAsUncheckedException(e.getTargetException());
	}

	public UncheckedException(Throwable cause) {
		super(cause);
	}

	private UncheckedException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}