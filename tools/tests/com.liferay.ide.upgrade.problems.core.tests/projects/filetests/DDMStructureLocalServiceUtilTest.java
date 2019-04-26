package blade.migrate.liferay70;

public class DDMStructureLocalServiceUtilTest {

	public static void main(String[] args) {

		DDMStructureLocalServiceUtil.updateStructure(
				_ddmStructure.getStructureId(),
				_ddmStructure.getParentStructureId(), _ddmStructure.getNameMap(),
				_ddmStructure.getDescriptionMap(), ddmForm, ddmFormLayout,
				serviceContext);
	}

}
