Unwrap {
	
	// pseudo kr-ugen for retreiving wrapped signals
	
	/* example:
	(
	{	var sig, wrapped;
		sig = LFNoise2.kr(10);
		wrapped = Wrap.kr( sig, -0.1, 0.1 );
		[ sig, wrapped, Unwrap.kr( wrapped, -0.1, 0.1 ) ];
	}.plot( 1 );
	)
	*/
	
	*ar { arg in = 0.0, lo = 0.0, hi = 1.0; // 64 samples lag; not reliable for over 300hz changes
		var delayed, nch = 1, sig, buf;
		buf = LocalBuf( 1, nch ).clear;
		delayed = BufRd.ar( 1, buf, DC.ar(0) );
		sig = Wrap.ar( in, delayed + lo, delayed + hi );
		//sig = (((in - lo)+delayed) % (hi-lo)) + (lo - delayed);
		BufWr.kr( sig, buf, DC.ar(0) );
		^sig;
	}
	
		
	*kr { arg in = 0.0, lo = 0.0, hi = 1.0;
		var delayed, nch = 1, sig, buf;
		buf = LocalBuf( 1, nch ).clear;
		delayed = BufRd.kr( 1, buf, 0 );
		sig = Wrap.kr( in, delayed + lo, delayed + hi );
		//sig = (((in - lo)+delayed) % (hi-lo)) + (lo - delayed);
		BufWr.kr( sig, buf, 0 );
		^sig;
	}
	
}

+ UGen {
	
	/*
	{ LFNoise2.kr( 10 ).wrap2(0.1).unwrap2(0.1) }.plot( 1 );
	
	(
	{ var pt;
	  pt = Point( *LFNoise2.kr( 1000.dup ) );
	  [ pt.theta, pt.theta.unwrap2(pi) ] 
	}.plot2(0.1);
	)
	*/
	
	unwrap { |lo = -1, hi = 1|
		^if( rate === 'control')
			{ Unwrap.kr( this, lo, hi ) }
			{ this };
		}
	
	unwrap2 { |aNumber|
		^this.unwrap( aNumber.neg, aNumber );
		}
}