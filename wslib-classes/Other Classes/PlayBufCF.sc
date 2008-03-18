PlayBufCF {
	// dual play buf which crosses from 1 to the other at trigger
		
	*ar { arg numChannels, bufnum=0, rate=1.0, trigger=1.0, startPos=0.0, loop = 0.0, lag = 0.1;
		var ffp, slew;
		case { trigger.rate == \audio }
			{ ffp = ToggleFF.ar( trigger ).linlin(0,1,-1,1);
			 slew = Slew.ar( ffp + 1, 2/lag, 2/lag ) - 1;  }
			{ trigger.rate == \control }
			{ ffp = ToggleFF.kr( trigger ).linlin(0,1,-1,1);
			slew = Slew.kr( ffp + 1, 2/lag, 2/lag ) - 1;  }
			{ true }
			{ ffp = 1; slew = 1};
		rate = rate.asCollection;
		
		^XFade2.ar( 
			PlayBuf.ar( numChannels, bufnum, rate.wrapAt(1), ffp * -1, startPos, loop ),
			PlayBuf.ar( numChannels, bufnum, rate.wrapAt(0), ffp, startPos, loop ),
			slew );
		}
	}
	
PlayBufAlt {
	// switches from forward to backward and vv playback when triggered
	// includes crossfade to prevent clicks
	*ar { arg numChannels, bufnum=0, rate=1.0, trigger=1.0, startPos=0.0, loop = 1.0, lag = 0.05;
		if( trigger.size < 2 )
			{ ^this.ar1( numChannels, bufnum, rate, trigger, startPos, loop, lag ); }
			{ ^trigger.collect({ |item,i|
				this.ar1( 
						numChannels.asCollection.wrapAt(i), 
						bufnum.asCollection.wrapAt(i), 
						rate.asCollection.wrapAt(i), 
						item, 
						startPos.asCollection.wrapAt(i), 
						loop.asCollection.wrapAt(i), 
						lag.asCollection.wrapAt(i) );
				})
			}
		}
	
	/*
	*ar1 { arg numChannels, bufnum=0, rate=1.0, trigger=1.0, startPos=0.0, loop = 1.0, lag = 0.05;
		var ffp, x;
		ffp = ToggleFF.ar( trigger ).linlin(0,1,-1,1);
		x = Phasor.ar(0, rate * ffp, 0, BufFrames.kr(bufnum));
		ffp = ffp * [1,-1];
		^[ 
		PlayBuf.ar( numChannels, bufnum, rate * 1, ffp[0], startPos + Latch.ar( x, ffp[0] ), loop ) 
			* Lag.ar( ffp[0].max(0), lag ),
		PlayBuf.ar( numChannels, bufnum, rate * -1, ffp[1], startPos + Latch.ar( x, ffp[1] ), loop ) 
			* Lag.ar( ffp[1].max(0), lag  )
		].sum;
			
		}
	*/
	
	
	*ar1 { arg numChannels, bufnum=0, rate=1.0, trigger=1.0, startPos=0.0, loop = 1.0, lag = 0.05;
		var ffp, x;
		ffp = ToggleFF.ar( trigger ).linlin(0,1,-1,1);
		x = Phasor.ar(0, rate * ffp, 0, BufFrames.kr(bufnum));
		
		^XFade2.ar( 
			PlayBuf.ar( numChannels, bufnum, rate * -1, ffp * -1, 
				startPos + Latch.ar( x, ffp * -1 ), loop ),
			PlayBuf.ar( numChannels, bufnum, rate * 1, ffp, startPos + Latch.ar( x, ffp ), loop ),
			Slew.ar( ffp + 1, 2/lag, 2/lag ) - 1; );
		}
	
	
	}