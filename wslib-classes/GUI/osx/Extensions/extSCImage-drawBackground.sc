

// backgroundImage_

+ SCImage {

	drawBackground { |rect, tileMode=1, alpha=1.0, fromRect, operation = 'sourceOver'|
		var toRect;
		rect = rect ?? { Rect(0,0,400,400) };
		switch ( tileMode,
			1, { this.drawAtPoint( rect.leftTop, fromRect, operation, alpha ); },
			2, { this.tileInRect( Rect( rect.left, rect.top, rect.width, 
					(fromRect ? this.bounds).height ), fromRect, operation, alpha ) },
			3, { this.drawAtPoint( rect.rightTop - (((fromRect ? this.bounds).width)@0),
					fromRect, operation, alpha ); },
			4, { this.tileInRect( Rect( rect.left, rect.top, (fromRect ? this.bounds).width,
					rect.height), fromRect, operation, alpha );  },
			5, { this.tileInRect( rect, fromRect, operation, alpha ); },
			6, { fromRect = fromRect ? this.bounds;
				this.tileInRect( Rect( rect.right - fromRect.width, rect.top, fromRect.width,
					rect.height), fromRect, operation, alpha );  },
			7, { this.drawAtPoint( rect.left@(rect.bottom - (fromRect ? this.bounds).height), 
					fromRect, operation, alpha ); },
			8, { fromRect = fromRect ? this.bounds;
				 this.tileInRect( Rect( rect.left, rect.bottom - fromRect.height, rect.width, 
					fromRect.height ), fromRect, operation, alpha ); },
			9, { this.drawAtPoint( rect.rightBottom - (fromRect ? this.bounds).extent, 
					fromRect, operation, alpha ); },
			10, { this.drawInRect( rect, fromRect, operation, alpha ); },
			
			11, {	
				fromRect = fromRect ?? { this.bounds };
				
				if( (fromRect.width/fromRect.height) >= (rect.width/rect.height) )
						{ toRect = Rect(0,0, rect.width, fromRect.height * (rect.width/fromRect.width) ); }
						{ toRect = Rect(0,0, fromRect.width * (rect.height/fromRect.height), rect.height); };
					
				 this.drawInRect( toRect.moveTo( *toRect.centerIn( rect ).asArray ), fromRect, operation, alpha ); 
				},
			
			12, { this.drawAtPoint( ((fromRect ? this.bounds).centerIn( rect ).x)@rect.top, 
					fromRect, operation, alpha); },
			13, { this.drawAtPoint( ((fromRect ? this.bounds).centerIn( rect ).x)@
						(rect.bottom - (fromRect ? this.bounds).height), 
					fromRect, operation, alpha); },
			14, { this.drawAtPoint( rect.left@((fromRect ? this.bounds).centerIn( rect ).y), 
					fromRect, operation, alpha); },
			15, { this.drawAtPoint( (rect.right - (fromRect ? this.bounds).width)@
						((fromRect ? this.bounds).centerIn( rect ).y), 
					fromRect, operation, alpha); },
			16, { this.drawAtPoint( (fromRect ? this.bounds).centerIn( rect ), 
					fromRect, operation, alpha); }
		);
		}
	
	fill { |rect, tileMode=1, alpha=1.0, fromRect, operation = 'sourceOver'|
		^Pen.use{
			Pen.clip;
			this.drawBackground( rect, tileMode, alpha, fromRect, operation );
			}; 
		}
		
	}

/*
	1 - fixed to left, fixed to top
	2 - horizontally tile, fixed to top
	3 - fixed to right, fixed to top
	4 - fixed to left, vertically tile
	5 - horizontally tile, vertically tile
	6 - fixed to right, vertically tile
	7 - fixed to left, fixed to bottom
	8 - horizontally tile, fixed to bottom
	9 - fixed to right, fixed to bottom
	10 - fit
	11 - center, center (scale)
	12 - center , fixed to top
	13 - center , fixed to bottom
	14 - fixed to left, center
	15 - fixed to right, center
	16 - center, center (no scale)
*/