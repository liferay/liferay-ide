<aui:form action="<%= configurationURL %>" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveConfiguration();" %>'>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<aui:fieldset>
		<aui:select label="vocabularies" name="preferences--allAssetVocabularies--">
			<aui:option label="all" selected="<%= allAssetVocabularies %>" value="<%= true %>" />
			<aui:option label="filter[action]" selected="<%= !allAssetVocabularies %>" value="<%= false %>" />
		</aui:select>

		<aui:input name="preferences--assetVocabularyIds--" type="hidden" />

		<%
		Set<Long> availableAssetVocabularyIdsSet = SetUtil.fromArray(availableAssetVocabularyIds);

		// Left list

		List<KeyValuePair> typesLeftList = new ArrayList<KeyValuePair>();

		for (long assetVocabularyId : assetVocabularyIds) {
			try {
				AssetVocabulary assetVocabulary = AssetVocabularyLocalServiceUtil.getVocabulary(assetVocabularyId);

				assetVocabulary = assetVocabulary.toEscapedModel();

				typesLeftList.add(new KeyValuePair(String.valueOf(assetVocabularyId), _getTitle(assetVocabulary, themeDisplay)));
			}
			catch (NoSuchVocabularyException nsve) {
			}
		}

		// Right list

		List<KeyValuePair> typesRightList = new ArrayList<KeyValuePair>();

		Arrays.sort(assetVocabularyIds);

		for (long assetVocabularyId : availableAssetVocabularyIdsSet) {
			if (Arrays.binarySearch(assetVocabularyIds, assetVocabularyId) < 0) {
				AssetVocabulary assetVocabulary = AssetVocabularyLocalServiceUtil.getVocabulary(assetVocabularyId);

				assetVocabulary = assetVocabulary.toEscapedModel();

				typesRightList.add(new KeyValuePair(String.valueOf(assetVocabularyId), _getTitle(assetVocabulary, themeDisplay)));
			}
		}

		typesRightList = ListUtil.sort(typesRightList, new KeyValuePairComparator(false, true));
		%>

		<div class="<%= allAssetVocabularies ? "hide" : "" %>" id="<portlet:namespace />assetVocabulariesBoxes">
			<liferay-ui:input-move-boxes
				leftBoxName="currentAssetVocabularyIds"
				leftList="<%= typesLeftList %>"
				leftReorder="true"
				leftTitle="current"
				rightBoxName="availableAssetVocabularyIds"
				rightList="<%= typesRightList %>"
				rightTitle="available"
			/>
		</div>

		<div class="display-template">

			<%
			TemplateHandler templateHandler = TemplateHandlerRegistryUtil.getTemplateHandler(AssetCategory.class.getName());
			%>

			<liferay-ui:ddm-template-selector
				classNameId="<%= PortalUtil.getClassNameId(templateHandler.getClassName()) %>"
				displayStyle="<%= displayStyle %>"
				displayStyleGroupId="<%= displayStyleGroupId %>"
				refreshURL="<%= currentURL %>"
				showEmptyOption="<%= true %>"
			/>
		</div>
	</aui:fieldset>

	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>