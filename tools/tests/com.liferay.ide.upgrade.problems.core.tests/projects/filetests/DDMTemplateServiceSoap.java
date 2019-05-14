/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portlet.dynamicdatamapping.service.http;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;

import com.liferay.portlet.dynamicdatamapping.service.DDMTemplateServiceUtil;

import java.rmi.RemoteException;

import java.util.Locale;
import java.util.Map;

/**
 * Provides the SOAP utility for the
 * {@link com.liferay.portlet.dynamicdatamapping.service.DDMTemplateServiceUtil} service utility. The
 * static methods of this class calls the same methods of the service utility.
 * However, the signatures are different because it is difficult for SOAP to
 * support certain types.
 *
 * <p>
 * ServiceBuilder follows certain rules in translating the methods. For example,
 * if the method in the service utility returns a {@link java.util.List}, that
 * is translated to an array of {@link com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap}.
 * If the method in the service utility returns a
 * {@link com.liferay.portlet.dynamicdatamapping.model.DDMTemplate}, that is translated to a
 * {@link com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap}. Methods that SOAP cannot
 * safely wire are skipped.
 * </p>
 *
 * <p>
 * The benefits of using the SOAP utility is that it is cross platform
 * compatible. SOAP allows different languages like Java, .NET, C++, PHP, and
 * even Perl, to call the generated services. One drawback of SOAP is that it is
 * slow because it needs to serialize all calls into a text format (XML).
 * </p>
 *
 * <p>
 * You can see a list of services at http://localhost:8080/api/axis. Set the
 * property <b>axis.servlet.hosts.allowed</b> in portal.properties to configure
 * security.
 * </p>
 *
 * <p>
 * The SOAP utility is only generated for remote services.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMTemplateServiceHttp
 * @see com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap
 * @see com.liferay.portlet.dynamicdatamapping.service.DDMTemplateServiceUtil
 * @generated
 */
@ProviderType
public class DDMTemplateServiceSoap {
	/**
	* Adds a template.
	*
	* @param groupId the primary key of the group
	* @param classNameId the primary key of the class name for template's
	related model
	* @param classPK the primary key of the template's related entity
	* @param nameMap the template's locales and localized names
	* @param descriptionMap the template's locales and localized descriptions
	* @param type the template's type. For more information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param mode the template's mode. For more information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param language the template's script language. For more information,
	see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param script the template's script
	* @param serviceContext the service context to be applied. Must have the
	<code>ddmResource</code> attribute to check permissions. Can set
	the UUID, creation date, modification date, guest permissions,
	and group permissions for the template.
	* @return the template
	* @throws PortalException if the user did not have permission to add the
	template or if a portal exception occurred
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap addTemplate(
		long groupId, long classNameId, long classPK,
		java.lang.String[] nameMapLanguageIds,
		java.lang.String[] nameMapValues,
		java.lang.String[] descriptionMapLanguageIds,
		java.lang.String[] descriptionMapValues, java.lang.String type,
		java.lang.String mode, java.lang.String language,
		java.lang.String script,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			Map<Locale, String> nameMap = LocalizationUtil.getLocalizationMap(nameMapLanguageIds,
					nameMapValues);
			Map<Locale, String> descriptionMap = LocalizationUtil.getLocalizationMap(descriptionMapLanguageIds,
					descriptionMapValues);

			com.liferay.portlet.dynamicdatamapping.model.DDMTemplate returnValue =
				DDMTemplateServiceUtil.addTemplate(groupId, classNameId,
					classPK, nameMap, descriptionMap, type, mode, language,
					script, serviceContext);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Copies the template, creating a new template with all the values
	* extracted from the original one. This method supports defining a new name
	* and description.
	*
	* @param templateId the primary key of the template to be copied
	* @param nameMap the new template's locales and localized names
	* @param descriptionMap the new template's locales and localized
	descriptions
	* @param serviceContext the service context to be applied. Must have the
	<code>ddmResource</code> attribute to check permissions. Can set
	the UUID, creation date, modification date, guest permissions,
	and group permissions for the template.
	* @return the new template
	* @throws PortalException if the user did not have permission to add the
	template or if a portal exception occurred
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap copyTemplate(
		long templateId, java.lang.String[] nameMapLanguageIds,
		java.lang.String[] nameMapValues,
		java.lang.String[] descriptionMapLanguageIds,
		java.lang.String[] descriptionMapValues,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			Map<Locale, String> nameMap = LocalizationUtil.getLocalizationMap(nameMapLanguageIds,
					nameMapValues);
			Map<Locale, String> descriptionMap = LocalizationUtil.getLocalizationMap(descriptionMapLanguageIds,
					descriptionMapValues);

			com.liferay.portlet.dynamicdatamapping.model.DDMTemplate returnValue =
				DDMTemplateServiceUtil.copyTemplate(templateId, nameMap,
					descriptionMap, serviceContext);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap copyTemplate(
		long templateId,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			com.liferay.portlet.dynamicdatamapping.model.DDMTemplate returnValue =
				DDMTemplateServiceUtil.copyTemplate(templateId, serviceContext);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Copies all the templates matching the class name ID, class PK, and type.
	* This method creates new templates, extracting all the values from the old
	* ones and updating their class PKs.
	*
	* @param classNameId the primary key of the class name for template's
	related model
	* @param classPK the primary key of the original template's related entity
	* @param newClassPK the primary key of the new template's related entity
	* @param type the template's type. For more information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param serviceContext the service context to be applied. Must have the
	<code>ddmResource</code> attribute to check permissions. Can set
	the UUID, creation date, modification date, guest permissions,
	and group permissions for the template.
	* @return the new template
	* @throws PortalException if the user did not have permission to add the
	template or if a portal exception occurred
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap[] copyTemplates(
		long classNameId, long classPK, long newClassPK, java.lang.String type,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			java.util.List<com.liferay.portlet.dynamicdatamapping.model.DDMTemplate> returnValue =
				DDMTemplateServiceUtil.copyTemplates(classNameId, classPK,
					newClassPK, type, serviceContext);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Deletes the template and its resources.
	*
	* @param templateId the primary key of the template to be deleted
	* @throws PortalException if the user did not have permission to delete the
	template or if a portal exception occurred
	* @throws SystemException if a system exception occurred
	*/
	public static void deleteTemplate(long templateId)
		throws RemoteException {
		try {
			DDMTemplateServiceUtil.deleteTemplate(templateId);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns the template matching the group and template key.
	*
	* @param groupId the primary key of the group
	* @param classNameId the primary key of the class name for template's
	related model
	* @param templateKey the unique string identifying the template
	* @return the matching template, or <code>null</code> if a matching
	template could not be found
	* @throws PortalException if the user did not have permission to view the
	template
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap fetchTemplate(
		long groupId, long classNameId, java.lang.String templateKey)
		throws RemoteException {
		try {
			com.liferay.portlet.dynamicdatamapping.model.DDMTemplate returnValue =
				DDMTemplateServiceUtil.fetchTemplate(groupId, classNameId,
					templateKey);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns the template with the ID.
	*
	* @param templateId the primary key of the template
	* @return the template with the ID
	* @throws PortalException if the user did not have permission to view the
	template or if a matching template could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap getTemplate(
		long templateId) throws RemoteException {
		try {
			com.liferay.portlet.dynamicdatamapping.model.DDMTemplate returnValue =
				DDMTemplateServiceUtil.getTemplate(templateId);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns the template matching the group and template key.
	*
	* @param groupId the primary key of the group
	* @param classNameId the primary key of the class name for template's
	related model
	* @param templateKey the unique string identifying the template
	* @return the matching template
	* @throws PortalException if a matching template could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap getTemplate(
		long groupId, long classNameId, java.lang.String templateKey)
		throws RemoteException {
		try {
			com.liferay.portlet.dynamicdatamapping.model.DDMTemplate returnValue =
				DDMTemplateServiceUtil.getTemplate(groupId, classNameId,
					templateKey);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns the template matching the group and template key, optionally in
	* the global scope.
	*
	* <p>
	* This method first searches in the group. If the template is still not
	* found and <code>includeGlobalTemplates</code> is set to
	* <code>true</code>, this method searches the global group.
	* </p>
	*
	* @param groupId the primary key of the group
	* @param classNameId the primary key of the class name for template's
	related model
	* @param templateKey the unique string identifying the template
	* @param includeGlobalTemplates whether to include the global scope in the
	search
	* @return the matching template
	* @throws PortalException if a matching template could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap getTemplate(
		long groupId, long classNameId, java.lang.String templateKey,
		boolean includeGlobalTemplates) throws RemoteException {
		try {
			com.liferay.portlet.dynamicdatamapping.model.DDMTemplate returnValue =
				DDMTemplateServiceUtil.getTemplate(groupId, classNameId,
					templateKey, includeGlobalTemplates);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns all the templates matching the group and class name ID.
	*
	* @param groupId the primary key of the group
	* @param classNameId the primary key of the class name for template's
	related model
	* @return the matching templates
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap[] getTemplates(
		long groupId, long classNameId) throws RemoteException {
		try {
			java.util.List<com.liferay.portlet.dynamicdatamapping.model.DDMTemplate> returnValue =
				DDMTemplateServiceUtil.getTemplates(groupId, classNameId);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns all the templates matching the group, class name ID, and class
	* PK.
	*
	* @param groupId the primary key of the group
	* @param classNameId the primary key of the class name for template's
	related model
	* @param classPK the primary key of the template's related entity
	* @return the matching templates
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap[] getTemplates(
		long groupId, long classNameId, long classPK) throws RemoteException {
		try {
			java.util.List<com.liferay.portlet.dynamicdatamapping.model.DDMTemplate> returnValue =
				DDMTemplateServiceUtil.getTemplates(groupId, classNameId,
					classPK);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns all the templates matching the class name ID, class PK, type, and
	* mode.
	*
	* @param groupId the primary key of the group
	* @param classNameId the primary key of the class name for template's
	related model
	* @param classPK the primary key of the template's related entity
	* @param type the template's type. For more information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @return the matching templates
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap[] getTemplates(
		long groupId, long classNameId, long classPK, java.lang.String type)
		throws RemoteException {
		try {
			java.util.List<com.liferay.portlet.dynamicdatamapping.model.DDMTemplate> returnValue =
				DDMTemplateServiceUtil.getTemplates(groupId, classNameId,
					classPK, type);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap[] getTemplates(
		long groupId, long classNameId, long classPK, java.lang.String type,
		java.lang.String mode) throws RemoteException {
		try {
			java.util.List<com.liferay.portlet.dynamicdatamapping.model.DDMTemplate> returnValue =
				DDMTemplateServiceUtil.getTemplates(groupId, classNameId,
					classPK, type, mode);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns all the templates matching the group and class PK.
	*
	* @param groupId the primary key of the group
	* @param classPK the primary key of the template's related entity
	* @return the matching templates
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap[] getTemplatesByClassPK(
		long groupId, long classPK) throws RemoteException {
		try {
			java.util.List<com.liferay.portlet.dynamicdatamapping.model.DDMTemplate> returnValue =
				DDMTemplateServiceUtil.getTemplatesByClassPK(groupId, classPK);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns an ordered range of all the templates matching the group and
	* structure class name ID and all the generic templates matching the group.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end -
	* start</code> instances. <code>start</code> and <code>end</code> are not
	* primary keys, they are indexes in the result set. Thus, <code>0</code>
	* refers to the first result in the set. Setting both <code>start</code>
	* and <code>end</code> to {@link
	* com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	* result set.
	* </p>
	*
	* @param groupId the primary key of the group
	* @param structureClassNameId the primary key of the class name for the
	template's related structure (optionally <code>0</code>). Specify
	<code>0</code> to return generic templates only.
	* @param start the lower bound of the range of templates to return
	* @param end the upper bound of the range of templates to return (not
	inclusive)
	* @param orderByComparator the comparator to order the templates
	(optionally <code>null</code>)
	* @return the range of matching templates ordered by the comparator
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap[] getTemplatesByStructureClassNameId(
		long groupId, long structureClassNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws RemoteException {
		try {
			java.util.List<com.liferay.portlet.dynamicdatamapping.model.DDMTemplate> returnValue =
				DDMTemplateServiceUtil.getTemplatesByStructureClassNameId(groupId,
					structureClassNameId, start, end, orderByComparator);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns the number of templates matching the group and structure class
	* name ID plus the number of generic templates matching the group.
	*
	* @param groupId the primary key of the group
	* @param structureClassNameId the primary key of the class name for the
	template's related structure (optionally <code>0</code>). Specify
	<code>0</code> to count generic templates only.
	* @return the number of matching templates plus the number of matching
	generic templates
	* @throws SystemException if a system exception occurred
	*/
	public static int getTemplatesByStructureClassNameIdCount(long groupId,
		long structureClassNameId) throws RemoteException {
		try {
			int returnValue = DDMTemplateServiceUtil.getTemplatesByStructureClassNameIdCount(groupId,
					structureClassNameId);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns an ordered range of all the templates matching the group, class
	* name ID, class PK, type, and mode, and matching the keywords in the
	* template names and descriptions.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end -
	* start</code> instances. <code>start</code> and <code>end</code> are not
	* primary keys, they are indexes in the result set. Thus, <code>0</code>
	* refers to the first result in the set. Setting both <code>start</code>
	* and <code>end</code> to {@link
	* com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	* result set.
	* </p>
	*
	* @param companyId the primary key of the template's company
	* @param groupId the primary key of the group
	* @param classNameId the primary key of the class name for template's
	related model
	* @param classPK the primary key of the template's related entity
	* @param keywords the keywords (space separated), which may occur in the
	template's name or description (optionally <code>null</code>)
	* @param type the template's type (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param mode the template's mode (optionally <code>null</code>) For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param start the lower bound of the range of templates to return
	* @param end the upper bound of the range of templates to return (not
	inclusive)
	* @param orderByComparator the comparator to order the templates
	(optionally <code>null</code>)
	* @return the matching templates ordered by the comparator
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap[] search(
		long companyId, long groupId, long classNameId, long classPK,
		java.lang.String keywords, java.lang.String type,
		java.lang.String mode, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws RemoteException {
		try {
			java.util.List<com.liferay.portlet.dynamicdatamapping.model.DDMTemplate> returnValue =
				DDMTemplateServiceUtil.search(companyId, groupId, classNameId,
					classPK, keywords, type, mode, start, end, orderByComparator);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns an ordered range of all the templates matching the group, class
	* name ID, class PK, name keyword, description keyword, type, mode, and
	* language.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end -
	* start</code> instances. <code>start</code> and <code>end</code> are not
	* primary keys, they are indexes in the result set. Thus, <code>0</code>
	* refers to the first result in the set. Setting both <code>start</code>
	* and <code>end</code> to {@link
	* com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	* result set.
	* </p>
	*
	* @param companyId the primary key of the template's company
	* @param groupId the primary key of the group
	* @param classNameId the primary key of the class name for template's
	related model
	* @param classPK the primary key of the template's related entity
	* @param name the name keywords (optionally <code>null</code>)
	* @param description the description keywords (optionally
	<code>null</code>)
	* @param type the template's type (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param mode the template's mode (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param language the template's script language (optionally
	<code>null</code>). For more information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param andOperator whether every field must match its keywords, or just
	one field.
	* @param start the lower bound of the range of templates to return
	* @param end the upper bound of the range of templates to return (not
	inclusive)
	* @param orderByComparator the comparator to order the templates
	(optionally <code>null</code>)
	* @return the matching templates ordered by the comparator
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap[] search(
		long companyId, long groupId, long classNameId, long classPK,
		java.lang.String name, java.lang.String description,
		java.lang.String type, java.lang.String mode,
		java.lang.String language, boolean andOperator, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws RemoteException {
		try {
			java.util.List<com.liferay.portlet.dynamicdatamapping.model.DDMTemplate> returnValue =
				DDMTemplateServiceUtil.search(companyId, groupId, classNameId,
					classPK, name, description, type, mode, language,
					andOperator, start, end, orderByComparator);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns an ordered range of all the templates matching the group IDs,
	* class name IDs, class PK, type, and mode, and matching the keywords in
	* the template names and descriptions.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end -
	* start</code> instances. <code>start</code> and <code>end</code> are not
	* primary keys, they are indexes in the result set. Thus, <code>0</code>
	* refers to the first result in the set. Setting both <code>start</code>
	* and <code>end</code> to {@link
	* com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	* result set.
	* </p>
	*
	* @param companyId the primary key of the template's company
	* @param groupIds the primary keys of the groups
	* @param classNameIds the primary keys of the entity's instances the
	templates are related to
	* @param classPKs the primary keys of the template's related entities
	* @param keywords the keywords (space separated), which may occur in the
	template's name or description (optionally <code>null</code>)
	* @param type the template's type (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param mode the template's mode (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param start the lower bound of the range of templates to return
	* @param end the upper bound of the range of templates to return (not
	inclusive)
	* @param orderByComparator the comparator to order the templates
	(optionally <code>null</code>)
	* @return the matching templates ordered by the comparator
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap[] search(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		java.lang.String keywords, java.lang.String type,
		java.lang.String mode, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws RemoteException {
		try {
			java.util.List<com.liferay.portlet.dynamicdatamapping.model.DDMTemplate> returnValue =
				DDMTemplateServiceUtil.search(companyId, groupIds,
					classNameIds, classPKs, keywords, type, mode, start, end,
					orderByComparator);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns an ordered range of all the templates matching the group IDs,
	* class name IDs, class PK, name keyword, description keyword, type, mode,
	* and language.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end -
	* start</code> instances. <code>start</code> and <code>end</code> are not
	* primary keys, they are indexes in the result set. Thus, <code>0</code>
	* refers to the first result in the set. Setting both <code>start</code>
	* and <code>end</code> to {@link
	* com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	* result set.
	* </p>
	*
	* @param companyId the primary key of the template's company
	* @param groupIds the primary keys of the groups
	* @param classNameIds the primary keys of the entity's instances the
	templates are related to
	* @param classPKs the primary keys of the template's related entities
	* @param name the name keywords (optionally <code>null</code>)
	* @param description the description keywords (optionally
	<code>null</code>)
	* @param type the template's type (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param mode the template's mode (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param language the template's script language (optionally
	<code>null</code>). For more information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param andOperator whether every field must match its keywords, or just
	one field.
	* @param start the lower bound of the range of templates to return
	* @param end the upper bound of the range of templates to return (not
	inclusive)
	* @param orderByComparator the comparator to order the templates
	(optionally <code>null</code>)
	* @return the matching templates ordered by the comparator
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap[] search(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		java.lang.String name, java.lang.String description,
		java.lang.String type, java.lang.String mode,
		java.lang.String language, boolean andOperator, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws RemoteException {
		try {
			java.util.List<com.liferay.portlet.dynamicdatamapping.model.DDMTemplate> returnValue =
				DDMTemplateServiceUtil.search(companyId, groupIds,
					classNameIds, classPKs, name, description, type, mode,
					language, andOperator, start, end, orderByComparator);

			return com.liferay.portlet.dynamicdatamapping.model.DDMTemplateSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns the number of templates matching the group, class name ID, class
	* PK, type, and mode, and matching the keywords in the template names and
	* descriptions.
	*
	* @param companyId the primary key of the template's company
	* @param groupId the primary key of the group
	* @param classNameId the primary key of the class name for template's
	related model
	* @param classPK the primary key of the template's related entity
	* @param keywords the keywords (space separated), which may occur in the
	template's name or description (optionally <code>null</code>)
	* @param type the template's type (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param mode the template's mode (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @return the number of matching templates
	* @throws SystemException if a system exception occurred
	*/
	public static int searchCount(long companyId, long groupId,
		long classNameId, long classPK, java.lang.String keywords,
		java.lang.String type, java.lang.String mode) throws RemoteException {
		try {
			int returnValue = DDMTemplateServiceUtil.searchCount(companyId,
					groupId, classNameId, classPK, keywords, type, mode);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns the number of templates matching the group, class name ID, class
	* PK, name keyword, description keyword, type, mode, and language.
	*
	* @param companyId the primary key of the template's company
	* @param groupId the primary key of the group
	* @param classNameId the primary key of the class name for template's
	related model
	* @param classPK the primary key of the template's related entity
	* @param name the name keywords (optionally <code>null</code>)
	* @param description the description keywords (optionally
	<code>null</code>)
	* @param type the template's type (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param mode the template's mode (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param language the template's script language (optionally
	<code>null</code>). For more information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param andOperator whether every field must match its keywords, or just
	one field.
	* @return the number of matching templates
	* @throws SystemException if a system exception occurred
	*/
	public static int searchCount(long companyId, long groupId,
		long classNameId, long classPK, java.lang.String name,
		java.lang.String description, java.lang.String type,
		java.lang.String mode, java.lang.String language, boolean andOperator)
		throws RemoteException {
		try {
			int returnValue = DDMTemplateServiceUtil.searchCount(companyId,
					groupId, classNameId, classPK, name, description, type,
					mode, language, andOperator);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns the number of templates matching the group IDs, class name IDs,
	* class PK, type, and mode, and matching the keywords in the template names
	* and descriptions.
	*
	* @param companyId the primary key of the template's company
	* @param groupIds the primary keys of the groups
	* @param classNameIds the primary keys of the entity's instances the
	templates are related to
	* @param classPKs the primary keys of the template's related entities
	* @param keywords the keywords (space separated), which may occur in the
	template's name or description (optionally <code>null</code>)
	* @param type the template's type (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param mode the template's mode (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @return the number of matching templates
	* @throws SystemException if a system exception occurred
	*/
	public static int searchCount(long companyId, long[] groupIds,
		long[] classNameIds, long[] classPKs, java.lang.String keywords,
		java.lang.String type, java.lang.String mode) throws RemoteException {
		try {
			int returnValue = DDMTemplateServiceUtil.searchCount(companyId,
					groupIds, classNameIds, classPKs, keywords, type, mode);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	/**
	* Returns the number of templates matching the group IDs, class name IDs,
	* class PK, name keyword, description keyword, type, mode, and language.
	*
	* @param companyId the primary key of the template's company
	* @param groupIds the primary keys of the groups
	* @param classNameIds the primary keys of the entity's instances the
	templates are related to
	* @param classPKs the primary keys of the template's related entities
	* @param name the name keywords (optionally <code>null</code>)
	* @param description the description keywords (optionally
	<code>null</code>)
	* @param type the template's type (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param mode the template's mode (optionally <code>null</code>). For more
	information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param language the template's script language (optionally
	<code>null</code>). For more information, see {@link
	com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants}.
	* @param andOperator whether every field must match its keywords, or just
	one field.
	* @return the number of matching templates
	* @throws SystemException if a system exception occurred
	*/
	public static int searchCount(long companyId, long[] groupIds,
		long[] classNameIds, long[] classPKs, java.lang.String name,
		java.lang.String description, java.lang.String type,
		java.lang.String mode, java.lang.String language, boolean andOperator)
		throws RemoteException {
		try {
			int returnValue = DDMTemplateServiceUtil.searchCount(companyId,
					groupIds, classNameIds, classPKs, name, description, type,
					mode, language, andOperator);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	private static Log _log = LogFactoryUtil.getLog(DDMTemplateServiceSoap.class);
}