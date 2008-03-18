// wslib 2007 
// generate compact code (only for Points so far;  Point(1,2).asShortCS -> "1@2" )

+ Point {
	asShortCS {
		if( (x.size == 0) && { x.rate == \scalar })
			{ ^"%@%".format( x.asShortCS, 
				if( y.sign == -1 ) { "(" ++ y.asShortCS ++ ")" } { y.asShortCS } )
			}
			{ ^this.asCompileString }
		}
	}
	
+ Object { 
	asShortCS { ^this.asCompileString }
	}