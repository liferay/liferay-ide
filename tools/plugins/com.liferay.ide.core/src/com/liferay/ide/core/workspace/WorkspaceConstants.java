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

package com.liferay.ide.core.workspace;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Terry Jia
 */
@SuppressWarnings("serial")
public class WorkspaceConstants {

	public static final String BUNDLE_ARTIFACT_NAME_PROPERTY = "liferay.workspace.bundle.artifact.name";

	public static final String BUNDLE_URL_CE_7_0 =
		"https://releases-cdn.liferay.com/portal/7.0.6-ga7/liferay-ce-portal-tomcat-7.0-ga7-20180507111753223.zip";

	public static final String BUNDLE_URL_CE_7_1 =
		"https://releases-cdn.liferay.com/portal/7.1.3-ga4/liferay-ce-portal-tomcat-7.1.3-ga4-20190508171117552.tar.gz";

	public static final String BUNDLE_URL_CE_7_2 =
		"https://releases-cdn.liferay.com/portal/7.2.1-ga2/liferay-ce-portal-tomcat-7.2.1-ga2-20191111141448326.tar.gz";

	public static final String BUNDLE_URL_CE_7_3 =
		"https://releases-cdn.liferay.com/portal/7.3.2-ga3/liferay-ce-portal-tomcat-7.3.2-ga3-20200519164024819.tar.gz";

	public static final String BUNDLE_URL_CE_7_4 =
		"https://releases-cdn.liferay.com/portal/7.4.0-ga1/liferay-ce-portal-tomcat-7.4.0-ga1-20210419204607406.tar.gz";

	public static final String BUNDLE_URL_PROPERTY = "liferay.workspace.bundle.url";

	public static final String DEFAULT_BUNDLE_ARTIFACT_NAME = "portal-tomcat-bundle";

	public static final String DEFAULT_EXT_DIR = "ext";

	public static final String DEFAULT_HOME_DIR = "bundles";

	public static final String DEFAULT_MODULES_DIR = "modules";

	public static final String DEFAULT_PLUGINS_SDK_DIR = "plugins-sdk";

	public static final String DEFAULT_THEMES_DIR = "themes";

	public static final String DEFAULT_WARS_DIR = "wars";

	public static final String EXT_DIR_PROPERTY = "liferay.workspace.ext.dir";

	public static final String HOME_DIR_PROPERTY = "liferay.workspace.home.dir";

	public static final String LIFERAY_HOME_PROPERTY = "liferayHome";

	public static final String LIFERAY_PORTAL_URL = "https://releases-cdn.liferay.com/portal/";

	public static final String[] LIFERAY_VERSIONS = {"7.0", "7.1", "7.2", "7.3", "7.4"};

	public static final String MODULES_DIR_PROPERTY = "liferay.workspace.modules.dir";

	public static final String PLUGINS_SDK_DIR_PROPERTY = "liferay.workspace.plugins.sdk.dir";

	public static final String TARGET_PLATFORM_INDEX_SOURCES_PROPERTY = "target.platform.index.sources";

	public static final String TARGET_PLATFORM_VERSION_PROPERTY = "liferay.workspace.target.platform.version";

	public static final String THEMES_DIR_PROPERTY = "liferay.workspace.themes.dir";

	public static final String WARS_DIR_PROPERTY = "liferay.workspace.wars.dir";

	public static final String WORKSPACE_BOM_VERSION = "liferay.bom.version";

	public static final String WORKSPACE_PRODUCT_7_1 = "portal-7.1-ga4";

	public static final String WORKSPACE_PRODUCT_PROPERTY = "liferay.workspace.product";

	public static final Map<String, String> liferayBundleUrlVersions = new HashMap<String, String>() {
		{
			put("7.0.6-2", BUNDLE_URL_CE_7_0);
			put("7.1.0", LIFERAY_PORTAL_URL + "7.1.0-ga1/liferay-ce-portal-tomcat-7.1.0-ga1-20180703012531655.zip");
			put("7.1.1", LIFERAY_PORTAL_URL + "7.1.1-ga2/liferay-ce-portal-tomcat-7.1.1-ga2-20181112144637000.tar.gz");
			put("7.1.2", LIFERAY_PORTAL_URL + "7.1.2-ga3/liferay-ce-portal-tomcat-7.1.2-ga3-20190107144105508.tar.gz");
			put("7.1.3-1", BUNDLE_URL_CE_7_1);
			put("7.2.0", LIFERAY_PORTAL_URL + "7.2.0-ga1/liferay-ce-portal-tomcat-7.2.0-ga1-20190531153709761.tar.gz");
			put("7.2.1-1", BUNDLE_URL_CE_7_2);
			put(
				"7.3.0-1",
				LIFERAY_PORTAL_URL + "7.3.0-ga1/liferay-ce-portal-tomcat-7.3.0-ga1-20200127150653953.tar.gz");
			put(
				"7.3.1-1",
				LIFERAY_PORTAL_URL + "7.3.1-ga2/liferay-ce-portal-tomcat-7.3.1-ga2-20200327090859603.tar.gz");
			put("7.3.2-1", BUNDLE_URL_CE_7_3);
			put(
				"7.3.3-1",
				LIFERAY_PORTAL_URL + "7.3.3-ga4/liferay-ce-portal-tomcat-7.3.3-ga4-20200701015330959.tar.gz");
			put("7.3.4", LIFERAY_PORTAL_URL + "7.3.4-ga5/liferay-ce-portal-tomcat-7.3.4-ga5-20200811154319029.tar.gz");
			put("7.3.5", LIFERAY_PORTAL_URL + "7.3.5-ga6/liferay-ce-portal-tomcat-7.3.5-ga6-20200930172312275.tar.gz");
			put("7.3.6", LIFERAY_PORTAL_URL + "7.3.6-ga7/liferay-ce-portal-tomcat-7.3.6-ga7-20210301155526191.tar.gz");
			put("7.3.7", LIFERAY_PORTAL_URL + "7.3.7-ga8/liferay-ce-portal-tomcat-7.3.7-ga8-20210610183559721.tar.gz");
			put("7.4.0", LIFERAY_PORTAL_URL + "7.4.0-ga1/liferay-ce-portal-tomcat-7.4.0-ga1-20210419204607406.tar.gz");
			put(
				"7.4.1-1",
				LIFERAY_PORTAL_URL + "7.4.1-ga2/liferay-ce-portal-tomcat-7.4.1-ga2-20210609223456272.tar.gz");
			put(
				"7.4.2-1",
				LIFERAY_PORTAL_URL + "7.4.2-ga3/liferay-ce-portal-tomcat-7.4.2-ga3-20210728053338694.tar.gz");
			put(
				"7.4.3.4",
				LIFERAY_PORTAL_URL + "7.4.3.4-ga4/liferay-ce-portal-tomcat-7.4.3.4-ga4-20211020095956970.tar.gz");
			put(
				"7.4.3.5",
				LIFERAY_PORTAL_URL + "7.4.3.5-ga5/liferay-ce-portal-tomcat-7.4.3.5-ga5-20211221192828235.tar.gz");
			put(
				"7.4.3.6",
				LIFERAY_PORTAL_URL + "7.4.3.6-ga6/liferay-ce-portal-tomcat-7.4.3.6-ga6-20211227141428520.tar.gz");
			put(
				"7.4.3.7",
				LIFERAY_PORTAL_URL + "7.4.3.7-ga7/liferay-ce-portal-tomcat-7.4.3.7-ga7-20220107103529408.tar.gz");
			put(
				"7.4.3.8",
				LIFERAY_PORTAL_URL + "7.4.3.8-ga8/liferay-ce-portal-tomcat-7.4.3.8-ga8-20220117195955276.tar.gz");
			put(
				"7.4.3.9",
				LIFERAY_PORTAL_URL + "7.4.3.9-ga9/liferay-ce-portal-tomcat-7.4.3.9-ga9-20220125123415632.tar.gz");
			put(
				"7.4.3.10",
				LIFERAY_PORTAL_URL + "7.4.3.10-ga10/liferay-ce-portal-tomcat-7.4.3.10-ga10-20220130185310773.tar.gz");
			put(
				"7.4.3.11",
				LIFERAY_PORTAL_URL + "7.4.3.11-ga11/liferay-ce-portal-tomcat-7.4.3.11-ga11-20220207124232466.tar.gz");
			put(
				"7.4.3.12",
				LIFERAY_PORTAL_URL + "7.4.3.12-ga12/liferay-ce-portal-tomcat-7.4.3.12-ga12-20220215025828785.tar.gz");
			put(
				"7.4.3.13",
				LIFERAY_PORTAL_URL + "7.4.3.13-ga13/liferay-ce-portal-tomcat-7.4.3.13-ga13-20220223110450724.tar.gz");
			put(
				"7.4.3.14",
				LIFERAY_PORTAL_URL + "7.4.3.14-ga14/liferay-ce-portal-tomcat-7.4.3.14-ga14-20220302152058124.tar.gz");
			put(
				"7.4.3.15",
				LIFERAY_PORTAL_URL + "7.4.3.15-ga15/liferay-ce-portal-tomcat-7.4.3.15-ga15-20220308142134102.tar.gz");
			put(
				"7.4.3.16",
				LIFERAY_PORTAL_URL + "7.4.3.16-ga16/liferay-ce-portal-tomcat-7.4.3.16-ga16-20220314155405565.tar.gz");
			put(
				"7.4.3.17",
				LIFERAY_PORTAL_URL + "7.4.3.17-ga17/liferay-ce-portal-tomcat-7.4.3.17-ga17-20220322082755437.tar.gz");
			put(
				"7.4.3.18",
				LIFERAY_PORTAL_URL + "7.4.3.18-ga18/liferay-ce-portal-tomcat-7.4.3.18-ga18-20220329092001364.tar.gz");
			put(
				"7.4.3.19",
				LIFERAY_PORTAL_URL + "7.4.3.19-ga19/liferay-ce-portal-tomcat-7.4.3.19-ga19-20220404174529051.tar.gz");
			put(
				"7.4.3.20",
				LIFERAY_PORTAL_URL + "7.4.3.20-ga20/liferay-ce-portal-tomcat-7.4.3.20-ga20-20220408161249276.tar.gz");
			put(
				"7.4.3.21",
				LIFERAY_PORTAL_URL + "7.4.3.21-ga21/liferay-ce-portal-tomcat-7.4.3.21-ga21-20220418102345052.tar.gz");
			put(
				"7.4.3.22",
				LIFERAY_PORTAL_URL + "7.4.3.22-ga22/liferay-ce-portal-tomcat-7.4.3.22-ga22-20220427101210177.tar.gz");
			put(
				"7.4.3.23",
				LIFERAY_PORTAL_URL + "7.4.3.23-ga23/liferay-ce-portal-tomcat-7.4.3.23-ga23-20220504071803965.tar.gz");
			put(
				"7.4.3.24",
				LIFERAY_PORTAL_URL + "7.4.3.24-ga24/liferay-ce-portal-tomcat-7.4.3.24-ga24-20220510074512112.tar.gz");
			put(
				"7.4.3.25",
				LIFERAY_PORTAL_URL + "7.4.3.25-ga25/liferay-ce-portal-tomcat-7.4.3.25-ga25-20220517170439802.tar.gz");
			put(
				"7.4.3.26",
				LIFERAY_PORTAL_URL + "7.4.3.26-ga26/liferay-ce-portal-tomcat-7.4.3.26-ga26-20220520184751553.tar.gz");
			put(
				"7.4.3.27",
				LIFERAY_PORTAL_URL + "7.4.3.27-ga27/liferay-ce-portal-tomcat-7.4.3.27-ga27-20220531092132731.tar.gz");
			put(
				"7.4.3.28",
				LIFERAY_PORTAL_URL + "7.4.3.28-ga28/liferay-ce-portal-tomcat-7.4.3.28-ga28-20220607175240830.tar.gz");
			put(
				"7.4.3.29",
				LIFERAY_PORTAL_URL + "7.4.3.29-ga29/liferay-ce-portal-tomcat-7.4.3.29-ga29-20220615102224235.tar.gz");
			put(
				"7.4.3.30",
				LIFERAY_PORTAL_URL + "7.4.3.30-ga30/liferay-ce-portal-tomcat-7.4.3.30-ga30-20220622172832884.tar.gz");
			put(
				"7.4.3.31",
				LIFERAY_PORTAL_URL + "7.4.3.31-ga31/liferay-ce-portal-tomcat-7.4.3.31-ga31-20220628171544529.tar.gz");
			put(
				"7.4.3.32",
				LIFERAY_PORTAL_URL + "7.4.3.32-ga32/liferay-ce-portal-tomcat-7.4.3.32-ga32-20220704200951543.tar.gz");
			put(
				"7.4.3.33",
				LIFERAY_PORTAL_URL + "7.4.3.33-ga33/liferay-ce-portal-tomcat-7.4.3.33-ga33-20220711220434272.tar.gz");
			put(
				"7.4.3.34",
				LIFERAY_PORTAL_URL + "7.4.3.34-ga34/liferay-ce-portal-tomcat-7.4.3.34-ga34-20220719155007101.tar.gz");
			put(
				"7.4.3.35",
				LIFERAY_PORTAL_URL + "7.4.3.35-ga35/liferay-ce-portal-tomcat-7.4.3.35-ga35-20220726170951454.tar.gz");
			put(
				"7.4.3.36",
				LIFERAY_PORTAL_URL + "7.4.3.36-ga36/liferay-ce-portal-tomcat-7.4.3.36-ga36-20220802152002501.tar.gz");
			put(
				"7.4.3.37",
				LIFERAY_PORTAL_URL + "7.4.3.37-ga37/liferay-ce-portal-tomcat-7.4.3.37-ga37-20220810074517865.tar.gz");
			put(
				"7.4.3.38",
				LIFERAY_PORTAL_URL + "7.4.3.38-ga38/liferay-ce-portal-tomcat-7.4.3.38-ga38-20220816172522381.tar.gz");
			put(
				"7.4.3.39",
				LIFERAY_PORTAL_URL + "7.4.3.39-ga39/liferay-ce-portal-tomcat-7.4.3.39-ga39-20220824100312686.tar.gz");
			put(
				"7.4.3.40",
				LIFERAY_PORTAL_URL + "7.4.3.40-ga40/liferay-ce-portal-tomcat-7.4.3.40-ga40-20220901103436940.tar.gz");
			put(
				"7.4.3.41",
				LIFERAY_PORTAL_URL + "7.4.3.41-ga41/liferay-ce-portal-tomcat-7.4.3.41-ga41-20220902171954397.tar.gz");
			put(
				"7.4.3.42",
				LIFERAY_PORTAL_URL + "7.4.3.42-ga42/liferay-ce-portal-tomcat-7.4.3.42-ga42-20220913145951615.tar.gz");
			put(
				"7.4.3.43",
				LIFERAY_PORTAL_URL + "7.4.3.43-ga43/liferay-ce-portal-tomcat-7.4.3.43-ga43-20220921132136623.tar.gz");
			put(
				"7.4.3.44",
				LIFERAY_PORTAL_URL + "7.4.3.44-ga44/liferay-ce-portal-tomcat-7.4.3.44-ga44-20220928155119787.tar.gz");
			put(
				"7.4.3.45",
				LIFERAY_PORTAL_URL + "7.4.3.45-ga45/liferay-ce-portal-tomcat-7.4.3.45-ga45-20221005161655738.tar.gz");
			put(
				"7.4.3.46",
				LIFERAY_PORTAL_URL + "7.4.3.46-ga46/liferay-ce-portal-tomcat-7.4.3.46-ga46-20221012072050357.tar.gz");
			put(
				"7.4.3.47",
				LIFERAY_PORTAL_URL + "7.4.3.47-ga47/liferay-ce-portal-tomcat-7.4.3.47-ga47-20221020194008980.tar.gz");
			put(
				"7.4.3.48",
				LIFERAY_PORTAL_URL + "7.4.3.48-ga48/liferay-ce-portal-tomcat-7.4.3.48-ga48-20221024170919019.tar.gz");
			put(
				"7.4.3.49",
				LIFERAY_PORTAL_URL + "7.4.3.49-ga49/liferay-ce-portal-tomcat-7.4.3.49-ga49-20221101182812798.tar.gz");
			put(
				"7.4.3.50",
				LIFERAY_PORTAL_URL + "7.4.3.50-ga50/liferay-ce-portal-tomcat-7.4.3.50-ga50-20221109133624287.tar.gz");
		}
	};
	public static final Map<String, String[]> liferayTargetPlatformVersions = new HashMap<String, String[]>() {
		{
			put("7.0", new String[] {"7.0.6-2"});
			put("7.1", new String[] {"7.1.3-1", "7.1.2", "7.1.1", "7.1.0"});
			put("7.2", new String[] {"7.2.1-1", "7.2.0"});
			put("7.3", new String[] {"7.3.7", "7.3.6", "7.3.5", "7.3.4", "7.3.3-1", "7.3.2-1", "7.3.1-1", "7.3.0-1"});
			put(
				"7.4",
				new String[] {
					"7.4.3.50", "7.4.3.49", "7.4.3.48", "7.4.3.47", "7.4.3.46", "7.4.3.45", "7.4.3.44", "7.4.3.43",
					"7.4.3.42", "7.4.3.41", "7.4.3.40", "7.4.3.39", "7.4.3.38", "7.4.3.37", "7.4.3.36", "7.4.3.35",
					"7.4.3.34", "7.4.3.32", "7.4.3.31", "7.4.3.30", "7.4.3.29", "7.4.3.28", "7.4.3.27", "7.4.3.26",
					"7.4.3.25", "7.4.3.24", "7.4.3.23", "7.4.3.22", "7.4.3.21", "7.4.3.20", "7.4.3.19", "7.4.3.18",
					"7.4.3.17", "7.4.3.16", "7.4.3.15", "7.4.3.14", "7.4.3.13", "7.4.3.12", "7.4.3.11", "7.4.3.10",
					"7.4.3.9", "7.4.3.8", "7.4.3.7", "7.4.3.6", "7.4.3.5", "7.4.3.4", "7.4.2-1", "7.4.1-1", "7.4.0"
				});
		}
	};

}