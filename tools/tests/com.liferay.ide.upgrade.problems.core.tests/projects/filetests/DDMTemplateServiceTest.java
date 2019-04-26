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

package com.liferay.portlet.dynamicdatamapping.service;

import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.ExecutionTestListeners;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.service.ServiceTestUtil;
import com.liferay.portal.test.EnvironmentExecutionTestListener;
import com.liferay.portal.test.LiferayIntegrationJUnitTestRunner;
import com.liferay.portal.test.Sync;
import com.liferay.portal.test.SynchronousDestinationExecutionTestListener;
import com.liferay.portal.test.TransactionalExecutionTestListener;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.TestPropsValues;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.dynamicdatamapping.RequiredTemplateException;
import com.liferay.portlet.dynamicdatamapping.TemplateDuplicateTemplateKeyException;
import com.liferay.portlet.dynamicdatamapping.TemplateNameException;
import com.liferay.portlet.dynamicdatamapping.TemplateScriptException;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.model.DDMTemplate;
import com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.util.JournalTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eduardo Garcia
 */
@ExecutionTestListeners(
	listeners = {
		EnvironmentExecutionTestListener.class,
		SynchronousDestinationExecutionTestListener.class,
		TransactionalExecutionTestListener.class
	})
@RunWith(LiferayIntegrationJUnitTestRunner.class)
@Sync
@Transactional
public class DDMTemplateServiceTest extends BaseDDMServiceTestCase {

	@Test
	public void testAddTemplateWithDuplicateKey() throws Exception {
		String templateKey = ServiceTestUtil.randomString();
		String language = TemplateConstants.LANG_TYPE_VM;

		try {
			addTemplate(
				_classNameId, 0, templateKey, "Test Template 1",
				DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
				DDMTemplateConstants.TEMPLATE_MODE_CREATE, language,
				getTestTemplateScript(language));
			addTemplate(
				_classNameId, 0, templateKey, "Test Template 2",
				DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
				DDMTemplateConstants.TEMPLATE_MODE_CREATE, language,
				getTestTemplateScript(language));

			Assert.fail();
		}
		catch (TemplateDuplicateTemplateKeyException tdtke) {
		}
	}

	@Test
	public void testAddTemplateWithoutName() throws Exception {
		String language = TemplateConstants.LANG_TYPE_VM;

		try {
			addTemplate(
				_classNameId, 0, null, StringPool.BLANK,
				DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
				DDMTemplateConstants.TEMPLATE_MODE_CREATE, language,
				getTestTemplateScript(language));

			Assert.fail();
		}
		catch (TemplateNameException tne) {
		}
	}

	@Test
	public void testAddTemplateWithoutScript() throws Exception {
		try {
			addTemplate(
				_classNameId, 0, null, "Test Template",
				DDMTemplateConstants.TEMPLATE_TYPE_FORM,
				DDMTemplateConstants.TEMPLATE_MODE_CREATE,
				TemplateConstants.LANG_TYPE_VM, StringPool.BLANK);

			Assert.fail();
		}
		catch (TemplateScriptException tse) {
		}
	}

	@Test
	public void testCopyTemplates() throws Exception {
		DDMTemplate template = addDisplayTemplate(
			_classNameId, 0, "Test Template");

		List<DDMTemplate> templates = copyTemplate(template);

		Assert.assertTrue(templates.size() >= 1);
	}

	@Test
	public void testDeleteTemplate() throws Exception {
		DDMTemplate template = addDisplayTemplate(
			_classNameId, 0, "Test Template");

		DDMTemplateLocalServiceUtil.deleteTemplate(template.getTemplateId());

		Assert.assertNull(
			DDMTemplateLocalServiceUtil.fetchDDMTemplate(
				template.getTemplateId()));
	}

	@Test
	public void testDeleteTemplateReferencedByJournalArticles()
		throws Exception {

		DDMStructure structure = addStructure(
			PortalUtil.getClassNameId(JournalArticle.class.getName()),
			"Test Structure");

		DDMTemplate template = addDisplayTemplate(
			structure.getPrimaryKey(), "Test Display Template");

		JournalTestUtil.addArticleWithXMLContent(
			group.getGroupId(), "<title>Test Article</title>",
			structure.getStructureKey(), template.getTemplateKey());

		try {
			DDMTemplateLocalServiceUtil.deleteTemplate(
				template.getTemplateId());

			Assert.fail();
		}
		catch (RequiredTemplateException rse) {
		}
	}

	@Test
	public void testFetchTemplate() throws Exception {
		DDMTemplate template = addDisplayTemplate(
			_classNameId, 0, "Test Template");

		Assert.assertNotNull(
			DDMTemplateLocalServiceUtil.fetchTemplate(
				template.getGroupId(), _classNameId,
				template.getTemplateKey()));
	}

	@Test
	public void testGetTemplates() throws Exception {
		DDMTemplate template = addDisplayTemplate(
			_classNameId, 0, "Test Template");

		List<DDMTemplate> templates = DDMTemplateLocalServiceUtil.getTemplates(
			template.getGroupId(), template.getClassNameId());

		Assert.assertTrue(templates.contains(template));
	}

	@Test
	public void testSearch() throws Exception {
		DDMTemplate template = addDisplayTemplate(
			_classNameId, 0, "Test Template 1");

		addDisplayTemplate(_classNameId, 0, "Test Template 2");

		List<DDMTemplate> templates = DDMTemplateLocalServiceUtil.search(
			template.getCompanyId(), template.getGroupId(),
			template.getClassNameId(), template.getClassPK(),
			template.getName(), template.getDescription(), template.getType(),
			template.getMode(), template.getLanguage(), false, 0, 1, null);

		Assert.assertEquals(1, templates.size());
	}

	@Test
	public void testSearchByKeywords() throws Exception {
		DDMTemplate template = addDisplayTemplate(
			_classNameId, 0, "Test Template 1");

		addDisplayTemplate(_classNameId, 0, "Test Template 2");

		List<DDMTemplate> templates = DDMTemplateLocalServiceUtil.search(
			template.getCompanyId(), template.getGroupId(),
			template.getClassNameId(), template.getClassPK(), null,
			template.getType(), template.getMode(), 0, 1, null);

		Assert.assertEquals(1, templates.size());
	}

	@Test
	public void testSearchCount() throws Exception {
		int initialCount = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), group.getGroupId(), _classNameId, 0,
			"Test Template", null, null, null, null, false);

		addDisplayTemplate(_classNameId, 0, "Test Template");

		int count = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), group.getGroupId(), _classNameId, 0,
			"Test Template", null, null, null, null, false);

		Assert.assertEquals(initialCount + 1, count);
	}

	@Test
	public void testSearchCountByKeywords() throws Exception {
		int initialCount = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), group.getGroupId(), _classNameId, 0,
			null, null, null);

		addDisplayTemplate(_classNameId, 0, "Test Template");

		int count = DDMTemplateLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), group.getGroupId(), _classNameId, 0,
			null, null, null);

		Assert.assertEquals(initialCount + 1, count);
	}

	protected List<DDMTemplate> copyTemplate(DDMTemplate template)
		throws Exception {

		return DDMTemplateLocalServiceUtil.copyTemplates(
			template.getUserId(), template.getClassNameId(),
			template.getClassPK(), -1, template.getType(),
			ServiceTestUtil.getServiceContext(group.getGroupId()));
	}

	protected DDMTemplate updateTemplate(DDMTemplate template)
		throws Exception {

		return DDMTemplateLocalServiceUtil.updateTemplate(
			template.getTemplateId(), template.getClassPK(),
			template.getNameMap(), template.getDescriptionMap(),
			template.getType(), template.getMode(), template.getLanguage(),
			template.getScript(), template.isCacheable(),
			template.isSmallImage(), template.getSmallImageURL(), null,
			ServiceTestUtil.getServiceContext());
	}

	private long _classNameId = PortalUtil.getClassNameId(AssetEntry.class);

}