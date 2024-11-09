TestSimpleMIDIFile : UnitTest {
	*getFixturesPath {
		^(
			this.class.filenameSymbol.asString.dirname
			+/+ "test_fixtures"
		);
	}

  test_generatePatternSeqs_withVelocity {
    var m, pat;
		m = SimpleMIDIFile.new(
			this.class.getFixturesPath() +/+ "two-notes-w-velocity.mid"
		);

    m.read();

		pat = m.generatePatternSeqs(true)[0];

    this.assertEquals(pat, [
      [60, 1.0, 75/127.0],
      [\rest, 1.0, 0.0],
      [60, 1.0, 100/127.0]
    ]);
		
  }

	test_generatePatternSeqs_noPaddingBeginningStart {
    var m, pat;
		m = SimpleMIDIFile.new(
			this.class.getFixturesPath() +/+ "two-notes-beginning-start.mid"
		);

    m.read();

		pat = m.generatePatternSeqs()[0];

    this.assertEquals(pat, [
      [60, 1.0],
      [\rest, 1.0],
      [60, 1.0]
    ]);
		
	}

  test_generatePatternSeqs_noPaddingOffsetStart {
    var m, pat;
    m = SimpleMIDIFile.new(
			this.class.getFixturesPath() +/+ "two-notes-offset-start.mid"
		);

    m.read();

		pat = m.generatePatternSeqs()[0];

    this.assertEquals(pat, [
      [60, 1.0],
      [\rest, 1.0],
      [60, 1.0]
    ]);
  }
  test_generatePatternSeqs_padStart {
    var m, pat;
    m = SimpleMIDIFile.new(
      this.class.getFixturesPath() +/+ "two-notes-offset-start.mid"
    );

    m.read();

    pat = m.generatePatternSeqs(false, true)[0];
    
    this.assertEquals(pat, [
      [\rest, 1.0],
      [60, 1.0],
      [\rest, 1.0],
      [60, 1.0]
    ]);
  }
  test_generatePatternSeqs_padEnd {
    var m, pat;
    m = SimpleMIDIFile.new(
			this.class.getFixturesPath() +/+ "two-notes-beginning-start.mid"
    );

    m.read();

    pat = m.generatePatternSeqs(false, true, 4.0)[0];
    
    this.assertEquals(pat, [
      [60, 1.0],
      [\rest, 1.0],
      [60, 1.0],
      [\rest, 1.0]
    ]);

  }
}
