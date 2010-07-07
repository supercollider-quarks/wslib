// wslib 2010

XFadeDelay {
	
	// dual crossfading delay, based on DelayN
	// equal power crossfade to new time when delaytime is changed
	
	*ar { |in = 0, max = 0.2, delay = 0.2, fadeTime = 0.1, trigger, mul = 1, add = 0|
		var dChange, dTime, delayed;
		
		trigger = trigger ?? { Impulse.kr( 1/fadeTime ) }; // auto trigger if no external trigger
		delay = Latch.kr( delay, trigger );
		
		dChange = Trig.kr( Slope.kr( delay ).abs, fadeTime ); // only change at actual change
		dChange = ToggleFF.kr( dChange );
		
		dTime = Latch.kr( delay, [ 1-dChange, dChange ] );
		
		delayed = dTime.collect({ |dTime| DelayN.ar( in, max, dTime ); });
		
		^XFade2.ar( delayed[0], delayed[1], 
			Delay1.kr( Slew.kr( dChange, 1/fadeTime, 1/fadeTime ) ).linlin(0,1,-1,1) )
				.madd( mul, add );
	}
}