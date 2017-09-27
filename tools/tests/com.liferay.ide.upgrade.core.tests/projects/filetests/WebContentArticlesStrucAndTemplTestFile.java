import java.util.HashMap;
import java.util.Locale;

import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.util.TestPropsValues;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalArticleServiceUtil;

public class WebContentArticlesStrucAndTempl {
	public static void main(String[] args){

		JournalArticleLocalServiceUtil.addArticle(
			TestPropsValues.getUserId(), group.getGroupId(), 0, 0, 0,
			StringPool.BLANK, true, 1, titleMap, new HashMap<Locale, String>(),
			sb.toString(), "general", var, var, layout.getUuid(), 1, 1, 1965,
			0, 0, 0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0, true, false, false, var,
			var, var, StringPool.BLANK, serviceContext);

		JournalArticleServiceUtil.addArticle(
			groupId, folderId, classNameId, classPK, articleId,
			autoArticleId, titleMap, descriptionMap, content, type,
			structureId, templateId, layoutUuid, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, reviewDateMonth, reviewDateDay, reviewDateYear,
			reviewDateHour, reviewDateMinute, neverReview, indexable,
			smallImage, smallImageURL, smallFile, images, articleURL,
			serviceContext);
	}
}
