// wslib 2010

+ UGen {
	blend { arg that, blendFrac = 0.5;
		// blendFrac should be from zero to one
		^this + (blendFrac * (that - this));
		}
	}