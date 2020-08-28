package cz.datalite.zk.liferay.mock;

import com.liferay.bean.portlet.cdi.extension.internal.BeanPortletInvokerPortlet;

import com.liferay.calendar.model.CalendarBookingWrapper;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.util.DLProcessorRegistry;
import com.liferay.document.library.kernel.util.DLProcessorRegistryUtil;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.document.library.kernel.util.DLValidatorUtil;

import com.liferay.exportimport.internal.lar.ExportImportProcessCallbackUtil;

import com.liferay.petra.encryptor.Encryptor;

import com.liferay.portal.dao.jdbc.aop.DefaultDynamicDataSourceTargetSource;
import com.liferay.portal.deploy.DeployUtil;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.spring.transaction.TransactionStatusAdapter;
import com.liferay.portal.tools.ToolsUtil;

import com.liferay.reading.time.model.ReadingTimeEntryWrapper;

import java.security.Provider;

import java.lang.String;

import java.util.concurrent.Callable;
import java.util.List;
import java.util.Stack;

import org.springframework.transaction.PlatformTransactionManager;

public class Liferay72DeprecatedMethodsTestCase {
	
	public void testMethod() {
		
		DLAppLocalServiceUtil.deleteFileRanksByFileEntryId(_fileEntryId);
		
		DLAppLocalServiceUtil.deleteFileRanksByUserId(_userId);
		
		List<DLFileEntry> dLFileEntryList = _dLFileEntryLocalServiceWrapper.getMisversionedFileEntries();
		
		List<DLFileEntry> _dLFileEntryList = _dLFileEntryLocalServiceWrapper.getOrphanedFileEntries();
		
		DLValidator dLValidator = DLValidatorUtil.getDLValidator();
		
		DLValidatorUtil.setDLValidator(dLValidator);
		
		DLProcessorRegistry dLProcessorRegistry = DLProcessorRegistryUtil.getDLProcessorRegistry();
		
		DLProcessorRegistryUtil.setDLProcessorRegistry(dLProcessorRegistry);
		
		Stack<String> stack = _dynamicDataSourceTargetSource.getMethodStack();
		
		_dynamicDataSourceTargetSource.pushMethod(targetClass.getName());
		
		String resourcePath = DeployUtil.getResourcePath("/");
		
		PlatformTransactionManager platformTransactionManager = _transactionStatusAdapter.getPlatformTransactionManager();
		
		String qualifiedClassNames = ToolsUtil.stripFullyQualifiedClassNames(StringUtil.replaceFirst(content, imports, newImports););
		
		boolean isStrutsBridgePortlet = _beanPortletInvokerPortlet.isStrutsBridgePortlet();
		
		boolean isStrutsPortlet = _beanPortletInvokerPortlet.isStrutsPortlet();
		
		List<Callable<?>> callbackList = ExportImportProcessCallbackUtil.popCallbackList();
		
		ExportImportProcessCallbackUtil.pushCallbackList();
		
		TrashHandler trashHandler = _calendarBookingWrapper.getTrashHandler();
		
		Provider provider = Encryptor.getProvider();
		
		TrashHandler _trashHandler = _readingTimeEntryWrapper.getTrashHandler();
	}
	
	private long _fileEntryId = 1;
	
	private long _userId = 1;
	
	private DLFileEntryLocalServiceWrapper _dLFileEntryLocalServiceWrapper = new DLFileEntryLocalServiceWrapper(dlFileEntryLocalService);
			
	private DefaultDynamicDataSourceTargetSource _dynamicDataSourceTargetSource = new DefaultDynamicDataSourceTargetSource();

	private TransactionStatusAdapter _transactionStatusAdapter = new TransactionStatusAdapter(transactionStatus);
	
	private BeanPortletInvokerPortlet _beanPortletInvokerPortlet = new BeanPortletInvokerPortlet(beanPortlet.getBeanMethods());

	private	CalendarBookingWrapper _calendarBookingWrapper = new CalendarBookingWrapper(_calendarBooking.toUnescapedModel());

	private ReadingTimeEntryWrapper _readingTimeEntryWrapper = new ReadingTimeEntryWrapper((ReadingTimeEntry)_readingTimeEntry.clone());
}