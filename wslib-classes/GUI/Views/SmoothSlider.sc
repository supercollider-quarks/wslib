// wslib 2006
// slider based on blackrain's knob

SmoothSlider : SCUserView {
	var <>color, <value, <>step, hit, <>keystep, <>mode, isCentered = false;
	var <knobSize = 0.25, hitValue = 0;
	var <orientation = \v;
	var <>type = 0; // 0: slider only, 1: green bar next to slider
	var <thumbSize = 0; // compatible with old sliders
	var <focusColor;
	
	*viewClass { ^SCUserView }
	
	init { arg parent, bounds;
		bounds = bounds.asRect;
		if( bounds.width > bounds.height )
			{ orientation = \h };
		super.init(parent, bounds);
		super.focusColor = Color.clear;
		
		this.relativeOrigin_( false );
		
		mode = \jump;  // \jump or \move
		keystep = 0.01;
		step = 0.01;
		value = 0.0;
		color = [Color.gray(0.5, 0.5), Color.blue.alpha_(0.5), Color.black.alpha_(0.3),
			Color.black.alpha_(0.7)];
	}
	
	sliderBounds {
		var realKnobSize, drawBounds;
		//realKnobSize = knobSize * this.bounds.width;
		
		drawBounds = this.bounds;
		if( orientation == \h )
				{  drawBounds = Rect( drawBounds.top, drawBounds.left, 
					drawBounds.height, drawBounds.width ); 
				};
				
		realKnobSize = (knobSize * drawBounds.width)
					.max( thumbSize ).min( drawBounds.height );
		
		case { type == 0 }
			{ ^drawBounds.insetBy( 0, realKnobSize / 2 ) }
			{ type == 1 }
			{ ^drawBounds.insetBy( realKnobSize, realKnobSize / 2 ).moveBy( realKnobSize, 0 ) };
				
		}
		
	focusColor_ { |newColor| focusColor = newColor; this.parent.refresh; }
		
	knobColor { ^color[3] }
	knobColor_ { |newColor| color[3] = newColor; this.refresh; }
	
	background { ^color[0] }
	background_ { |newColor| color[0] = newColor; this.refresh; }
	
	hilightColor { ^color[1] }
	hilightColor_ { |newColor| color[1] = newColor; this.refresh; }
	
	hiliteColor { ^color[1] } // slang but compatible
	hiliteColor_ { |newColor| color[1] = newColor; this.refresh; }
	
	thumbSize_ { |newSize = 0| thumbSize = newSize; this.refresh; }
	
	relThumbSize { if( orientation == \h ) 
		{ ^thumbSize / this.bounds.width  }
		{ ^thumbSize / this.bounds.height };
		}
	
	relThumbSize_ { |newSize = 0| if( orientation == \h ) 
		{ thumbSize = newSize * this.bounds.width }
		{ thumbSize = newSize * this.bounds.height };
		this.refresh;
		}
		
	absKnobSize {  if( orientation == \h ) 
		{ ^knobSize * this.bounds.height  }
		{ ^knobSize * this.bounds.width };
	 }
	
	draw {
		var startAngle, arcAngle, size, widthDiv2, aw;
		var knobPosition, realKnobSize;
		var drawBounds, radius;
		
		GUI.pen.use {
			drawBounds = this.bounds;
			if( orientation == \h )
				{  drawBounds = Rect( drawBounds.top, drawBounds.left, 
					drawBounds.height, drawBounds.width );
				   //GUI.pen.translate( drawBounds.height, 0 );
				    GUI.pen.rotate( 0.5pi,
				   	(this.bounds.left + this.bounds.right) / 2, 
				   	this.bounds.left  + (this.bounds.width / 2)  );
				  
				};
				
				
			size = drawBounds.width;
			widthDiv2 = drawBounds.width * 0.5;
					
			realKnobSize = (knobSize * drawBounds.width)
					.max( thumbSize ).min( drawBounds.height );
			radius = (knobSize * drawBounds.width) / 2;
			knobPosition = drawBounds.top + ( realKnobSize / 2 )
						+ ( (drawBounds.height - realKnobSize) * (1- value).max(0).min(1));
			
			if( this.hasFocus ) // rounded focus rect
				{
				GUI.pen.use({
					GUI.pen.color = focusColor ?? { Color.gray(0.2).alpha_(0.8) };
					GUI.pen.width = 2;
					GUIPen.roundedRect( drawBounds.insetBy(-2,-2), radius + 1 );
					GUI.pen.stroke;
					});
				};
				
			case { type == 0 } // only slider, different color bottom side
				{			
				color[0] !? { color[0].set; // base / background
					GUIPen.roundedRect( Rect( 
						drawBounds.left, 
						drawBounds.top,
						drawBounds.width,
						drawBounds.height ), radius ).fill;
					};
					
				
				color[1] !? { 
					color[1].set; // hilight
					if( isCentered )
					{
					GUIPen.roundedRect( Rect.fromPoints( 
							drawBounds.left@
								((knobPosition - (realKnobSize / 2))
									.min( drawBounds.center.y ) ),
							drawBounds.right@
								((knobPosition + (realKnobSize / 2))
									.max( drawBounds.center.y  ) ))
							
						, radius ).fill;
					}
					{
					GUIPen.roundedRect( Rect.fromPoints( 
							drawBounds.left@(knobPosition - (realKnobSize / 2)),
							drawBounds.right@drawBounds.bottom ), radius ).fill;
					};
					};
		
				color[3] !? {	 
					color[3].set; // knob
					GUI.pen.width = realKnobSize;
					GUIPen.roundedRect( Rect.fromPoints(
						Point( drawBounds.left, 
							( knobPosition - (realKnobSize / 2) ) ),
						Point( drawBounds.right, knobPosition + (realKnobSize / 2) ) ),
						radius )
						.fill; 
					};
				} 
			{ type == 1 } // with line next to slider; like Knob
			{ 			
				color[0].set;
				GUIPen.roundedRect( Rect( 
						drawBounds.left + ( realKnobSize * 2), 
						drawBounds.top,
						drawBounds.width - (realKnobSize * 2),
						drawBounds.height ), realKnobSize / 2 ).fill;
					
				color[2].set; // line
				GUI.pen.width = realKnobSize;
				GUI.pen.moveTo( (drawBounds.left + (realKnobSize/2))@
					(drawBounds.top + ( realKnobSize / 2) ));
					
				GUI.pen.lineTo( (drawBounds.left + (realKnobSize/2))@
					(drawBounds.bottom - ( realKnobSize / 2) ));
				
				GUI.pen.perform(\stroke);
		
				color[1].set; // green line
				GUI.pen.width = realKnobSize * 1.75;
				 GUI.pen.moveTo( (drawBounds.left + ((realKnobSize * 1.75)/2))@
						knobPosition ); 
				if( isCentered )
					{GUI.pen.lineTo( (drawBounds.left + ((realKnobSize * 1.75)/2))
						@(drawBounds.center.y ) ); }
					{GUI.pen.lineTo( (drawBounds.left + ((realKnobSize * 1.75)/2))
						@(drawBounds.bottom - (realKnobSize / 2) ) ); };
							
				GUI.pen.perform(\stroke);
		
				color[3].set; // knob
				GUI.pen.width = realKnobSize;
				
				GUIPen.roundedRect( Rect.fromPoints(
					Point( drawBounds.left + (realKnobSize * 2), 
							knobPosition - (realKnobSize / 2) ),
					Point( drawBounds.right, knobPosition + (realKnobSize / 2) )  ) );
				GUI.pen.fill;
				      
			};
			};
		}

	mouseDown { arg x, y, modifiers, buttonNumber, clickCount;
		 hit = Point(x,y);
			
		hitValue = value;
		this.mouseMove(x, y, modifiers);
	}
	
	mouseMove { arg x, y, modifiers;
		var pt, angle, inc = 0;
		if (modifiers != 1048576, { // we are not dragging out - apple key
			case
				{ mode == \move } {
					if( orientation == \v )
						{ if( thumbSize < this.bounds.height )
							{ value = 
								( hitValue + ( (hit.y - y) / this.sliderBounds.height  ) )
							.clip( 0.0,1.0 ); }; }
						{ if( thumbSize < this.bounds.width )
							{ value = 
								( hitValue + ( (x - hit.x) / this.sliderBounds.height  ) )
							.clip( 0.0,1.0 ); } };
							
					//hit = Point(x,y);
					action.value(this, x, y, modifiers);
					this.refresh;
				}
				{ mode == \jump } {
					if( orientation == \v )
						{ 
						if( thumbSize < this.bounds.height )
							{ value = ( 1 - ((y - (this.bounds.top + (
									( knobSize * this.bounds.width )
									.max( thumbSize.min( this.bounds.height ) ) / 2))) / 
								(this.bounds.height - 
									(knobSize * this.bounds.width )
									.max( thumbSize )  ))
								).clip( 0.0,1.0 );
								};
						 }
						{ if( thumbSize < this.bounds.width )
							{ value = ((x - (this.bounds.left + (
								( knobSize * this.bounds.height )
								.max( thumbSize.min( this.bounds.width ) ) / 2))) / 
							(this.bounds.width - (knobSize * this.bounds.height )
								.max( thumbSize.min( this.bounds.width ) ) ))
								.clip(0.0,1.0); };
						};
							
					//hit = Point(x,y);
					action.value(this, x, y, modifiers);
					this.refresh;
				}
		});
	}

	value_ { arg val;
		value = val.clip(0.0, 1.0);
		this.refresh;
	}

	valueAction_ { arg val;
		value = val.clip(0.0, 1.0);
		action.value(this);
		this.refresh;
	}
	
	safeValue_ {  // prevent crash when window is closed
		 arg val;
		value = val.clip(0.0, 1.0);
		if( parent.notNil && { parent.findWindow.dataptr.notNil } )
			{ this.refresh; }
		}
	

	centered_ { arg bool;
		isCentered = bool;
		this.refresh;
	}
	
	centered {
		^isCentered
	}
	
	orientation_ { |newOrientation| orientation = newOrientation ? orientation; this.refresh; }
	knobSize_ { |newSize| knobSize = newSize ? knobSize; this.refresh; }
	
	increment { ^this.valueAction = (this.value + keystep).min(1) }
	decrement { ^this.valueAction = (this.value - keystep).max(0) }

	keyDown { arg char, modifiers, unicode,keycode;
		// standard keydown
		if (char == $r, { this.valueAction = 1.0.rand; });
		if (char == $n, { this.valueAction = 0.0; });
		if (char == $x, { this.valueAction = 1.0; });
		if (char == $c, { this.valueAction = 0.5; });
		if (char == $], { this.increment; ^this });
		if (char == $[, { this.decrement; ^this });
		if (unicode == 16rF700, { this.increment; ^this });
		if (unicode == 16rF703, { this.increment; ^this });
		if (unicode == 16rF701, { this.decrement; ^this });
		if (unicode == 16rF702, { this.decrement; ^this });
		
	}

	defaultReceiveDrag {
		this.valueAction_(SCView.currentDrag);
	}
	defaultGetDrag { 
		^value
	}
	defaultCanReceiveDrag {
		^currentDrag.isFloat;
	}
}


