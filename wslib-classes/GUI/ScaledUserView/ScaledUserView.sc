// wslib 2006
// scaled SCUserView

// 2007: altered for SC3.2 compatibility

ScaledUserView { 

	classvar <>gridColor;
	
	var <view, <>fromBounds;
	var <scaleH = 1, <scaleV = 1, <moveH = 0.5, <moveV = 0.5;
	var <>gridSpacingH = 0, <>gridSpacingV = 0; // no grid when spacing == 0
	var <>gridMode = \blocks;
	var <>mouseDownAction, <>mouseMoveAction, <>mouseUpAction;
	var <>mouseOutOfBoundsAction;
	var <drawFunc, <unscaledDrawFunc, <unclippedUnscaledDrawFunc, <beforeDrawFunc;
	var <>autoRefresh = true;
	var <>autoRefreshMouseActions = true;
	var <clip = true;
	var <>clipScaled = true; // clips only when clip is also true
	var <>clipUnscaled = true; 
	var <background;
	var <>keepRatio = false;  // experimental - handle with care..
	
	// mouseActions arguments:
	//  |this, scaledX, scaledY, modifier, x, y, isInside|
	
	// unscaledDrawFunc:  0@0 == leftTop of view (not window)
	
	*initClass { 
		gridColor = Color.gray.alpha_(0.25); 
		}
	
	*new { |window, bounds, fromBounds|
		bounds = bounds ? Rect(0,0,360, 360);
		fromBounds = fromBounds ? bounds;
		^super.new.fromBounds_( fromBounds ).init( window, bounds);
		}
		
	*window { |name, bounds, fromBounds, viewOffset| 
			// creates a window with sliders for scale/move
		^ScaledUserViewWindow( name, bounds, fromBounds, viewOffset );
		}
	
	init { |window, bounds|
		view = GUI.userView.new( window, bounds );
		view.relativeOrigin_( false );
		view.background = background;
		
		view.mouseDownAction = { |v, x, y, m|
			var scaledX, scaledY, isInside = true;
			x = x - v.bounds.left; y = y - v.bounds.top;
			#scaledX, scaledY = this.convertBwd( x,y );
			mouseDownAction.value( this, scaledX, scaledY, m, x, y, isInside );
			this.refresh( autoRefreshMouseActions );
			};
			
		view.mouseMoveAction = { |v, x, y, m|
			var scaledX, scaledY, isInside;
			isInside = v.bounds.containsPoint( x@y );
			x = x - v.bounds.left; y = y - v.bounds.top;
			#scaledX, scaledY = this.convertBwd( x,y );
			mouseMoveAction.value( this, scaledX, scaledY, m, x, y, isInside );
			if( isInside.not )
				{ mouseOutOfBoundsAction.value( this, scaledX, scaledY, m, x, y ); };
			this.refresh( autoRefreshMouseActions );
			};
		
		view.mouseUpAction = { |v, x, y, m|
			var scaledX, scaledY, isInside;
			isInside = v.bounds.containsPoint( x@y );
			x = x - v.bounds.left; y = y - v.bounds.top;
			#scaledX, scaledY = this.convertBwd( x,y );
			mouseUpAction.value( this, scaledX, scaledY, m, x, y, isInside );
			if( isInside.not )
				{ mouseOutOfBoundsAction.value( this, scaledX, scaledY, m, x, y ); };
			this.refresh( autoRefreshMouseActions );
			};
		
		view.drawFunc = { |v|
			
			var scaledViewBounds;
			var viewRect;
			
			viewRect = this.viewRect;
			
			beforeDrawFunc.value( this );
			
			if( background.class == Color )
				{ Pen.use({ 
					background.set;
					Pen.fillRect( v.bounds );
					}); 
				}; 
			
			Pen.use({
			
				Pen.translate( v.bounds.left, v.bounds.top ); // move to views leftTop corner
				
				Pen.scale( *this.scaleAmt );
				Pen.translate( *this.moveAmt );
				
				// clip:
				if( clip && clipScaled ) { 
					
					// clip doesn't work with negative scaling
					scaledViewBounds =
						Rect.fromPoints( *([[0,0], 
								[v.bounds.width,v.bounds.height]]
							.collect({ |point| this.convertBwd( *point ).asPoint; }) ) );
							
					Pen.moveTo(scaledViewBounds.leftTop);
					Pen.lineTo(scaledViewBounds.rightTop);
					Pen.lineTo(scaledViewBounds.rightBottom);
					Pen.lineTo(scaledViewBounds.leftBottom);
					Pen.lineTo(scaledViewBounds.leftTop);
					Pen.clip;
					
					};
				
				// grid:
				
				if( (gridSpacingV != 0) && // kill grid if spacing < 2px
					{ (viewRect.height / v.bounds.height) < ( gridSpacingV / 2 ) } )
				{	if( gridMode.asCollection.wrapAt( 0 ) === 'blocks' )
						{ 	gridColor.set;
							Pen.width = gridSpacingV;
							((0, (gridSpacingV * 2) .. fromBounds.height + gridSpacingV) 
									+ (gridSpacingV / 2))
								.abs
								.do({ |item| Pen.line( 0@item, (fromBounds.width)@item ); });
						} {  Color.black.blend( gridColor, 0.5 ).set;
							Pen.width = (fromBounds.width / v.bounds.width).abs / scaleV; 
							
							(0, gridSpacingV .. (fromBounds.height + gridSpacingV))
								.abs
								.do({ |item| Pen.line( 0@item, (fromBounds.width)@item ); });
						 };
						
					Pen.stroke;
				};
				
				
				if( ( gridSpacingH != 0 ) &&
					 { (viewRect.width / v.bounds.width) < (gridSpacingH / 2 ) } )
				{	if( gridMode.asCollection.wrapAt( 1 ) === 'blocks' )
						{	gridColor.set;
							Pen.width = gridSpacingH;
							((0, (gridSpacingH * 2) .. fromBounds.width + gridSpacingH) 
									+ (gridSpacingH / 2))
								.abs
								.do({ |item| Pen.line( item@0, item@(fromBounds.height) ); });
						} {  Color.black.blend( gridColor, 0.5 ).set;
							Pen.width = (fromBounds.width / v.bounds.width).abs / scaleH; 
							(0, gridSpacingH .. (fromBounds.width + gridSpacingH))
								.abs
								.do({ |item| Pen.line( item@0, item@(fromBounds.height) ); });
						};	
					
					Pen.stroke;
				};
					
				// drawFunc:
				
				// line will be 1px at current view width and scale == [1,1] 
				Pen.width = 
					[ (fromBounds.width / v.bounds.width).abs,
					  (fromBounds.height / v.bounds.height).abs ].mean; 
					 
				Color.black.set;
				
				drawFunc.value( this );
				});
			
			Pen.use({
				
				Pen.translate( v.bounds.left, v.bounds.top ); // move to views leftTop corner
				
				// clip unscaled:
				if( clip ) {
						Pen.moveTo(0@0);
						Pen.lineTo(v.bounds.width@0);
						Pen.lineTo(v.bounds.width@v.bounds.height);
						Pen.lineTo(0@v.bounds.height);
						Pen.lineTo(0@0);
						Pen.clip;
						};
	
				unscaledDrawFunc.value( this );
				});
				
			Pen.use({
				Pen.translate( v.bounds.left, v.bounds.top ); // move to views leftTop corner
				unclippedUnscaledDrawFunc.value( this );
				});
				
			};
		}
		
	refresh { |flag = true| 
		flag = flag ? autoRefresh;
		if( flag == true ) { view.parent.refresh; }; }

	
	scaleH_ { |newScaleH, refreshFlag|
		scaleH = newScaleH ? scaleH; 
		if( keepRatio ) { scaleV = newScaleH ? scaleH; };
		this.refresh( refreshFlag );
		}
		
	scaleV_ { |newScaleV, refreshFlag|
		if( keepRatio.not )
			{ scaleV = newScaleV ? scaleV; this.refresh( refreshFlag ); };
		}
	
	scale { ^[ scaleH, scaleV ] }
	
	scale_ { |newScaleArray, refreshFlag| // can be single value, array or point
		newScaleArray = (newScaleArray ? this.scale).asPoint;
		this.scaleH_( newScaleArray.x, false );
		this.scaleV_( newScaleArray.y, false );
		this.refresh( refreshFlag );
		}
	
	moveH_ { |newMoveH, refreshFlag|
		moveH = newMoveH ? moveH; this.refresh( refreshFlag );
		}
	
	moveV_ { |newMoveV, refreshFlag|
		moveV = newMoveV ? moveV; this.refresh( refreshFlag );
		}
	
	move { ^[ moveH, moveV ] }
	
	move_ { |newMoveArray, refreshFlag|
		newMoveArray = (newMoveArray ? this.move).asPoint;
		moveH = newMoveArray.x;
		moveV = newMoveArray.y;
		this.refresh( refreshFlag );
		}
		
	movePixels { // works - pixel offset from center
		var bnds;
		bnds = this.bounds.extent.asArray.neg;
		^this.move.collect({ |item,i|
			item.linlin( 0.5,1.5,0, bnds[i] * (this.scale[i] - 1), \none);
			});
		}
		
	movePixels_ { |newPixelsArray, limit, refreshFlag| 
		var bnds;
		limit = limit ? true;
		newPixelsArray = (newPixelsArray ? [0,0]).asPoint.asArray;
		bnds = this.bounds.extent.asArray.neg;
		#moveH, moveV = newPixelsArray.asPoint.asArray.collect({ |item,i|
			if( this.scale[i] != 1 ) // no change if scale == 1 (prevent nan error)
				{ item.linlin( 0, bnds[i] * (this.scale[i] - 1), 0.5, 1.5, \none); }
				{ [moveH,moveV][i] };
			});
		if( limit ) { #moveH, moveV = [ moveH, moveV ].clip(0,1) };
		this.refresh( refreshFlag );	
		}
		
	reset { |refreshFlag| scaleH = scaleV = 1; moveH = moveV = 0.5; this.refresh( refreshFlag ); }
		
	// number of grid lines:
	gridLines { ^[ fromBounds.width / gridSpacingH, fromBounds.height / gridSpacingV ]; }
	
	gridLines_ { |newGridLines, refreshFlag|
		newGridLines = (newGridLines ? this.gridLines).asPoint;
		gridSpacingH = fromBounds.width / newGridLines.x;
		gridSpacingV = fromBounds.height / newGridLines.y;
		if( gridSpacingH == inf ) { gridSpacingH = 0 };
		if( gridSpacingV == inf ) { gridSpacingV = 0 };
		this.refresh( refreshFlag );
		}
		
		
	clip_ { |newBool, refreshFlag|
		newBool = newBool ? clip;
		clip = newBool;
		this.refresh( refreshFlag ); }
	
	drawFunc_ { |newDrawFunc, refreshFlag|
		drawFunc = newDrawFunc;  this.refresh( refreshFlag );
		}
		
	unscaledDrawFunc_ { |newDrawFunc, refreshFlag|
		unscaledDrawFunc = newDrawFunc;  this.refresh( refreshFlag );
		}
		
	unclippedUnscaledDrawFunc_ { |newDrawFunc, refreshFlag|
		unclippedUnscaledDrawFunc = newDrawFunc;  this.refresh( refreshFlag );
		}
		
	beforeDrawFunc_ { |newDrawFunc, refreshFlag|
		beforeDrawFunc = newDrawFunc;  this.refresh( refreshFlag );
		}
		
	clearDrawFuncs { |refreshFlag|
		drawFunc = unscaledDrawFunc = unclippedUnscaledDrawFunc = beforeDrawFunc = nil;
		this.refresh( refreshFlag );
		}
	
	background_ { |color, refreshFlag|
		background = color; 
		//view.background = background;
		this.refresh( refreshFlag ); }
		
	keyDownAction { ^view.keyDownAction }
	keyDownAction_ { |newAction| view.keyDownAction_( newAction ); }
		
	bounds { ^view.bounds }
	
	bounds_ { |newBounds|
		newBounds = (newBounds ? view.bounds).asRect;
		view.bounds = newBounds;
		}
		
	viewRect { |inset = 0| 
		// the currently viewed part of fromBounds
		var points;
		points = [ inset@inset,  this.bounds.extent - (inset@inset) ];
		points = points.collect({ |point| this.convertBwd( point.x, point.y ).asPoint; });
		^Rect( points[0].x, points[0].y, points[1].x - points[0].x, points[1].y - points[0].y );
		}
		
	translateScale { |item|  
	 	// requires extRect-transformations.sc from wslib
	 	// item should be Points, Rects or array containing Points and Rects
	 	if( item.class == Meta_Pen )
	 		{ ^Pen.scale( *this.scaleAmt ).translate( *this.moveAmt );  }
	 		{
	 		if( item.isArray )
	 			{ ^item.collect({ |subItem| 
	 				subItem.translateScale( this.moveAmt, this.scaleAmt ); }); }
	 			{ ^item.translateScale( this.moveAmt, this.scaleAmt ); }
			};
		 }
		  
	// scaling methods
	convertScale { |inRect, outRect, sh = 1, sv = 1|
		if ( keepRatio )
			{	^( (1 / inRect.width.min(  inRect.height ) ) * 
				       outRect.width.min( outRect.height ) * [sh,sv] );  }
			{ 	^[ (1 / inRect.width ) * outRect.width  * sh, 
				   (1 / inRect.height) * outRect.height * sv ]; };
		}
			 
	convertMove { |inRect, mh = 0, mv = 0| 
		if ( keepRatio )
			{  ^( (( [mh.neg, mv.neg] * inRect.width.min( inRect.height )) + 0) 
				* (1 - (1/scaleH)) ); }
			{ ^[
				((mh.neg * inRect.width) + 0) * (1 - (1/scaleH)), 
				((mv.neg * inRect.height) + 0) * (1 - (1/scaleV))
			   ]; 
			 };
		}
		
	
	// these return input values for .translate and .scale
	scaleAmt { ^this.convertScale( fromBounds, view.bounds, scaleH, scaleV ); }
	moveAmt { ^this.convertMove( fromBounds, moveH, moveV ); }
	
	// you can use these within drawFuncs and mouseFuncs to convert x/y values:
	
	convertFwd { |x = 0, y = 0| // move and scale input
		^( [x,y] + this.convertMove( fromBounds, moveH, moveV ) )
			* this.convertScale( fromBounds, view.bounds, scaleH, scaleV )
		}
	
	convertBwd { |x = 0, y = 0| // scale and move input backwards
	 	^( [x,y] / this.convertScale( fromBounds, view.bounds, scaleH, scaleV ) ) 
			- this.convertMove( fromBounds, moveH, moveV );
		}
	
	}