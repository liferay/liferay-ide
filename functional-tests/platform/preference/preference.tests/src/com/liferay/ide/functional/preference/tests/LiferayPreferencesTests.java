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

package com.liferay.ide.functional.preference.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Rui Wang
 */
public class LiferayPreferencesTests extends SwtbotBase {

	@Test
	public void checkItemsInLiferay() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openLiferay();

		dialogAction.liferay.expandLiferay(LIFERAY);

		String[] expectedLiferay = {
			KALEO_DESIGNER, KALEO_WORKFLOW_VALIDATION, MAVEN, PLUGIN_VALIDATION, UPGRADE_PROBLEMS, XML_SEARCH
		};

		List<String> liferayItems = dialogAction.liferay.expandLiferay(LIFERAY);

		String[] liferayItemsString = liferayItems.toArray(new String[0]);

		validationAction.assertEquals(expectedLiferay, liferayItemsString);

		dialogAction.cancel();
	}

	@Test
	public void checkKaleoDesignerInitialState() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openKaleoDesigner();

		validationAction.assertRadioFalse(dialogAction.kaleoDesign.getAlwaysRadio());

		validationAction.assertRadioFalse(dialogAction.kaleoDesign.getNeverRadio());

		validationAction.assertRadioTrue(dialogAction.kaleoDesign.getPromptRadio());

		dialogAction.cancel();
	}

	@Test
	public void checkKaleoWorkflowValidationInitialState() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openKaleoWorkflowValidation();

		Assert.assertTrue(
			dialogAction.kaleoWorkflowValidation.getConfigureProjectSpecificSettings(
				CONFIGURE_PROJECT_SPECIFIC_SETTINGS));

		validationAction.assertTextEquals(
			SELECT_THE_SEVERITY_LEVEL_FOR_THE_FOLLOWING_VALIDATION_PROBLEMS,
			dialogAction.kaleoWorkflowValidation.getSelectTheSeverityLevelForTheFollowingValidationProblems());

		validationAction.assertTextEquals(
			WORKFLOW_VALIDATION, dialogAction.kaleoWorkflowValidation.getWorkflowValidation());

		validationAction.assertTextEquals(
			"Error", dialogAction.kaleoWorkflowValidation.getDefaultWorkflowValidationLogical());

		dialogAction.cancel();
	}

	@Test
	public void checkLiferayInitialState() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openLiferay();

		validationAction.assertTextEquals(LIFERAY, dialogAction.liferay.getLiferay());

		validationAction.assertEnabledTrue(dialogAction.liferay.getMessageDialogs());

		validationAction.assertTextEquals(
			CLEAR_ALL_DO_NOT_SHOW_AGAIN_SETTINGS_AND_SHOW_ALL_HIDDEN_DIALOGS_AGAIN,
			dialogAction.liferay.getClearAllDoNotShowAgainSettingsAndShowAllHiddenDialogsAgain());

		validationAction.assertEnabledTrue(dialogAction.liferay.getClearBtn());

		dialogAction.cancel();
	}

	@Test
	public void checkMavenInitialState() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openMaven();

		validationAction.assertTextEquals(MAVEN, dialogAction.maven.getMaven());

		validationAction.assertEnabledTrue(dialogAction.maven.getDefaultArchetypesForNewLiferayPluginProjectWizard());

		validationAction.assertTextEquals(
			"com.liferay.maven.archetypes:liferay-portlet-archetype:6.2.5", dialogAction.maven.getPortlet());

		validationAction.assertTextEquals(
			"com.liferay.maven.archetypes:liferay-portlet-jsf-archetype:6.2.5", dialogAction.maven.getPortletJsf());

		validationAction.assertTextEquals(
			"com.liferay.maven.archetypes:liferay-portlet-icefaces-archetype:6.2.5",
			dialogAction.maven.getPortletICEfaces());

		validationAction.assertTextEquals(
			"com.liferay.maven.archetypes:liferay-portlet-liferay-faces-alloy-archetype:6.2.5",
			dialogAction.maven.getPortletLiferayFacesAlloy());

		validationAction.assertTextEquals(
			"com.liferay.maven.archetypes:liferay-portlet-primefaces-archetype:6.2.5",
			dialogAction.maven.getPortletPrimefaces());

		validationAction.assertTextEquals(
			"com.liferay.maven.archetypes:liferay-portlet-richfaces-archetype:6.2.5",
			dialogAction.maven.getPortletRichFaces());

		validationAction.assertTextEquals(
			"com.liferay.maven.archetypes:liferay-portlet-spring-mvc-archetype:6.2.5",
			dialogAction.maven.getPortletSpringMVC());

		validationAction.assertTextEquals(
			"com.vaadin:vaadin-archetype-liferay-portlet:7.4.0.alpha2", dialogAction.maven.getPortletVaadin());

		validationAction.assertTextEquals(
			"com.liferay.maven.archetypes:liferay-hook-archetype:6.2.5", dialogAction.maven.getHook());

		validationAction.assertTextEquals(
			"com.liferay.maven.archetypes:liferay-theme-archetype:6.2.5", dialogAction.maven.getTheme());

		validationAction.assertTextEquals(
			"com.liferay.maven.archetypes:liferay-layouttpl-archetype:6.2.5", dialogAction.maven.getLayoutTemplate());

		validationAction.assertTextEquals(
			"com.liferay.maven.archetypes:liferay-servicebuilder-archetype:6.2.5",
			dialogAction.maven.getServiceBuilder());

		validationAction.assertTextEquals(
			"com.liferay.maven.archetypes:liferay-ext-archetype:6.2.5", dialogAction.maven.getExt());

		validationAction.assertTextEquals(
			"com.liferay.maven.archetypes:liferay-web-archetype:6.2.5", dialogAction.maven.getWeb());

		validationAction.assertCheckedFalse(dialogAction.maven.getAddPluginTypeSuffixForMavenProjectContextRoot());

		validationAction.assertCheckedTrue(dialogAction.maven.getDisableCustomJspValidationChecking());

		dialogAction.cancel();
	}

	@Test
	public void checkPluginValidationInitialState() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openPluginValidation();

		Assert.assertTrue(
			dialogAction.pluginValidation.getConfigureProjectSpecificSettings(CONFIGURE_PROJECT_SPECIFIC_SETTINGS));

		validationAction.assertTextEquals(
			SELECT_THE_SEVERITY_LEVEL_FOR_THE_FOLLOWING_VALIDATION_PROBLEMS,
			dialogAction.pluginValidation.getSelectTheSeverityLevelForTheFollowingValidationProblems());

		validationAction.assertTextEquals(
			PORTLET_XML_DESCRIPTOR, dialogAction.pluginValidation.getPortletXmlDescriptor());

		validationAction.assertTextEquals("Warning", dialogAction.pluginValidation.getSyntaxInvalid());

		validationAction.assertTextEquals("Warning", dialogAction.pluginValidation.getTypeClassOrInterfaceNotFound());

		validationAction.assertTextEquals(
			"Warning", dialogAction.pluginValidation.getHierarchyOfTypeClassOrInterfaceIncorrect());

		validationAction.assertTextEquals("Warning", dialogAction.pluginValidation.getResourceNotFound());

		validationAction.assertTextEquals("Warning", dialogAction.pluginValidation.getReferenceToXmlElementNotFound());

		validationAction.assertTextEquals(
			SERVICE_XML_DESCIPTOR, dialogAction.pluginValidation.getServiceXmlDescriptor());

		validationAction.assertTextEquals(
			LIFERAY_PORTLET_XML_DESCIPTOR, dialogAction.pluginValidation.getLiferayPortletXmlDescriptor());

		validationAction.assertTextEquals(
			LIFERAY_HOOK_XML_DESCRIPTOR, dialogAction.pluginValidation.getLiferayHookXmlDescriptor());

		validationAction.assertTextEquals(
			LIFERAY_DISPLAY_XML_DESCRIPTOR, dialogAction.pluginValidation.getLiferayDisplayXmlDescriptor());

		validationAction.assertTextEquals(
			LIFERAY_LAYOUT_TEMPLATES_DESCIPTOR, dialogAction.pluginValidation.getLiferayLayoutTemplatesDescriptor());

		validationAction.assertTextEquals(LIFERAY_JSP_FILES, dialogAction.pluginValidation.getLiferayJspFiles());

		dialogAction.cancel();
	}

	@Ignore("IDE-4613 upgrade planner in preferences has been removed because of offline support")
	@Test
	public void checkUpgradePlannerInitialState() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openUpgradePlanner();

		validationAction.assertTextEquals(UPGRADE_PLANNER, dialogAction.upgradePlanner.getUpgradePlanner());

		validationAction.assertEnabledTrue(dialogAction.upgradePlanner.getAddBtn());

		validationAction.assertEnabledTrue(dialogAction.upgradePlanner.getRemoveBtn());

		validationAction.assertTableContains(dialogAction.upgradePlanner.getUrl(), CODE_UPGRADE);

		validationAction.assertTableContains(dialogAction.upgradePlanner.getUrl(), CODE_UPGRADE);

		dialogAction.cancel();
	}

	@Test
	public void checkUpgradeProblemsInitialState() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openUpgradeProblems();

		validationAction.assertEnabledTrue(dialogAction.upgradeProblems.getRemoveBtn());

		validationAction.assertEnabledTrue(
			dialogAction.upgradeProblems.getUpgradeProblemsForIgnoreBreakingChangeProblems());

		dialogAction.cancel();
	}

	@Test
	public void checkXmlSearchInitialState() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openXmlSearch();

		validationAction.assertTextEquals(XML_SEARCH, dialogAction.xmlSearch.getXmlSearch());

		validationAction.assertTextEquals(
			"portal-master,portal-trunk",
			dialogAction.xmlSearch.getSpecifyTheListOfProjectsToIgnoreWhileSearchingXmlFiles());

		dialogAction.cancel();
	}

}