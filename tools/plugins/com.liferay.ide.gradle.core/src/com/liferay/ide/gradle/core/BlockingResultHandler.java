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

import com.liferay.ide.core.util.ListUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.ResultHandler;

/**
 * Copied from gradle sdk:
 * tooling-api/org/gradle/tooling/internal/consumer/BlockingResultHandler.java
 *
 * @author Gregory Amerson
 */
public class BlockingResultHandler<T> implements ResultHandler<T> {

	public BlockingResultHandler(Class<T> resultType) {
		_resultType = resultType;
	}

	public T getResult() {
		Object result;

		try {
			result = _queue.take();
		}
		catch (InterruptedException ie) {
			throw UncheckedException.throwAsUncheckedException(ie);
		}

		if (result instanceof Throwable) {
			throw UncheckedException.throwAsUncheckedException(_attachCallerThreadStackTrace((Throwable)result));
		}

		if (result == _NULL) {
			return null;
		}

		return _resultType.cast(result);
	}

	public void onComplete(T result) {
		_queue.add(result == null ? _NULL : result);
	}

	public void onFailure(GradleConnectionException failure) {
		_queue.add(failure);
	}

	private Throwable _attachCallerThreadStackTrace(Throwable failure) {
		List<StackTraceElement> adjusted = new ArrayList<>();

		Collections.addAll(adjusted, failure.getStackTrace());

		List<StackTraceElement> currentThreadStack = Arrays.asList(Thread.currentThread().getStackTrace());

		if (ListUtil.isNotEmpty(currentThreadStack)) {
			adjusted.addAll(currentThreadStack.subList(2, currentThreadStack.size()));
		}

		failure.setStackTrace(adjusted.toArray(new StackTraceElement[0]));

		return failure;
	}

	private static final Object _NULL = new Object();

	private final BlockingQueue<Object> _queue = new ArrayBlockingQueue<>(1);
	private final Class<T> _resultType;

}