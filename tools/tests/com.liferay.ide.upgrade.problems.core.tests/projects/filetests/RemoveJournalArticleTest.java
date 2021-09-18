
public class RemoveJournalArticleTest {

	@Test
	public void testInvocationMethod() throws Exception {

		_journalArticleModel.getContent();
		_journalArticleModel.setContent("test");
		
		_journalArticleSoap.getContent();
		_journalArticleSoap.setContent("test");
		
		_journalArticleWrapper.setContent("test");
		
		_journalArticleLocalService.checkNewLine();
		_journalArticleLocalService.setContent("test");
		
		JournalArticleLocalServiceUtil.checkNewLine();
		JournalArticleLocalServiceUtil.updateContent("test");
		
		_journalArticleLocalServiceWrapper.checkNewLine();
		_journalArticleLocalServiceWrapper.updateContent("test");
		
		_journalArticleService.updateContent("test");
		
		JournalArticleServiceUtil.updateContent("test");
		
		_journalArticleServiceWrapper.updateContent("test");
		
		_journalArticleImpl.setContent("test");
		
		_journalArticleModelImpl.getContent();
		
		_journalArticleModelImpl.setContent("test");
		
		JournalArticleServiceHttp.updateContent("test");
		
		JournalArticleServiceSoap.updateContent("test");
		
		_journalArticleLocalServiceImpl.checkNewLine();
		
		_journalArticleLocalServiceImpl.updateContent();
		
		_journalArticleServiceImpl.updateContent("test");
		
	}
	
	@Reference
	private JournalArticleModel _journalArticleModel;

	@Reference
	private JournalArticleSoap _journalArticleSoap;
	
	@Reference
	private JournalArticleWrapper _journalArticleWrapper;
	
	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalArticleLocalServiceWrapper _journalArticleLocalServiceWrapper;

	@Reference
	private JournalArticleService _journalArticleService;
	
	@Reference
	private JournalArticleServiceWrapper _journalArticleServiceWrapper;

	@Reference
	private JournalArticleLocalServiceWrapper _journalArticleLocalServiceWrapper;

	@Reference
	private JournalArticleImpl _journalArticleImpl;
	
	@Reference
	private JournalArticleModelImpl _journalArticleModelImpl;
	
	@Reference
	private JournalArticleServiceHttp _journalArticleServiceHttp;
	
	@Reference
	private JournalArticleServiceSoap _journalArticleServiceSoap;
	
	@Reference
	private JournalArticleLocalServiceImpl _journalArticleLocalServiceImpl;
	
	@Reference
	private  JournalArticleServiceImpl _journalArticleServiceImpl;
}
