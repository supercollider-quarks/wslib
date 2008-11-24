// wslib 2006

// 1st step to SwingOsc compat :: 03/2008
// 	- no rounded rects yet: all 

+ GUIPen {

	*roundedRect { |rect, radius| // radius can be array for 4 corners
		var points, lastPoint;
		
		radius = radius ?? {  rect.width.min( rect.height ) / 2; };
			
		if( radius != 0 )
			{	
				radius = radius.asCollection.collect({ |item| 
					item ?? {  rect.width.min( rect.height ) / 2; }; });
					
				case { GUI.pen.respondsTo( \arcTo ) } // after rev7947 (redfrik added Meta_Pen:arcTo)
					{
					points = [rect.rightTop, rect.rightBottom,rect.leftBottom, rect.leftTop];
					lastPoint = points.last;
					
					GUI.pen.moveTo( points[2] - (0@radius.last) );
					
					points.do({ |point,i|
						GUI.pen.arcTo( lastPoint, point, radius@@i );
						lastPoint = point;
						});
						
					^GUI.pen; // allow follow-up methods
					}
					
					{ GUI.scheme.id === \swing } // swingosc; cannot draw a good rounded rect
					{ ^GUI.pen.addRect( rect ); } 
					
					{ true } // before rev7947 (old)
					{
					GUI.pen.moveTo( rect.leftTop + ((radius@@0)@0) );
					GUI.pen.lineTo( rect.rightTop - ((radius@@1)@0) );
					GUI.pen.addArc( rect.rightTop - ((radius@@1)@((radius@@1).neg)), 
						(radius@@1), 1.5pi, 0.5pi );
					GUI.pen.lineTo( rect.rightBottom - (0@(radius@@2)) );
					GUI.pen.addArc( rect.rightBottom - ((radius@@2)@(radius@@2)), 
						(radius@@2), 0, 0.5pi );
					GUI.pen.lineTo( rect.leftBottom + ((radius@@3)@0) );
					GUI.pen.addArc( rect.leftBottom + ((radius@@3)@((radius@@3).neg)), 
						(radius@@3), 0.5pi, 0.5pi );
					GUI.pen.lineTo( rect.leftTop + (0@(radius@@0)) );
					^GUI.pen.addArc( rect.leftTop + ((radius@@0)@(radius@@0)), 
						(radius@@0), pi, 0.5pi );
					};
			}
			{	^GUI.pen.addRect( rect ); }
					
		}
		
	*extrudedRect {	 |rect, radius, width = 2, angle, inverse = false, colors|
	
		//var centers;
		var points, cornerFunc, sidesFunc;
		
		angle = angle ? 0.17pi;	
		
		/*
		if(  GUI.scheme.id === \swing ) // should be swing compatible now
			{ radius = 0; }  // no rounded rects in SwingOSC yet (TODO)
		*/
	
		radius = radius ?? {  rect.width.min( rect.height ) / 2; };
		radius = radius.asCollection;
		radius = radius.collect({ |item|
			item ?? {  rect.width.min( rect.height ) / 2; }; 
			});
		
		//angle = angle.asCollection; 
		
		colors = colors ? [ Color.white.alpha_(0.5), Color.black.alpha_(0.5) ];
		if( inverse ) { colors = colors.reverse };
		
		cornerFunc = { |points, start = (-1), angle = (0.17pi)| 
			var lastPoint;
			lastPoint =  points.rotate(start.neg).last;
			points.rotate(start.neg)[..2].do({ |point,ii|
				var rd, sta, rectPts, i; // radius, startAngle, rctPoints
				i = ii+start;
				rd = radius@@i;
				sta = i*0.5pi;
				
				case { rd != 0 }
					{ GUI.pen.addAnnularWedge( point + ((rd@rd).rotate(sta)),
					 rd - width,  rd,
					  sta - [ 0.5pi+angle, 0.5pi, 0.5pi+angle][ii], 
					  	[angle, -0.5pi, angle-0.5pi][ii]); };
				
				lastPoint = point;
				});
			};
		
		sidesFunc = { |points, start = 0| 
			var lastPoint;
			lastPoint = points.rotate(start.neg)[3];	
			points.rotate(start.neg)[..1].do({ |point,i|
				var rd, sta, rectPts, prevRdNeg; // radius, startAngle, rctPoints
				i = i + start;
				rd = radius@@i;
				prevRdNeg = (radius@@(i-1)).neg;
				sta = i*0.5pi;
	
				rectPts = [lastPoint, point].stutter(2) +
				 	[ 0@prevRdNeg, width@prevRdNeg.min(width.neg),  
				 	  width@rd.max(width), 0@rd ].collect( _.rotate(sta) );
				
				GUI.pen.moveTo( rectPts.last );
				rectPts.do( GUI.pen.lineTo(_) );
				
				lastPoint = point;
				});
			
			};
			
	points = [  rect.leftTop, rect.rightTop, rect.rightBottom, rect.leftBottom ];
			
	// light side
	
	GUI.pen.fillColor = colors[0];
	cornerFunc.( points, -1, angle );	
	sidesFunc.( points, 0 );		
	GUI.pen.fill;
	
	// dark side
	
	GUI.pen.fillColor = colors[1];
	
	cornerFunc.( points, -3, angle );
	sidesFunc.( points, 2 );
	GUI.pen.fill;


		
		/* // OLD VERSION (not rev7947 compatible)
		centers = [
			 (radius@@0)@(radius@@0),
			 ((radius@@1).neg)@(radius@@1),
			 ((radius@@2).neg)@((radius@@2).neg),
			 (radius@@3)@((radius@@3).neg)
			 ];
			
		centers = centers + [ rect.leftTop, rect.rightTop, rect.rightBottom, rect.leftBottom ];
		
		

		// light side
		/* if( radius@@3 != 0 )
		 	{ GUI.pen.moveTo( centers[3] + Polar( (radius@@3) - width,  pi - (angle@@1) ).asPoint );
		 	  GUI.pen.lineTo( centers[3] + Polar( (radius@@3),  pi - (angle@@1) ).asPoint );
		 	  GUI.pen.addArc( centers[3], radius@@3, pi - (angle@@1), angle@@1 ); }
		 	{ GUI.pen.moveTo( centers[3] + ((width)@(width.neg)));
		 	  GUI.pen.lineTo( rect.leftBottom ); };
		*/		
		// OLD VERSION
		// light side
		
		 if( radius@@3 != 0 )
		 	{ GUI.pen.moveTo( centers[3] + Polar( (radius@@3) - width,  pi - (angle@@1) ).asPoint );
		 	  GUI.pen.lineTo( centers[3] + Polar( (radius@@3),  pi - (angle@@1) ).asPoint );
		 	  GUI.pen.addArc( centers[3], radius@@3, pi - (angle@@1), angle@@1 ); }
		 	{ GUI.pen.moveTo( centers[3] + ((width)@(width.neg)));
		 	  GUI.pen.lineTo( rect.leftBottom ); };
		 
		 if( radius@@0 != 0 )
		 	{ GUI.pen.lineTo( rect.leftTop + (0@(radius@@0)) );
		 	  GUI.pen.addArc( centers[0], radius@@0, pi, 0.5pi ); }
		 	{ GUI.pen.lineTo( centers[0] ) }; 
		 
		 if( radius@@1 != 0 )
		 	{	GUI.pen.lineTo( rect.rightTop - ((radius@@1)@0) );
		 		GUI.pen.addArc( centers[1], radius@@1, 1.5pi,  0.5pi-(angle@@0) ); 
		 		GUI.pen.lineTo( centers[1] + Polar( (radius@@1) - width, (angle@@0).neg ).asPoint );
		 		GUI.pen.addArc( centers[1], (radius@@1) - width, (angle@@0).neg, 
		 			(0.5pi-(angle@@0)).neg ); }
		 	{   GUI.pen.lineTo( centers[1] ); 
		 		GUI.pen.lineTo( centers[1] + ((width.neg)@(width)) ); };
		 
		 if( radius@@0 != 0 )
		 	{ GUI.pen.lineTo( rect.leftTop + ((radius@@0)@(width)) );
		 	  GUI.pen.addArc( centers[0], (radius@@0) - width, 1.5pi, -0.5pi ); }
		 	{ GUI.pen.lineTo( centers[0] + ((width)@(width)) ) };
		 	
		 if( radius@@3 != 0 )
		 	{ GUI.pen.lineTo( rect.leftBottom - ((width.neg)@(radius@@3)) );
		 	  GUI.pen.addArc( centers[3], (radius@@3) - width, pi, (angle@@1).neg ); }
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
		 */
		 
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