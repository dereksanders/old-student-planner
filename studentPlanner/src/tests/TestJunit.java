package tests;

import static org.junit.Assert.*;

import org.junit.*;

import core.Planner;
import utility.IOManager;

public class TestJunit {

	private static String testDir = Planner.saveDir + "//test";

	@BeforeClass
	public static void init() {

		Planner.main(null);
	}

	@Test
	public void sample() {

		/* Create test directory */

		assertTrue(true);

		/* Remove test directory */
	}

	@Test
	public void addSampleTerm() {

		IOManager.createDirectory(testDir);
		Planner.saveDir = testDir;

	}

	@Test
	public void addSampleCourse() {

	}

	@AfterClass
	public static void cleanup() {

		IOManager.removeDirectory(testDir);
	}

}
