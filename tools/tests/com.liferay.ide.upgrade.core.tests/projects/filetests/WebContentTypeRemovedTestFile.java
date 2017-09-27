import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.service.ServiceTestUtil;
import com.liferay.portal.util.TestPropsValues;
import com.liferay.portlet.journal.ArticleTypeException;
import com.liferay.portlet.journal.model.JournalArticleConstants;
import com.liferay.portlet.journal.model.JournalFolderConstants;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalArticleServiceUtil;
import com.liferay.portlet.journal.service.JournalFeedLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalFeedServiceUtil;
import com.liferay.portlet.journal.util.JournalTestUtil;

import java.util.Calendar;
public class WebContentTypeRemovedTestFile {

	public static void main(String[] args) {
		JournalArticle journalArticle = null;
		journalArticle.getType();

		try {
			JournalFeed journalFeed = null;
			journalFeed.getType();
		}catch (ArticleTypeException e) {
		}

		JournalArticleLocalServiceUtil.addArticle(
			TestPropsValues.getUserId(), groupId,
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT, id, StringPool.BLANK,
			true, JournalArticleConstants.VERSION_DEFAULT, titleMap,
			descriptionMap,
			JournalTestUtil.createLocalizedContent(
				ServiceTestUtil.randomString(), LocaleUtil.getDefault()),
			"general", var, var, var, displayDateCalendar.get(Calendar.MONTH),
			displayDateCalendar.get(Calendar.DAY_OF_MONTH),
			displayDateCalendar.get(Calendar.YEAR),
			displayDateCalendar.get(Calendar.HOUR_OF_DAY),
			displayDateCalendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, 0, 0,
			0, 0, 0, true, true, false, var, var, var, var, serviceContext);

		JournalArticleServiceUtil.search(companyId, groupId,
			ListUtil.toList(folderIds), classNameId, keywords, version, type,
			ddmStructureKey, ddmTemplateKey, displayDateGT, displayDateLT,
			status, reviewDate, start, end, obc);

		JournalFeedLocalServiceUtil.addFeed(
				userId, groupId, feedId, autoFeedId, name, description, type,
				ddmStructureKey, ddmTemplateKey, rendererTemplateKey, delta,
				orderByCol, orderByType, friendlyURL, targetPortletId,
				contentField, feedFormat, feedVersion, serviceContext);

		JournalFeedServiceUtil.addFeed(groupId,
				feedId, autoFeedId, name, description, type, structureId,
				templateId, rendererTemplateId, delta, orderByCol, orderByType,
				targetLayoutFriendlyUrl, targetPortletId, contentField,
				feedType, feedVersion, serviceContext);
	}

}
