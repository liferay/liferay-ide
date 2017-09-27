package com.matrimony.util;

import java.util.List;
import java.util.Locale;

import javax.portlet.PortletURL;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.matrimony.constant.ProfileConstants;
import com.matrimony.model.Profile;
import com.matrimony.service.ProfileLocalServiceUtil;

public final class ProfileIndexer extends BaseIndexer {

	private static final String ERROR_WHILE_REINDEXING_PROFILE = "Error While Reindexing Profile: ";

	private static final String ERROR_WHILE_DELETING_PROFILE = "Error While Deleting Profile: ";

	private static final Log LOGGER = LogFactoryUtil
			.getLog(ProfileIndexer.class);

	public static final String[] CLASS_NAMES = { Profile.class.getName() };

	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	public String getPortletId() {
		return MatrimonyPropsValues.PROFILE_PORTLET_ID;
	}

	@Override
	protected void doDelete(Object obj) throws Exception {
		Profile profile = (Profile) obj;
		if (Validator.isNotNull(profile)) {
			try {
				SearchEngineUtil.deletePortletDocuments(getSearchEngineId(),
						profile.getCompanyId(),
						MatrimonyPropsValues.PROFILE_PORTLET_ID);
			} catch (SearchException e) {
				LOGGER.error(
						ERROR_WHILE_DELETING_PROFILE + profile.getProfileId()
								+ StringPool.COMMA_AND_SPACE + e.getMessage(),
						e);
			}
		}
	}

	@Override
	protected Document doGetDocument(Object obj) throws Exception {
		return null;
	}

	@Override
	protected Summary doGetSummary(Document document, Locale locale,
			String snippet, PortletURL portletURL) throws Exception {
		return null;
	}

	@Override
	protected void doReindex(Object obj) throws Exception {
		Profile profile = (Profile) obj;
		Document document = getProfileEntryDocument(profile);
		SearchEngineUtil.updateDocument(getSearchEngineId(),
				profile.getCompanyId(), document);
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		try {
			Profile profile = ProfileLocalServiceUtil.getProfile(classPK);
			doReindex(profile);
		} catch (Exception e) {
			throw new SearchException(e);
		}
	}

	@Override
	protected String getPortletId(SearchContext searchContext) {
		return MatrimonyPropsValues.PROFILE_PORTLET_ID;
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		if (SearchEngineUtil.isIndexReadOnly()) {
			return;
		}
		long companyId = GetterUtil.getLong(ids[0]);

		try {
			reIndexProfile(companyId);
		} catch (SystemException se) {
			LOGGER.error(ERROR_WHILE_REINDEXING_PROFILE + se.getMessage(), se);
		}
	}

	protected void reIndexProfile(long companyId) throws SystemException {

		List<Profile> profileList = ProfileLocalServiceUtil.getProfiles(-1, -1);
		try {
			SearchEngineUtil.deletePortletDocuments(getSearchEngineId(),
					companyId, MatrimonyPropsValues.PROFILE_PORTLET_ID);
		} catch (SearchException e1) {
			LOGGER.error(ERROR_WHILE_REINDEXING_PROFILE + e1.getMessage(), e1);
		}
		for (Profile profile : profileList) {
			try {
				doReindex(profile);
			} catch (Exception e) {
				LOGGER.error(ERROR_WHILE_REINDEXING_PROFILE + e.getMessage(), e);
			}
		}

	}

	public void addProfile(Profile profile) throws SearchException {
		Document document = getProfileEntryDocument(profile);

		if (document != null) {
			SearchEngineUtil.addDocument(getSearchEngineId(),
					profile.getCompanyId(), document);
		}
	}

	public Document getProfileEntryDocument(Profile profile) {
		Document document = new DocumentImpl();
		document.addUID(MatrimonyPropsValues.PROFILE_PORTLET_ID,
				profile.getProfileId());

		document.addDate(Field.CREATE_DATE, profile.getCreatedDate());
		document.addDate(Field.MODIFIED_DATE, profile.getModifiedDate());
		document.addKeyword(Field.ENTRY_CLASS_NAME, Profile.class.getName());
		document.addKeyword(Field.ENTRY_CLASS_PK, profile.getProfileId());
		document.addKeyword(Field.COMPANY_ID, profile.getCompanyId());
		document.addKeyword(Field.USER_ID, profile.getCreatedBy());
		document.addKeyword(Field.GROUP_ID, profile.getGroupId());
		document.addKeyword(Field.TITLE, profile.getProfileCode());

		document.addKeyword(ProfileConstants.PROFILE_STATUS,
				profile.getStatus());
		document.addKeyword(ProfileConstants.PROFILE_ANNUAL_INCOME,
				profile.getAnnualIncome());
		document.addKeyword(ProfileConstants.PROFILE_CHILDREN,
				profile.getChildren());
		document.addKeyword(ProfileConstants.PROFILE_COMPLEXION,
				profile.getComplexion());
		document.addKeyword(ProfileConstants.PROFILE_CREATED_FOR_MY,
				profile.getCreatedForMy());
		document.addKeyword(ProfileConstants.PROFILE_CURRENCY,
				profile.getCurrency());
		document.addKeyword(ProfileConstants.PROFILE_DOSAM, profile.getDosam());

		document.addText(Field.PORTLET_ID,
				MatrimonyPropsValues.PROFILE_PORTLET_ID);
		document.addText(ProfileConstants.PROFILE_EDUCATION,
				profile.getEducation());
		document.addText(ProfileConstants.PROFILE_FAMILY_STATUS,
				profile.getFamilyStatus());
		document.addText(ProfileConstants.PROFILE_FAMILY_TYPE,
				profile.getFamilyType());
		document.addText(ProfileConstants.PROFILE_FAMILY_VALUE,
				profile.getFamilyValue());
		document.addText(ProfileConstants.PROFILE_HEIGHT, profile.getHeight());
		document.addText(ProfileConstants.PROFILE_MARITAL_STATUS,
				profile.getMaritalStatus());
		document.addText(ProfileConstants.PROFILE_PROFESSION,
				profile.getProfession());
		document.addText(ProfileConstants.PROFILE_RASI, profile.getRasi());
		document.addText(ProfileConstants.PROFILE_STAR, profile.getStar());
		document.addText(ProfileConstants.PROFILE_WEIGHT, profile.getWeight());
		document.addKeyword(ProfileConstants.PROFILE_COUNTRY, profile.getCountry());
		document.addKeyword(ProfileConstants.PROFILE_STATE, profile.getState());
		document.addKeyword(ProfileConstants.PROFILE_CITY, profile.getCity());
		document.addKeyword(ProfileConstants.PROFILE_CASTE, profile.getCaste());
		document.addKeyword(ProfileConstants.PROFILE_RELIGION,
				profile.getReligion());
		document.addKeyword(ProfileConstants.PROFILE_SUB_CASTE,
				profile.getSubCaste());
		return document;
	}

	public void updateProfile(Profile profile) throws SearchException {
		Document document = getProfileEntryDocument(profile);
		SearchEngineUtil.updateDocument(getSearchEngineId(),
				profile.getCompanyId(), document);
	}

	public String getProfileUID(long groupId, String profileId) {
		Document document = new DocumentImpl();
		document.addUID(MatrimonyPropsValues.PROFILE_PORTLET_ID, groupId,
				profileId);
		return document.get(Field.UID);
	}
}