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

import com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormXSDDeserializer;
import com.liferay.bookmarks.service.persistence.BookmarksEntryFinder;

public class BaseDDMDisplayMethodTest {

	@Test
	public void testInvocationMethod() throws Exception {
		
		int count = _alloyServiceInvoker.dynamicQueryCount(_dynamicQuery);
		
		boolean isShowAddStructureButton = _baseDDMDisplay.isShowAddStructureButton();
		
		boolean isEditable = _ddl.isEditable(_preferences, "", _groupId);
		
		DDMExpression<Double> _ddmExpression = _ddmExpressionFactory.createDoubleDDMExpression("");
		
		List<DDMFormFieldEvaluationResult> ddmFormFieldEvaluationResultList = _ddmFormFieldEvaluationResult.getNestedDDMFormFieldEvaluationResults();

		ResourceBundle resourceBundle = _ddmFormFieldFactoryHelper.getResourceBundle(_locale);
		
		_calendarBookingService.invokeTransition(_calendarBookingId, _status, _serviceContext);
		
		CalendarBookingServiceUtil.invokeTransition(_calendarBookingId, _status, _serviceContext);
		
		DDLRecord ddlLocalRecord =_ddlRecordLocalService.deleteRecordLocale(_recordId, _locale, _serviceContext);
		
		DDLRecord _ddlLocalRecord = DDLRecordLocalServiceUtil.deleteRecordLocale(_recordId, _locale, _serviceContext);
		
		DDLRecord ddlRecord = _ddlRecordService.deleteRecordLocale(_recordId, _locale, _serviceContext);
		
		DDLRecord _ddlRecord = DDLRecordServiceUtil.deleteRecordLocale(_recordId, _locale, _serviceContext);
		
		DDMStructure ddmStructure = _ddmStructureLocalService.updateXSD(_structureId, "", _serviceContext);
		
		DDMStructure _ddmStructure = DDMStructureLocalServiceUtil.updateXSD(_structureId, "", _serviceContext);
	}
	
	private AlloyServiceInvoker _alloyServiceInvoker;
	
	private DynamicQuery _dynamicQuery;
	
	@Reference
	private BaseDDMDisplay _baseDDMDisplay;
	
	@Reference
	private DDL _ddl;
	
	@Reference
	private PortletPreferences _preferences;
	
	private long _groupId;
	
	@Reference
	private DDMExpressionFactory _ddmExpressionFactory;
	
	private DDMFormFieldEvaluationResult _ddmFormFieldEvaluationResult;
	
	private DDMFormFieldFactoryHelper _ddmFormFieldFactoryHelper;
	
	private CalendarBookingService _calendarBookingService;
	
	private DDLRecordLocalService _ddlRecordLocalService;
	
	private DDLRecordService _ddlRecordService;
	
	private DDMStructureLocalService _ddmStructureLocalService;
	
	@Reference
	private Locale _locale;
	
	private long _calendarBookingId = 1;
	
	private int _status = 1;
	
	private long _recordId = 1;
	
	private long _structureId = 1;
	
	@Reference
	private ServiceContext _serviceContext;
	
}
