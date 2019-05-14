package blade.migrate.liferay70;
public class DDMStructureLocalServiceUtilTest {
	public static void main(String[] args) 
	{
		try
		{
			DDMStructureLocalServiceUtil.updateXSDFieldMetadata(structureId,fieldName,metadataEntryName,metadataEntryValue,serviceContext);
		}
		catch (DuplicateUserScreenNameException e)
		{
		}
}