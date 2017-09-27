public class AssetTagPropertiesTestFile {
	public static void main(String[] args){
		//1
		AssetTagPropertyLocalServiceUtil.getTagProperties();
		//2
		AssetTagPropertyServiceUtil.getService();
		String str = null;
		String[] strs = null;
		ServiceContext sc = null;
		//3
		AssetTagLocalServiceUtil.addTag(1L , str , strs , sc );
		long[] longs = {1,1};
		//4
		AssetTagServiceUtil.getTags( longs , str , strs , 1 , 1 );
	}
}
