+ Pen {
	*transformToRect {|rect, fromRect|
		var scaleAmt;
		rect = rect ?? {Rect(0,0,400,400)};
		fromRect = fromRect ? rect;
		scaleAmt = (rect.extent/fromRect.extent).asArray;
		Pen.translate( *(fromRect.origin.asArray.neg * scaleAmt) + rect.origin.asArray );
		^Pen.scale( *scaleAmt );
		}
	}
	
+ Point {
	transformToRect {|rect, fromRect|
		rect = rect ?? {Rect(0,0,400,400)};
		fromRect = fromRect ? rect;
		^((this - fromRect.origin) * ( rect.extent/fromRect.extent )) + rect.origin;
		}
	}