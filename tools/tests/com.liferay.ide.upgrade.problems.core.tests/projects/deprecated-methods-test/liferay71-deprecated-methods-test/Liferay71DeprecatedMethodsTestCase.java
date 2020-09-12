package cz.datalite.zk.liferay.mock;

import com.liferay.bookmarks.service.BookmarksFolderServiceUtil;

import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.staging.Staging;

import com.liferay.message.boards.kernel.model.MBMessage;

import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.cache.SingleVMPoolUtil;
import com.liferay.portal.kernel.model.LayoutWrapper;
import com.liferay.portal.kernel.search.BooleanClauseFactory;
import com.liferay.portal.kernel.search.BooleanQueryFactory;
import com.liferay.portal.kernel.search.SearchEngineProxyWrapper;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.security.auth.AuthTokenWhitelistUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.repository.util.ExternalRepositoryFactoryUtil;

import com.liferay.util.servlet.NullSession;

import java.lang.String;

import java.util.Map;
import java.util.List;
import java.util.Set;

public class Liferay71DeprecatedMethodsTestCase {

	public void testDeprecatedMethod() {
		
		String[] valueNames = _portalSession.getValueNames();
		
		_portalSession.removeValue("");
		
		Map<String, String[]> stagingParameters = _staging.getStagingParameters();
		
		_staging.lockGroup(_userId, _groupId);
		
		Map<String, long[]> assetCategoryIdsMap = _portletDataContext.getAssetCategoryIdsMap();
		
		Map<String, List<MBMessage>> comments = _portletDataContext.getComments();
		
		Layout exportableLayout = ExportImportHelperUtil.getExportableLayout(_themeDisplay);
		
		String exportLayoutReferences = ExportImportHelperUtil.replaceExportLayoutReferences(_portletDataContext, "");
		
		Set<String> sets = AuthTokenWhitelistUtil.resetPortletCSRFWhitelist();
		
		Set<String> _sets = AuthTokenWhitelistUtil.resetPortletInvocationWhitelist();
		
		String unicodeProperties = _unicodeProperties.toSortedString();
		
		SingleVMPoolUtil.removeCache("");
		
		_portalCache = MultiVMPoolUtil.getCache("");
		
		BooleanClauseFactory booleanClauseFactory = _searchEngineProxyWrapper.getBooleanClauseFactory();
		
		BooleanQueryFactory booleanQueryFactory = _searchEngineProxyWrapper.getBooleanQueryFactory();
		
		boolean isIndexReadOnly = SearchEngineUtil.isIndexReadOnly();
		
		SearchEngineUtil.setIndexReadOnly(isIndexReadOnly);
		
		boolean isTypeArticle = _layoutWrapper.isTypeArticle();
		
		String[] externalRepositoryClassNames = ExternalRepositoryFactoryUtil.getExternalRepositoryClassNames();
		
		BookmarksFolderServiceUtil.getSubfolderIds(ListUtil.toList(folderIds), _groupId, _folderId);
	}
	
	private NullSession _portalSession = new NullSession();
	
	@Reference
	private Staging _staging;
	
	private long _userId = 1;

	private long _groupId = 1;
	
	private long _folderId = 1;
	
	private PortletDataContext _portletDataContext;
	
	private ThemeDisplay _themeDisplay;
	
	private UnicodeProperties _unicodeProperties = new UnicodeProperties();
	
	private PortalCache<String, Serializable> _portalCache;
	
	private SearchEngineProxyWrapper _searchEngineProxyWrapper = new SearchEngineProxyWrapper(searchEngine, getIndexSearcher(), getIndexWriter());
	
	private LayoutWrapper _layoutWrapper = new LayoutWrapper(_layout.toEscapedModel());
}