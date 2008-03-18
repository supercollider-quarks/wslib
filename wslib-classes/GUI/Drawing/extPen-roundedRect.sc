// wslib 2006

// 1st step to SwingOsc compat :: 03/2008
// 	- no rounded rects yet: all 

+ GUIPen {

	*roundedRect { |rect, radius| // radius can be array for 4 corners
	
		radius = radius ?? {  rect.width.min( rect.height ) / 2; };
			
		if( radius != 0 )
			{	
				radius = radius.asCollection.collect({ |item| 
					item ?? {  rect.width.min( rect.height ) / 2; }; });
					
				case { GUI.scheme.id === \swing }
					{ ^GUI.pen.addRect( rect ); } 
						// rounded rects : TODO ( incompatible JPen.addArc -> fill !! )
					{ true }
					{
					GUI.pen.moveTo( rect.leftTop + ((radius@@0)@0) );
					GUI.pen.addArc( rect.rightTop - ((radius@@1)@((radius@@1).neg)), 
						(radius@@1), -0.5pi, 0.5pi );
					GUI.pen.addArc( rect.rightBottom - ((radius@@2)@(radius@@2)), 
						(radius@@2), 0, 0.5pi );
					GUI.pen.addArc( rect.leftBottom + ((radius@@3)@((radius@@3).neg)), 
						(radius@@3), 0.5pi, 0.5pi );
					^GUI.pen.addArc( rect.leftTop + ((radius@@0)@(radius@@0)), 
						(radius@@0), pi, 0.5pi );
					};
			}
			{	^GUI.pen.addRect( rect ); }
					
		}
		
	*extrudedRect {	 |rect, radius, width = 2, angle, inverse = false, colors|
	
		var centers;
		
		angle = angle ? 0.17pi;	
				
		if(  GUI.scheme.id === \swing )
			{ radius = 0; }  // no rounded rects in SwingOSC yet (TODO)
			{ radius = radius ? (rect.width.min(rect.height) * 0.5); };
		
		radius = radius.asCollection;
		angle = angle.asCollection;
		
		colors = colors ? [ Color.white.alpha_(0.5), Color.black.alpha_(0.5) ];
		
		centers = [
			 (radius@@0)@(radius@@0),
			 ((radius@@1).neg)@(radius@@1),
			 ((radius@@2).neg)@((radius@@2).neg),
			 (radius@@3)@((radius@@3).neg)
			 ];
			
		centers = centers + [ rect.leftTop, rect.rightTop, rect.rightBottom, rect.leftBottom ];		
		// light side
		 if( inverse ) { GUI.pen.color = colors[1]; } { GUI.pen.color = colors[0]; };
		
		 if( radius@@3 != 0 )
		 	{ GUI.pen.moveTo( centers[3] + Polar( (radius@@3) - width,  pi - (angle@@1) ).asPoint );
		 	  GUI.pen.addArc( centers[3], radius@@3, pi - (angle@@1), angle@@1 ); }
		 	{ GUI.pen.moveTo( centers[3] + ((width)@(width.neg)));
		 	  GUI.pen.lineTo( rect.leftBottom ); };
		 
		 if( radius@@0 != 0 )
		 	{ GUI.pen.addArc( centers[0], radius@@0, pi, 0.5pi ); }
		 	{ GUI.pen.lineTo( centers[0] ) }; 
		 
		 if( radius@@1 != 0 )
		 	{	GUI.pen.addArc( centers[1], radius@@1, 1.5pi,  0.5pi-(angle@@0) ); 
		 		GUI.pen.addArc( centers[1], (radius@@1) - width, (angle@@0).neg, 
		 			(0.5pi-(angle@@0)).neg ); }
		 	{   GUI.pen.lineTo( centers[1] ); 
		 		GUI.pen.lineTo( centers[1] + ((width.neg)@(width)) ); };
		 
		 if( radius@@0 != 0 )
		 	{ GUI.pen.addArc( centers[0], (radius@@0) - width, 1.5pi, -0.5pi ); }
		 	{ GUI.pen.lineTo( centers[0] + ((width)@(width)) ) };
		 	
		 if( radius@@3 != 0 )
		 	{ GUI.pen.addArc( centers[3], (radius@@3) - width, pi, (angle@@1).neg ); }
		 	{ GUI.pen.lineTo( centers[3] + ((width)@(width.neg)) ) };
		 
		 GUI.pen.fill; 
		 
		// dark side
		 if( inverse )  { GUI.pen.color = colors[0]; } { GUI.pen.color = colors[1]; };
		
		 if( radius@@1 != 0 )
		 	{ GUI.pen.moveTo( centers[1] + Polar( (radius@@1) - width, (angle@@0).neg ).asPoint );
		 	  GUI.pen.addArc( centers[1], radius@@1, (angle@@0).neg, (angle@@0) ); }
		 	{ GUI.pen.moveTo( centers[1] + ((width)@(width.neg)) ); GUI.pen.lineTo( centers[1] )  };
		 
		 if( radius@@2 != 0 )
		 	{ GUI.pen.addArc( centers[2], radius@@2, 0, 0.5pi ); }
		 	{ GUI.pen.lineTo( centers[2] ); };
		 
		 if( radius@@3 != 0 )
			{ GUI.pen.addArc( centers[3], radius@@3, 0.5pi,  0.5pi-(angle@@1) ); 
		  	  GUI.pen.addArc( centers[3], (radius@@3) - width, pi - (angle@@1), 
		  	  	(0.5pi-(angle@@1)).neg );  }
		  	{ GUI.pen.lineTo( rect.leftBottom );
		  	  GUI.pen.lineTo( centers[3] + ((width)@(width.neg))); };
		  	
		 if( radius@@2 != 0 )
		 	{ GUI.pen.addArc( centers[2], (radius@@2) - width, 0.5pi, -0.5pi ); }
		 	{ GUI.pen.lineTo( centers[2] + ((width.neg)@(width.neg)) ) };
		 
		 if( radius@@1 != 0 )
		 	{ GUI.pen.addArc( centers[1], (radius@@1) - width, 0, (angle@@0).neg ); }
		 	{ GUI.pen.lineTo( centers[1] + ((width.neg)@(width)) ) }; 
		 
		 GUI.pen.fill;
		 
		 
	}
		
	*circle { |rect|
		var radius;
		radius = rect.width.min(rect.height) * 0.5;
		GUI.pen.addArc( rect.center, radius, 0, 2pi );
		}
		
	*extrudedCircle { |rect, width = 2, angle, inverse = false, colors|
	
		var center, radius;
		
		angle = angle ? 0.17pi;	
		radius = rect.width.min(rect.height) * 0.5;
		
		colors = colors ? [ Color.white.alpha_(0.5), Color.black.alpha_(0.5) ];
		
		center = rect.center;
			
		// light side
		 if( inverse ) { GUI.pen.color = colors[1] } { GUI.pen.color = colors[0] };
		 
		 GUI.pen.addAnnularWedge( center, radius - width, radius, pi - angle, pi );
		
		 GUI.pen.fill; 
		 
		// dark side
		 if( inverse ) { GUI.pen.color = colors[0] } { GUI.pen.color = colors[1] };
		 
		 GUI.pen.addAnnularWedge( center, radius - width, radius, angle.neg, pi );
		
		 GUI.pen.fill; 
	}

	

	}

+ Pen {

	*roundedRect { |rect, radius| // radius can be array for 4 corners
		GUIPen.roundedRect( rect, radius );					}
		
	*extrudedRect { |rect, radius, width = 2, angle, inverse = false, colors|
		^GUIPen.extrudedRect( rect, radius, width, angle, inverse, colors );
		}
		
	*circle { |rect|
		^GUIPen.circle( rect );
		}
		
	*extrudedCircle { |rect, width = 2, angle, inverse = false, colors|
		^GUIPen.extrudedCircle( rect, width, angle, inverse, colors );
		}

	
}