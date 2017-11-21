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

package com.liferay.ide.kaleo.core.model.internal;

import static com.liferay.ide.core.util.CoreUtil.empty;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 */
public class TransitionMetadataObject {

	public TransitionMetadataObject() {
	}

	@Override
	public boolean equals(Object obj) {
		boolean retval = true;

		if (this != obj) {
			if (obj instanceof TransitionMetadataObject) {
				try {
					if (!(toJSONString().equals(((TransitionMetadataObject)obj).toJSONString()))) {
						retval = false;
					}
				}
				catch (Exception e) {
					retval = false;
				}
			}
			else {
				retval = false;
			}
		}

		return retval;
	}

	public List<Point> getBendpoints() {
		return _bendpoints;
	}

	public Point getLabelPosition() {
		return _labelPosition;
	}

	public String getName() {
		return _name;
	}

	public void setLabelPosition(Point p) {
		_labelPosition = p;
	}

	public void setName(String n) {
		_name = n;
	}

	/**
	 * private void initialize( String contents ) { try { JSONObject json = new
	 * JSONObject( contents );
	 *
	 * if (json.has( "name" )) { _name = json.getString( "name"); } } catch
	 * (Exception e) { e.printStackTrace(); } }
	 */
	public String toJSONString() throws JSONException {
		JSONObject jsonObject = new JSONObject();

		if (!empty(_name)) {
			JSONArray jsonBendpoints = new JSONArray();

			for (Point point : _bendpoints) {
				JSONArray xy = new JSONArray();

				xy.put(point.getX());
				xy.put(point.getY());

				jsonBendpoints.put(xy);
			}

			jsonObject.put(_name, jsonBendpoints);
		}

		return jsonObject.toString();
	}

	@Override
	public String toString() {
		try {
			return toJSONString();
		}
		catch (JSONException jsone) {
			return super.toString();
		}
	}

	private List<Point> _bendpoints = new ArrayList<>();
	private Point _labelPosition = new Point(-1, -1);
	private String _name;

}