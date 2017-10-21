TestNote : UnitTest {
	setUp {
	}

	tearDown {
	}

	test_oct {
		[
			["C3",  3],
			["Cb3", 2],
			["B#4", 5],
		].do { |pair|
			var in = pair[0], expected = pair[1];
			var got = in.asNote;
			this.assertEquals(got.oct, expected);
		};
	}
}

// To test, use one of the following approaches:
//
//   UnitTest.gui;
//   UnitTest.runTestClassForClass(Note);
//   UnitTest.runTest("TestNote:test_oct");
