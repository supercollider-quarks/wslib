+ Integer {
	asStringWithFrac { |fracSize = 3|
		^(this.asString ++ ".".extend( fracSize+1, $0 ));
		}
	}
	
+ Float {
	asStringWithFrac { |fracSize = 3|
		^(this.floor.asString ++ "." ++ 
			(this.frac * (10**fracSize) ).round(1).asInt.asStringToBase(10,fracSize));
		}
	}
	
+ ArrayedCollection {
	preExtend { |size, item| ^this.reverse.extend( size, item ).reverse }
	}