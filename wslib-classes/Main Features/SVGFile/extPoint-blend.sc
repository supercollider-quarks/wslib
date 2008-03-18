// wslib 2007

+ Point {
	blend { |that, blendfrac = 0.5|
		that = that.asPoint;
		^Point( x.blend( that.x, blendfrac ), y.blend( that.y, blendfrac ) );
		}
	}