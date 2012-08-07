// wslib 2012
//
// returns a sorted array of random values
// *new can also be used in language
// trig:	(ar and kr versions) trigger new values
// n:      number of items
// minVal: minimum value
// maxVal: maximum value
// warp:   a distribution warp (as seen in ControlSpec)
// spread: equality of distribution; 
// 		0 = full linear (no random)
//		1 = equal distribution ( all steps < (2*n) )
//		2 = similar to ({0.0 rrand 1.0}!n).sort; total random
//		3 and up = exaggerated spread

SortedRand {
	
	*new { |n = 10, minVal = 0, maxVal = 1, warp = \lin, spread = 1|
		if( minVal.isNumber && { maxVal.isNumber } ) {
			^this.prMapValues( {0.0 rrand: 1.0}!(n+1), minVal, maxVal, warp, spread );
		} {
			^this.prMapValues( Rand(0.dup(n+1),1), minVal, maxVal, warp, spread );
		};
	}
	
	*ir { |n = 10, minVal = 0, maxVal = 1, warp = \lin, spread = 1|
		^this.prMapValues( Rand(0.dup(n+1),1), minVal, maxVal, warp, spread );
	}
	
	*kr { |trig = 0, n = 10, minVal = 0, maxVal = 1, warp = \lin, spread = 1|
		^this.prMapValues( TRand.kr(0.dup(n+1),1, trig), minVal, maxVal, warp, spread );
	}
	
	*ar {  |trig = 0, n = 10, minVal = 0, maxVal = 1, warp = \lin, spread = 1|
		^this.prMapValues( TRand.ar(0.dup(n+1),1, trig), minVal, maxVal, warp, spread );
	}
	
	*prMapValues { |values, minVal = 0, maxVal = 1, warp = \lin, spread = 1|
		var spec;
		spec = [minVal, maxVal, warp].asSpec;
		values = values.pow(spread).integrate;
		values = values / (values.last); // normalize
		values.pop; // remove last item
		^spec.map( values );
	}
	
}