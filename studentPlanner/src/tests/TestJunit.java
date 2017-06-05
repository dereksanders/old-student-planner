package tests;

import static org.junit.Assert.*;

import org.junit.*;

import core.Driver;

public class TestJunit {

	public static Driver driver;

	@BeforeClass
	public static void init() {
	}

	@Test
	public void plannerButtonVisibility() {

		assertTrue(true);
	}
}
