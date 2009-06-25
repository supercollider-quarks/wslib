+ SCUserView {
	drawBounds { 
		^if ( relativeOrigin ) // thanks JostM !
			{ this.bounds.moveTo(0,0) }
			{ this.absoluteBounds; };
		}
	}