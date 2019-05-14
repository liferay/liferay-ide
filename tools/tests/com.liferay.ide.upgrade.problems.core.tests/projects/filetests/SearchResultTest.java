package blade.migrate.liferay70;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletURL;

import org.eclipse.jdt.core.search.SearchEngine;

import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;

public class SearchResultTest {
	public static List<SearchResult> getSearchResults(
			Hits hits, Locale locale, PortletURL portletURL) {

			List<SearchResult> searchResults = new ArrayList<SearchResult>();
			for (Document document : hits.getDocs()) {
				String entryClassName = GetterUtil.getString(
					document.get(Field.ENTRY_CLASS_NAME));
				long entryClassPK = GetterUtil.getLong(
					document.get(Field.ENTRY_CLASS_PK));

				try {
					String className = entryClassName;
					long classPK = entryClassPK;

					FileEntry fileEntry = null;
					MBMessage mbMessage = null;

					if (entryClassName.equals(DLFileEntry.class.getName()) ||
						entryClassName.equals(MBMessage.class.getName())) {

						classPK = GetterUtil.getLong(document.get(Field.CLASS_PK));
						long classNameId = GetterUtil.getLong(
							document.get(Field.CLASS_NAME_ID));

						if ((classPK > 0) && (classNameId > 0)) {
							className = PortalUtil.getClassName(classNameId);

							if (entryClassName.equals(
									DLFileEntry.class.getName())) {

								fileEntry = DLAppLocalServiceUtil.getFileEntry(
									entryClassPK);
							}
							else if (entryClassName.equals(
										MBMessage.class.getName())) {

								mbMessage = MBMessageLocalServiceUtil.getMessage(
									entryClassPK);
							}
						}
						else {
							className = entryClassName;
							classPK = entryClassPK;
						}
					}

					SearchResult searchResult = new SearchResult(
						className, classPK);

					int index = searchResults.indexOf(searchResult);

					if (index < 0) {
						searchResults.add(searchResult);
					}
					else {
						searchResult = searchResults.get(index);
					}

					if (fileEntry != null) {
						Summary summary = getSummary(
							document, DLFileEntry.class.getName(),
							fileEntry.getFileEntryId(), locale, portletURL);

						searchResult.addFileEntry(fileEntry, summary);
					}

					if (mbMessage != null) {
						searchResult.addMBMessage(mbMessage);
						searchResult.getMBMessages();
						searchResult.getFileEntryTuples();
					}

					if (entryClassName.equals(JournalArticle.class.getName())) {
						String version = document.get(Field.VERSION);

						searchResult.addVersion(version);
					}

					if ((mbMessage == null) && (fileEntry == null)) {
						Summary summary = getSummary(
							document, className, classPK, locale, portletURL);

						searchResult.setSummary(summary);
					}
					else {
						if (searchResult.getSummary() == null) {
							Summary summary = getSummary(
								className, classPK, locale, portletURL);

							searchResult.setSummary(summary);
						}
					}
				}
				catch (Exception e) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Search index is stale and contains entry {" +
								entryClassPK + "}");
					}
				}
			}

			return searchResults;
		}
}