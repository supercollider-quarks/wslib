TestStringExtVariousAsNote : UnitTest {
	setUp {
	}

	tearDown {
	}

	test_asNote {
		[
			["B1",  47],
			["C2",  48],
			["C#2", 49],
			["B2",  59],
			["C3",  60],
			["C#3", 61],
			["B3",  71],
		].do { |pair|
			var in = pair[0], expected = pair[1];
			var got = in.asNote;
			this.assertEquals(got.class, Note);
			this.assertEquals(got.midi, expected);
		};
	}
}

// To test, use one of the following approaches:
//
//   UnitTest.gui;
//   UnitTest.runTest("TestExtVariousAsNote:test_asNote");
