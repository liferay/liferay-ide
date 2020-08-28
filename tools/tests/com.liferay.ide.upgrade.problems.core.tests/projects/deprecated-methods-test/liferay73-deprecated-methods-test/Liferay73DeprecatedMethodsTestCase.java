package cz.datalite.zk.liferay.mock;

import com.liferay.document.library.kernel.util.DLUtil;

import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;


import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.messaging.proxy.ProxyModeThreadLocal;
import com.liferay.portal.kernel.model.CompanyWrapper;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.VirtualHostLocalServiceWrapper;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.output.stream.container.OutputStreamContainerFactoryTracker;
import com.liferay.portal.search.elasticsearch6.internal.document.DefaultElasticsearchDocumentFactory;
import com.liferay.portal.search.elasticsearch6.internal.document.ElasticsearchDocumentFactory;
import com.liferay.portal.security.ldap.util.LDAPUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersionWrapper;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;

import java.lang.String;

import java.util.Date;

public class Liferay73DeprecatedMethodsTestCase {

	public void testDeprecatedMethod() {
		
		String dLFileEntryControlPanelLink = DLUtil.getDLFileEntryControlPanelLink(portletRequest, dlFileEntry.getFileEntryId());
		
		ProxyModeThreadLocal.setForceSync(forceSync);
		
		_ddmFormFieldValidation.setExpression("*/+");
		
		_outputStreamContainerFactoryTracker.runWithSwappedLog(new AllVerifiersRunnable(outputStream, force),outputStreamContainer.getDescription(), outputStream);

		String elasticsearchDocument = _elasticsearchDocumentFactory.getElasticsearchDocument(document);
		
		int count = _workflowInstanceManager.searchCount(companyId, userId, assetType, nodeName, kaleoDefinitionName, completed);
		
		JSONArray valueJSONArray = JSONUtil.put(cpDefinitionOptionValueRel.getKey());
		
		List<KaleoInstance> kaleoInstanceList = _kaleoInstanceLocalService.search(userId, assetClassName, nodeName, kaleoDefinitionName, completed, start, end, orderByComparator, serviceContext);
		
		String baseDN = LDAPUtil.escapeCharacters(ldapServerConfiguration.baseDN());
		
		LayoutSets returnValue = LayoutSetServiceUtil.updateVirtualHost(groupId, privateLayout, virtualHost);
		
		boolean hasIncompleteKaleoInstances = _kaleoDefinitionVersionWrapper.hasIncompleteKaleoInstances();

		KaleoDefinition kaleoDefinition = _kaleoDefinitionVersionWrapper.fetchKaleoDefinition();
		
		boolean isJOnAS = ServerDetector.isJOnAS();
		
		boolean isGlassfish = ServerDetector.isGlassfish();
		
		String virtualHostname = PortalUtil.getVirtualHostname(_layoutSet);
		
		boolean isSendPassword = _companyWrapper.isSendPassword();
		
		VirtualHost _virtualHost = _virtualHostLocalServiceWrapper.fetchVirtualHost(_companyId, _layoutSetId);
		
	}
	
	@Reference
	private OutputStreamContainerFactoryTracker _outputStreamContainerFactoryTracker;
	
	private DDMFormFieldValidation _ddmFormFieldValidation = new DDMFormFieldValidation();
	
	private ElasticsearchDocumentFactory _elasticsearchDocumentFactory = new DefaultElasticsearchDocumentFactory();
	
	private static WorkflowInstanceManager _workflowInstanceManager;
	
	private KaleoInstanceLocalService _kaleoInstanceLocalService;
	
	@Reference
	private NPMRegistry _npmRegistry;
	
	private KaleoDefinitionVersionWrapper _kaleoDefinitionVersionWrapper = new KaleoDefinitionVersionWrapper(kaleoDefinitionVersion);
	
	Reference
	private LayoutSetLocalService _layoutSetLocalService;
	
	private LayoutSet _layoutSet = _layoutSetLocalService.getLayoutSet(groupId, false);
	
	private CompanyWrapper _companyWrapper = new CompanyWrapper(company);
	
	private long _companyId = 1;
	
	private long _layoutSetId = 1;
	
	private VirtualHostLocalServiceWrapper _virtualHostLocalServiceWrapper = new VirtualHostLocalServiceWrapper(virtualHostLocalService);
	
	private Document _document;
}