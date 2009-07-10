AxialGradient {
	var <>startPoint, <>endPoint, <>color0, <>color1;
	
	*new { arg startPoint, endPoint, color0, color1;
		^super.newCopyArgs( startPoint, endPoint, color0, color1 );
		}
	
	fill { ^Pen.fillAxialGradient( startPoint, endPoint, color0, color1 ); }
	
	at { |index| // 0-1
		^blend(color0, color1, index);
		}
		
	atPoint { |point|
		
		// TODO ..
		
		}
	}
	
RadialGradient {
	var <>innerCircleCenter, <>outerCircleCenter, <>startRadius, <>endRadius, <>color0, <>color1;

	*new { arg innerCircleCenter, outerCircleCenter, startRadius, endRadius, color0, color1;
		^super.newCopyArgs( innerCircleCenter, outerCircleCenter, startRadius, endRadius, 
				color0, color1 );
		}
	
	fill { ^Pen.fillRadialGradient(
		innerCircleCenter, outerCircleCenter, startRadius, endRadius, color0, color1 ); }
	
	at { |index| // 0-1
		^blend(color0, color1, index);
		}
		
	atPoint { |point|
		
		// TODO ..
		
		}
	}