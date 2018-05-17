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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class WorkflowNodeMetadataObject {

	public WorkflowNodeMetadataObject() {
	}

	public WorkflowNodeMetadataObject(String contents) {
		if (!CoreUtil.empty(contents)) {
			_initialize(contents);
		}
	}

	@Override
	public boolean equals(Object obj) {
		boolean retval = true;

		if (this != obj) {
			if (obj instanceof WorkflowNodeMetadataObject) {
				WorkflowNodeMetadataObject object = (WorkflowNodeMetadataObject)obj;

				if ((_terminal == object._terminal) && _nodeLocation.equals(object.getNodeLocation())) {
					for (int i = 0; i < _transitionsMetadata.size(); i++) {
						TransitionMetadataObject transitionMetadataObject = _transitionsMetadata.get(i);

						if (!(transitionMetadataObject.equals(object._transitionsMetadata.get(i)))) {
							retval = false;
						}
					}
				}
				else {
					retval = false;
				}
			}
			else {
				retval = false;
			}
		}

		return retval;
	}

	public Point getNodeLocation() {
		return _nodeLocation;
	}

	public List<TransitionMetadataObject> getTransitionsMetadata() {
		return _transitionsMetadata;
	}

	public boolean isTerminal() {
		return _terminal;
	}

	public void setNodeLocation(Point nodeLocation) {
		_nodeLocation = nodeLocation;
	}

	public void setTerminal(boolean terminal) {
		_terminal = terminal;
	}

	public String toJSONString() throws JSONException {
		JSONObject jsonObject = new JSONObject();

		if (isTerminal()) {
			jsonObject.put("terminal", true);
		}

		if (_nodeLocation != null) {
			JSONArray jsonXY = _pointToJSONPoint(_nodeLocation);

			jsonObject.put("xy", jsonXY);
		}

		if (_transitionsMetadata.size() > 0) {
			JSONObject jsonTransitions = new JSONObject();

			for (TransitionMetadataObject transitionMetadata : _transitionsMetadata) {
				String transitionName = transitionMetadata.getName();

				if (!CoreUtil.empty(transitionName)) {
					JSONObject jsonTransitionMetadata = new JSONObject();

					JSONArray jsonBendpoints = new JSONArray();

					for (Point bendpoint : transitionMetadata.getBendpoints()) {
						JSONArray xy = _pointToJSONPoint(bendpoint);

						jsonBendpoints.put(xy);
					}

					Point labelPosition = transitionMetadata.getLabelPosition();

					if (!labelPosition.equals(KaleoModelUtil.DEFAULT_POINT)) {
						JSONArray xy = _pointToJSONPoint(labelPosition);

						jsonTransitionMetadata.put("xy", xy);
					}

					jsonTransitionMetadata.put("bendpoints", jsonBendpoints);

					jsonTransitions.put(transitionName, jsonTransitionMetadata);
				}
			}

			jsonObject.put("transitions", jsonTransitions);
		}

		return jsonObject.toString();
	}

	@SuppressWarnings("rawtypes")
	private void _initialize(String contents) {
		try {
			JSONObject jsonObject = new JSONObject(contents);

			if (jsonObject.has("terminal")) {
				_terminal = jsonObject.getBoolean("terminal");
			}

			_nodeLocation = _jsonPointToPoint(jsonObject);

			if (jsonObject.has("transitions")) {
				JSONObject jsonTransitions = jsonObject.getJSONObject("transitions");

				Iterator transitionNames = jsonTransitions.keys();

				while (transitionNames.hasNext()) {
					Object transitionName = transitionNames.next();

					String name = transitionName.toString();

					if (jsonTransitions.has(name)) {
						JSONObject jsonTransitionMetadata = jsonTransitions.getJSONObject(name);

						TransitionMetadataObject transitionMetaObject = new TransitionMetadataObject();

						transitionMetaObject.setName(name);

						if (jsonTransitionMetadata.has("xy")) {
							JSONArray jsonLabelPosition = jsonTransitionMetadata.getJSONArray("xy");

							transitionMetaObject.setLabelPosition(_jsonArrayToPoint(jsonLabelPosition));
						}

						if (jsonTransitionMetadata.has("bendpoints")) {
							JSONArray jsonBendpoints = jsonTransitionMetadata.getJSONArray("bendpoints");

							for (int i = 0; i < jsonBendpoints.length(); i++) {
								JSONArray xy = jsonBendpoints.optJSONArray(i);

								if (xy != null) {
									List<Point> bendPoint = transitionMetaObject.getBendpoints();

									bendPoint.add(_jsonArrayToPoint(xy));
								}
							}
						}

						_transitionsMetadata.add(transitionMetaObject);
					}
				}
			}
		}
		catch (Exception e) {
			KaleoCore.logError("Error loading node metadata object", e);
		}
	}

	private Point _jsonArrayToPoint(JSONArray jsonArray) {
		Point point = KaleoModelUtil.DEFAULT_POINT;

		try {
			if ((jsonArray.length() == 2) && !jsonArray.isNull(0) && !jsonArray.isNull(1)) {
				point = new Point(jsonArray.getInt(0), jsonArray.getInt(1));
			}
		}
		catch (JSONException jsone) {
			KaleoCore.logError("Invalid JSON syntax", jsone);
		}

		return point;
	}

	private Point _jsonPointToPoint(JSONObject jsonPoint) throws JSONException {
		if (jsonPoint.has("xy")) {
			JSONArray jsonXY = jsonPoint.getJSONArray("xy");

			return _jsonArrayToPoint(jsonXY);
		}
		else {
			return KaleoModelUtil.DEFAULT_POINT;
		}
	}

	private JSONArray _pointToJSONPoint(Point point) {
		JSONArray jsonXY = new JSONArray();

		jsonXY.put(point.getX());
		jsonXY.put(point.getY());

		return jsonXY;
	}

	private Point _nodeLocation = new Point();
	private boolean _terminal = false;
	private List<TransitionMetadataObject> _transitionsMetadata = new ArrayList<>();

}