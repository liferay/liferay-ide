import com.liferay.portal.kernel.security.exportimport.UserImporter;
import com.liferay.portal.kernel.security.exportimport.UserExporter;
import com.liferay.portal.kernel.security.exportimport.UserOperation;
import com.liferay.portal.kernel.security.exportimport.UserImporterUtil;
import com.liferay.portal.kernel.security.exportimport.UserExporterUtil;

import com.liferay.dynamic.data.mapping.util.DDMXMLUtil;

public class UserImporterExporterTestFile implements UserImporter {

	public static void main(String[] args) {
		UserExporterUtil.foo("11");
		DDMXMLUtil.formatXML(new Document());

		FlagsEntryService flagsEntryService = new FlagsEntryServiceImpl();
		flagsEntryService.addEntry("foo");
	}

}
