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

package com.liferay.portal.kernel.search.facet;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Raymond Augé
 */
public class AssetEntriesFacet extends MultiValueFacet {

	public AssetEntriesFacet(SearchContext searchContext) {
		super(searchContext);

		setFieldName(Field.ENTRY_CLASS_NAME);

		initFacetClause();
	}

	@Override
	public void setFacetConfiguration(FacetConfiguration facetConfiguration) {
		super.setFacetConfiguration(facetConfiguration);

		initFacetClause();
	}

	@Override
	protected BooleanClause doGetFacetClause() {
		SearchContext searchContext = getSearchContext();

		String[] entryClassNames = searchContext.getEntryClassNames();

		BooleanQuery facetQuery = BooleanQueryFactoryUtil.create(searchContext);

		for (String entryClassName : entryClassNames) {
			Indexer indexer = IndexerRegistryUtil.getIndexer(entryClassName);

			if (indexer == null) {
				continue;
			}

			String searchEngineId = searchContext.getSearchEngineId();

			if (!searchEngineId.equals(indexer.getSearchEngineId())) {
				continue;
			}

			try {
				BooleanQuery indexerBooleanQuery = indexer.getFacetQuery(
					entryClassName, searchContext);

				if ((indexerBooleanQuery == null) ||
					!indexerBooleanQuery.hasClauses()) {

					continue;
				}

				BooleanQuery entityQuery = BooleanQueryFactoryUtil.create(
					searchContext);

				entityQuery.add(indexerBooleanQuery, BooleanClauseOccur.MUST);

				indexer.postProcessContextQuery(entityQuery, searchContext);

				for (IndexerPostProcessor indexerPostProcessor :
						indexer.getIndexerPostProcessors()) {

					indexerPostProcessor.postProcessContextQuery(
						entityQuery, searchContext);
				}

				if (indexer.isStagingAware()) {
					if (!searchContext.isIncludeLiveGroups() &&
						searchContext.isIncludeStagingGroups()) {

						entityQuery.addRequiredTerm(Field.STAGING_GROUP, true);
					}
					else if (searchContext.isIncludeLiveGroups() &&
							 !searchContext.isIncludeStagingGroups()) {

						entityQuery.addRequiredTerm(Field.STAGING_GROUP, false);
					}
				}

				if (entityQuery.hasClauses()) {
					facetQuery.add(entityQuery, BooleanClauseOccur.SHOULD);
				}
			}
			catch (Exception e) {
				_log.error(e, e);
			}
		}

		if (!facetQuery.hasClauses()) {
			return null;
		}

		return BooleanClauseFactoryUtil.create(
			searchContext, facetQuery, BooleanClauseOccur.MUST.getName());
	}

	protected void initFacetClause() {
		SearchContext searchContext = getSearchContext();

		FacetConfiguration facetConfiguration = getFacetConfiguration();

		JSONObject dataJSONObject = facetConfiguration.getData();

		String[] entryClassNames = null;

		if (dataJSONObject.has("values")) {
			JSONArray valuesJSONArray = dataJSONObject.getJSONArray("values");

			entryClassNames = new String[valuesJSONArray.length()];

			for (int i = 0; i < valuesJSONArray.length(); i++) {
				entryClassNames[i] = valuesJSONArray.getString(i);
			}
		}

		if (ArrayUtil.isEmpty(entryClassNames)) {
			entryClassNames = searchContext.getEntryClassNames();
		}

		if (!isStatic()) {
			String[] entryClassNameParam = StringUtil.split(
				GetterUtil.getString(
					searchContext.getAttribute(getFieldName())));

			if (ArrayUtil.isNotEmpty(entryClassNameParam)) {
				entryClassNames = entryClassNameParam;
			}
		}

		if (ArrayUtil.isEmpty(entryClassNames)) {
			entryClassNames = SearchEngineUtil.getEntryClassNames();

			if (!dataJSONObject.has("values")) {
				JSONArray entriesJSONArray = JSONFactoryUtil.createJSONArray();

				for (String entryClassName : entryClassNames) {
					entriesJSONArray.put(entryClassName);
				}

				dataJSONObject.put("values", entriesJSONArray);
			}
		}

		searchContext.setEntryClassNames(entryClassNames);
	}

	private static Log _log = LogFactoryUtil.getLog(AssetEntriesFacet.class);

}