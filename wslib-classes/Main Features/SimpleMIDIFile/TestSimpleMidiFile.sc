TestSimpleMIDIFile : UnitTest {
	*getFixturesPath {
		^(
			this.class.filenameSymbol.asString.dirname
			+/+ "test_fixtures"
		);
	}
	test_generatePatternSeqs_noPadding {
		var m = SimpleMIDIFile.new(
			this.class.getFixturesPath() +/+ "two-notes-beginning-start.mid"
		);

		var pat = m.generatePatternSeqs();

		pat.postln();
	}
	test_generatePatternSeqs_padStart {
		var m = SimpleMIDIFile.new();

		this.class.getFixturesPath().postln();
	}
}
