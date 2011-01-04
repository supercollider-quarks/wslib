BHiCut : BEQSuite {
	
	classvar <allRQs;
	
	*initClass { 
		
		/*
		allRQs = [ 	// Tietze/Schenk, 1999 issue page 856, pdf page #882.
			[],
			[ 2.sqrt ],					 // 2nd order - 12dB per octave
			[1.8478, 0.7654 ],						// 4 - 24dB
			[1.9319, 1.4142, 0.5176 ],				// 6 - 36dB
			[1.9616, 1.6629, 1.1111, 0.3902 ],   		// 8 - 48dB
			[1.9754, 1.7820, 1.4142, 0.9080, 0.3129 ]	// 10 - 60dB
		];
		*/
		
		allRQs = [ 	// Tietze/Schenk, 1999 issue page 856, pdf page #882.
			[],
			[ 2 ].sqrt,					 		// 2nd order - 12dB per octave
			[ 2 + 2.sqrt, 2 - 2.sqrt ].sqrt,	 		// 4 - 24dB
			[ 2 + 3.sqrt, 2, 2 - 3.sqrt ].sqrt,		// 6 - 36dB
			[ 2 + (2 + 2.sqrt).sqrt, 
			  2 + (2 - 2.sqrt).sqrt, 
			  2 - (2 - 2.sqrt).sqrt, 
			  2 - (2 + 2.sqrt).sqrt ].sqrt,           // 8 - 48dB
			[1.9754, 1.7820, 2.sqrt, 0.9080, 0.3129 ]	// 10 - 60dB
		];
		
		
	}
	
	*filterClass { ^BLowPass }
	
	*new1 { |rate = 'audio', in, freq, order=2| 
		var rqs, selector; 
		rqs = allRQs.clipAt(order); 
		selector = this.methodSelectorForRate( rate );
		rqs.do {Ê|rq|Êin = this.filterClass.perform( selector, in, freq, rq ) };
		^in;
	}

	*ar { |in, freq, order=2|Ê
		^this.new1( 'audio', in, freq, order );
	}
	
	*kr { |in, freq, order=2|Ê
		^this.new1( 'control', in, freq, order );
	}

}

BLowCut : BHiCut {
	*filterClass { ^BHiPass }
}

LRLowCut : BLowCut { // dual cascaded butterworth - use for crossovers
	
	*new1 { |rate = 'audio', in, freq, order=2| 
		var rqs, selector; 
		rqs = allRQs.clipAt(order); 
		selector = this.methodSelectorForRate( rate );
		2.do { rqs.do {Ê|rq|Êin = this.filterClass.perform( selector, in, freq, rq ) }; };
		^in;
	}
	
}

LRHiCut : LRLowCut { 
	*filterClass { ^BHiPass }
}