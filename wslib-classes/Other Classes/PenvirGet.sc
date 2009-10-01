// wslib 2009
// getenv for patterns

/*
Pbind( \freq, \freq.p(440) ).play; // same as: Pbind( \freq, PenvirGet(\freq, 440) ).play;

~freq = 550; // change freq
~freq = { 330 rrand: 550 }; 

~freq = Pseq( [330,550,440, Pwhite( 440, 660, 1 )], inf ); // calls .asStream internally
~freq.postln; // becomes a Routine

*/

PenvirGet : Pattern {

	var <>key;
	var <>default;
	var <>environment;
		
	*new { arg key, default = 0, environment;	
		^super.newCopyArgs(key, default, environment ?? { currentEnvironment })
	}
	storeArgs { ^[key, default] }
	
	embedInStream { arg inval; 
		loop { 
			environment[ key ] = environment[ key ].asStream;
			inval = yield( (environment.at( key ) ? default).value ) 
			};
		}
	}
	
+ Symbol {
	// shortcut for PenvirGet
	p { |default = 0, env| ^PenvirGet( this, default, env ) }
	
	}