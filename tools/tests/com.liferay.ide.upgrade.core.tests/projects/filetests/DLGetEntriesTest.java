
public class DLDLImplDLUtilgetEntriesTest {

	public void testMethod(DLImpl dlImpl){
		DL dl = new DLImpl();
		Hits hits = null;
		//var call
		dl.getEntries(hits);
		dl.getEntries();
		
		//static call
		DL.getEntries(hits);
		
		//passed var call
		dlImpl.getEntries(hits);
	}
	

	public List<Object> getEntries(Hits hits) {
		//field call
		return dlUtil.getEntries(hits);
	}
	
	private DLUtil dlUtil;

}
