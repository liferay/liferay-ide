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

package com.liferay.ide.alloy.ui.tests;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.xml.search.ui.tests.XmlSearchTestsBase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import org.junit.Test;

/**
 * @author Kuo Zhang
 * @author Terry Jia
 * @author Li Lu
 */
public class JSPFileTests extends XmlSearchTestsBase {

	@Test
	public void testActionURLName() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		_jspFile = _getJspFile("test-jsp-validation.jsp");

		String elementName = "liferay-portlet:actionURL";
		String attrName = "name";

		Thread.sleep(10000);
		validateHyperLinksForAttr(elementName, attrName);
		setAttrValue(_jspFile, elementName, attrName, "");
		buildAndValidate(_jspFile);

		ICompletionProposal[] proposals = getProposalsForAttr(_jspFile, elementName, attrName);

		if (!containsProposal(proposals, "actionTest - NewPortlet", true)) {
			buildAndValidate(_jspFile);
			proposals = getProposalsForAttr(_jspFile, elementName, attrName);
		}

		assertEquals(true, containsProposal(proposals, "actionTest - NewPortlet", true));
	}

	@Test
	public void testAInputType() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:input";
		String attrName = "type";

		_jspFile = _getJspFile("test-jsp-validation.jsp");
		setAttrValue(_jspFile, elementName, attrName, "");
		buildAndValidate(_jspFile);

		ICompletionProposal[] proposals = getProposalsForAttr(_jspFile, elementName, attrName);
		assertNotNull(proposals);
		assertEquals(true, proposals.length > 0);
		String[] expectedProposalString = {"assetCategories", "assetTags", "checkbox", "hidden", "radio", "text",
				"textarea", "timeZone"
			};

		for (String proposal : expectedProposalString) {
			assertTrue(containsProposal(proposals, proposal, true));
		}

		setAttrValue(_jspFile, elementName, attrName, "asse");

		proposals = getProposalsForAttr(_jspFile, elementName, attrName);
		assertEquals(true, proposals.length > 0);
		assertTrue(containsProposal(proposals, "assetCategories", true));
	}

	@Test
	public void testALable() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:a";
		String attrName = "label";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testATitle() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:a";
		String attrName = "title";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testAValidatorErrorMessage() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:validator";
		String attrName = "errorMessage";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testAValidatorName() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:validator";
		String attrName = "name";

		_jspFile = _getJspFile("test-jsp-validation.jsp");

		ICompletionProposal[] proposals = getProposalsForAttr(_jspFile, elementName, attrName);
		assertNotNull(proposals);
		assertEquals(true, proposals.length > 0);
		String[] expectedProposalString = {"acceptFiles", "alpha", "alphanum", "date", "digits", "email",
				"equalTo", "iri", "max", "min", "minLength", "number", "range", "rangeLength", "required", "url"
			};

		for (String proposal : expectedProposalString) {
			assertTrue("can't get proposal of " + expectedProposalString, containsProposal(proposals, proposal, true));
		}
	}

	@Test
	public void testButtonType() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:button";
		String attrName = "type";

		_jspFile = _getJspFile("test-jsp-validation.jsp");
		setAttrValue(_jspFile, elementName, attrName, "");
		buildAndValidate(_jspFile);

		ICompletionProposal[] proposals = getProposalsForAttr(_jspFile, elementName, attrName);
		assertNotNull(proposals);
		assertEquals(true, proposals.length > 0);
		String[] expectedProposalString = {"cancel", "reset", "submit"};

		for (String proposal : expectedProposalString) {
			assertTrue(containsProposal(proposals, proposal, true));
		}

		setAttrValue(_jspFile, elementName, attrName, "can");

		proposals = getProposalsForAttr(_jspFile, elementName, attrName);
		assertEquals(true, proposals.length > 0);
		assertTrue(containsProposal(proposals, "cancel", true));
	}

	@Test
	public void testButtonValue() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:button";
		String attrName = "value";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testErrorMessage() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "liferay-ui:error";
		String attrName = "message";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);

		// test on IDE-1774

		_jspFile = _getJspFile("test-jsp-validation.jsp");
		setAttrValue(_jspFile, elementName, attrName, "aaa-bb");
		buildAndValidate(_jspFile);

		String[] hover = getTextHoverForAttr(_jspFile, elementName, attrName);
		String expectMessageRegex = ".*" + "aaa bb" + ".*" + "src/content/Language.properties.*";

		assertTrue(hover[0].toString().matches(expectMessageRegex));
	}

	@Test
	public void testHeaderTitle() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "liferay-ui:header";
		String attrName = "title";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testHelpMessage() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:input";
		String attrName = "helpMessage";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testIcondeleteConfirmation() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "liferay-ui:icon-delete";
		String attrName = "confirmation";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testInputChecked() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:input";
		String attrName = "checked";

		_jspFile = _getJspFile("test-jsp-validation.jsp");
		ICompletionProposal[] proposals = getProposalsForAttr(_jspFile, elementName, attrName);
		assertNotNull(proposals);
		assertEquals(true, proposals.length > 0);
		assertTrue(containsProposal(proposals, "false", true));
		assertTrue(containsProposal(proposals, "true", true));
	}

	@Test
	public void testInputDisabled() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:input";
		String attrName = "disabled";

		_jspFile = _getJspFile("test-jsp-validation.jsp");
		ICompletionProposal[] proposals = getProposalsForAttr(_jspFile, elementName, attrName);
		assertNotNull(proposals);
		assertEquals(true, proposals.length > 0);
		assertTrue(containsProposal(proposals, "false", true));
		assertTrue(containsProposal(proposals, "true", true));
	}

	@Test
	public void testInputInlineLabel() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:input";
		String attrName = "inlineLabel";

		_jspFile = _getJspFile("test-jsp-validation.jsp");
		ICompletionProposal[] proposals = getProposalsForAttr(_jspFile, elementName, attrName);
		assertNotNull(proposals);
		assertEquals(true, proposals.length > 0);
		assertTrue(containsProposal(proposals, "left", true));
		assertTrue(containsProposal(proposals, "right", true));
	}

	@Test
	public void testInputmoveboxesLefttitle() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "liferay-ui:input-move-boxes";
		String attrName = "leftTitle";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testInputmoveboxesRighgtitle() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "liferay-ui:input-move-boxes";
		String attrName = "rightTitle";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testLable() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:input";
		String attrName = "label";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testLiferayPortletParamValueMessage() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "liferay-portlet:param";
		String attrName = "value";

		validateHyperLinksForAttr(elementName, attrName);
	}

	@Test
	public void testMessageKey() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "liferay-ui:message";
		String attrName = "key";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testPanelTitle() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "liferay-ui:panel";
		String attrName = "title";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testPlaceholder() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:input";
		String attrName = "placeholder";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testPortletParamValueMessage() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "portlet:param";
		String attrName = "value";

		validateHyperLinksForAttr(elementName, attrName);
	}

	@Test
	public void testSearchContainerEmptyResulsMessage() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "liferay-ui:search-container";
		String attrName = "emptyResultsMessage";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testSocialActivitiesFeedLinkMessage() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "liferay-ui:social-activities";
		String attrName = "feedLinkMessage";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testSuffix() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:input";
		String attrName = "suffix";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	@Test
	public void testWorkflowstatusStatusMessage() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		String elementName = "aui:workflow-status";
		String attrName = "statusMessage";

		validateHyperLinksForAttr(elementName, attrName);
		validateTextHoverForAttr(elementName, attrName);
		validateContentAssistForEmptyAttr(elementName, attrName);
		validateContentAssistForAttr(elementName, attrName);
	}

	public void validateContentAssistForAttr(String elementName, String attrName) throws Exception {
		_jspFile = _getJspFile("test-jsp-validation.jsp");
		setAttrValue(_jspFile, elementName, attrName, "MessageKeyHove");
		buildAndValidate(_jspFile);
		ICompletionProposal[] proposals = getProposalsForAttr(_jspFile, elementName, attrName);

		assertNotNull(proposals);
		assertEquals(true, proposals.length > 0);
		String expectedProposalString = "MessageKeyHoverTest - [Language.properties]";

		assertEquals(true, containsProposal(proposals, expectedProposalString, true));
	}

	public void validateContentAssistForEmptyAttr(String elementName, String attrName) throws Exception {
		_jspFile = _getJspFile("test-jsp-validation.jsp");
		setAttrValue(_jspFile, elementName, attrName, "");
		buildAndValidate(_jspFile);

		ICompletionProposal[] proposals = getProposalsForAttr(_jspFile, elementName, attrName);
		assertNotNull(proposals);
		assertEquals(true, proposals.length > 0);
		String[] expectedProposalString = {"javax.portlet.title - [Language.properties]",
				"MessageKeyHoverTest - [Language.properties]", "Test - [Language.properties]"
			};

		for (String proposal : expectedProposalString) {
			assertTrue("can't get proposal " + proposal + " in " + elementName + attrName,
				containsProposal(proposals, proposal, true));
		}
	}

	public void validateHyperLinksForAttr(String elementName, String attrName) throws Exception {
		_jspFile = _getJspFile("test-jsp-validation.jsp");
		String expectedHyperlinkText1 = "Open 'MessageKeyHoverTest' in Language.properties";
		String expectedHyperlinkText2 = "Open";

		IHyperlink[] hyperLinks = getHyperLinksForAttr(_jspFile, elementName, attrName);

		boolean haslink = false;

		if (containHyperlink(hyperLinks, expectedHyperlinkText1, true) ||
			containHyperlink(hyperLinks, expectedHyperlinkText2, false)) {

			haslink = true;
		}

		hyperLinks = getHyperLinksForAttr(_jspFile, elementName, attrName);
		assertTrue("can'get hyper link at <" + elementName + "  " + attrName + ">", haslink);
	}

	public void validateTextHoverForAttr(String elementName, String attrName) throws Exception {
		_jspFile = _getJspFile("test-jsp-validation.jsp");

		String[] hover = getTextHoverForAttr(_jspFile, elementName, attrName);

		String expectMessageRegex =
			".*this is the test for message key text hover.* in /Portlet-Xml-Test-portlet/docroot/WEB-INF/src/content/Language.properties.*";

		hover = getTextHoverForAttr(_jspFile, elementName, attrName);

		if ((hover.length == 0) || !hover[0].toString().matches(expectMessageRegex)) {
			buildAndValidate(_jspFile);

			hover = getTextHoverForAttr(_jspFile, elementName, attrName);
		}

		String hoverString = hover[0].toString();

		assertTrue(hoverString.matches(expectMessageRegex));
	}

	@Override
	protected String getBundleId() {
		return _BUNDLE_ID;
	}

	private IFile _getJspFile(String fileName) throws Exception {
		IFolder folder = CoreUtil.getDefaultDocrootFolder(_getProject());

		IFile file = folder.getFile(fileName);

		if (FileUtil.exists(file)) {
			return file;
		}

		return null;
	}

	private IProject _getProject() throws Exception {
		if (_project == null) {
			_project = super.getProject("portlets", "Portlet-Xml-Test-portlet");

			deleteOtherProjects(_project);
		}

		return _project;
	}

	private static final String _BUNDLE_ID = "com.liferay.ide.alloy.ui.tests";

	private static IProject _project;

	private IFile _jspFile;

}