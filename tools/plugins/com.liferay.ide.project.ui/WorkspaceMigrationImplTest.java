package com.liferay.ide.project.ui.migration;

import com.liferay.blade.migration.api.Problem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorkspaceMigrationImplTest {

	private static final String TMPDIR = System.getProperty("java.io.tmpdir");

	String _expectedXml =

"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
"<WorkspaceMigration>\n"+
"<Problem endOffset=\"20\" file=\"" + TMPDIR + "\" lineNumber=\"999\" number=\"0\" startOffset=\"10\" summary=\"foobar\" ticket=\"LPS-123\" title=\"foo\" type=\"test\" url=\"bar\"/>\n"+
"<Problem endOffset=\"202\" file=\"" + TMPDIR + "\" lineNumber=\"9992\" number=\"0\" startOffset=\"102\" summary=\"foobar2\" ticket=\"LPS-1232\" title=\"foo2\" type=\"test2\" url=\"bar2\"/>\n"+
"</WorkspaceMigration>";

	List<Problem> _sampleProblems = new ArrayList<>();

	public WorkspaceMigrationImplTest() {
		_sampleProblems.add(new Problem("foo", "bar", "foobar", "test", "LPS-123", new File(TMPDIR), 999, 10, 20, false));
		_sampleProblems.add(new Problem("foo2", "bar2", "foobar2", "test2", "LPS-1232", new File(TMPDIR), 9992, 102, 202, false));
	}

	@Before
	public void deleteExistingDataFile() throws Exception {
		IO.delete(IO.getFile("generated/test/migration.xml"));
	}

	@Test
	public void mementoWrite() throws Exception {
		WorkspaceMigrationImpl impl = new WorkspaceMigrationImpl();
		impl._migrationFile = IO.getFile("generated/test/migration.xml");

		impl.problemsFound(_sampleProblems);

		assertTrue(impl._migrationFile.exists());

		assertEquals(_expectedXml, new String(IO.read(impl._migrationFile)));
	}

	@Test
	public void mementoRead() throws Exception {
		try {
			mementoWrite();
		}
		catch (Throwable e) {
			//ignore
		}

		WorkspaceMigrationImpl impl = new WorkspaceMigrationImpl();
		impl._migrationFile = IO.getFile("generated/test/migration.xml");

		List<Problem> storedProblems = impl.getStoredProblems(false);
		assertTrue( storedProblems != null && storedProblems.size() == 2);

		assertEquals("foo", storedProblems.get(0).title);
		assertEquals("foo2", storedProblems.get(1).title);
	}


}
