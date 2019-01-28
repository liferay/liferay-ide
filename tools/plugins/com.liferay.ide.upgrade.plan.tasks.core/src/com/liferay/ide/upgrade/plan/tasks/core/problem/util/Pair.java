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

package com.liferay.ide.upgrade.plan.tasks.core.problem.util;

/**
 * @author Alberto Chaparro
 */
public class Pair<F, S> {

	public Pair(F l, S r) {
		_first = l;
		_second = r;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		Pair<?, ?> other = (Pair<?, ?>)obj;

		if (_first == null) {
			if (other.first() != null) {
				return false;
			}
		}
		else if (!_first.equals(other.first())) {
			return false;
		}

		if (_second == null) {
			if (other.second() != null) {
				return false;
			}
		}
		else if (!_second.equals(other._second)) {
			return false;
		}

		return true;
	}

	public F first() {
		return _first;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((_first == null) ? 0 : _first.hashCode());
		result = prime * result + ((_second == null) ? 0 : _second.hashCode());

		return result;
	}

	public S second() {
		return _second;
	}

	@Override
	public String toString() {
		return "(" + _first + ", " + _second + ")";
	}

	private final F _first;
	private final S _second;

}