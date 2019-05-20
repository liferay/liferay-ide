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

package com.liferay.dynamic.data.mapping.form.values.query.internal.model;

import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.AggregatePredicateFilter;
import com.liferay.portal.kernel.util.PredicateFilter;
import com.liferay.portal.kernel.util.PrefixPredicateFilter;
import com.liferay.portal.kernel.portlet.JavaScriptPortletResourcePredicateFilter;
import com.liferay.dynamic.data.mapping.form.values.query.internal.model.DDMFormFieldValuePredicateFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pablo Carvalho
 */
public class DDMFormValuesFilterImpl implements DDMFormValuesFilter {

	@Override
	public List<DDMFormFieldValue> filter(DDMFormValues ddmFormValues) {
		List<DDMFormFieldValue> ddmFormFieldValues = ListUtil.copy(
			ddmFormValues.getDDMFormFieldValues());

		return filter(ddmFormFieldValues);
	}

	@Override
	public List<DDMFormFieldValue> filter(
		List<DDMFormFieldValue> ddmFormFieldValues) {

		if (_greedy) {
			addNestedDDMFormFieldValues(ddmFormFieldValues);
		}

		return ListUtil.filter(
			ddmFormFieldValues, new DDMFormFieldValuePredicateFilter());
	}

	@Override
	public boolean isGreedy() {
		return _greedy;
	}

	@Override
	public void setDDMFormFieldValueMatcher(
		DDMFormFieldValueMatcher ddmFormFieldValueMatcher) {

		_ddmFormFieldValueMatcher = ddmFormFieldValueMatcher;
	}

	@Override
	public void setGreedy(boolean greedy) {
		_greedy = greedy;
	}

	protected void addNestedDDMFormFieldValues(
		List<DDMFormFieldValue> ddmFormFieldValues) {

		for (int i = 0; i < ddmFormFieldValues.size(); i++) {
			DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(i);

			ddmFormFieldValues.addAll(
				ddmFormFieldValue.getNestedDDMFormFieldValues());
		}
	}

	protected List<DDMFormFieldValue> getDDMFormFieldValues(
		DDMFormFieldValue ddmFormFieldValue) {

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(ddmFormFieldValue);

		if (_greedy) {
			ddmFormFieldValues.addAll(
				getDDMFormFieldValues(
					ddmFormFieldValue.getNestedDDMFormFieldValues()));
		}

		return ddmFormFieldValues;
	}

	protected List<DDMFormFieldValue> getDDMFormFieldValues(
		List<DDMFormFieldValue> baseDDMFormFieldValues) {

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		for (DDMFormFieldValue ddmFormFieldValue : baseDDMFormFieldValues) {
			ddmFormFieldValues.addAll(getDDMFormFieldValues(ddmFormFieldValue));
		}

		return ddmFormFieldValues;
	}

	private DDMFormFieldValueMatcher _ddmFormFieldValueMatcher;
	private boolean _greedy;

	private class DDMFormFieldValuePredicateFilter
		implements PredicateFilter<DDMFormFieldValue> {

		@Override
		public boolean filter(DDMFormFieldValue ddmFormFieldValue) {
			return _ddmFormFieldValueMatcher.matches(ddmFormFieldValue);
		}

	}

}