XYView : SCViewHolder {

	var <x = 0, <y = 0;
	var <>foreground, <>background;
	var <>returnAfterMouseUp = true;
	var mouseDownPoint, mouseDownValue;
	var <radius;
	var <>stepSize = 1;
	
	var <>mouseDownAction, <>mouseUpAction, <>mouseMoveAction;
	
	var <>action;
	
	*new { arg parent, bounds;
		^super.new.prInit( parent, bounds );
	}
	
	properties { ^[] }
	
	prInit { arg parent, bounds;
		bounds = bounds.asRect;
		foreground = Color.grey( 0.2 );
		background = Color.white.alpha_(0.5);
		radius = bounds.width.min(bounds.height) / 2;
		this.view = GUI.userView.new( parent, bounds )
			.canFocus_( true )
			//.relativeOrigin_( true )
			.drawFunc_({ arg ... args; this.prDraw( *args )})
			.mouseDownAction_({ arg ... args; 
				this.prMouseDown( *args );
				mouseDownAction.value( *args );
				action.value( this, x, y );
				})
			.mouseMoveAction_({ arg ... args; 
				this.prMouseMove( *args );
				mouseMoveAction.value( *args );
				action.value( this, x, y );
				})
			.mouseUpAction_({ arg ... args; 
				this.prMouseUp( *args );
				mouseUpAction.value( *args );
				});
	}
	
	value { ^(x@y) }
	
	x_ {  arg xx;
		x = xx;
		view.refresh;
		}
		
	y_ {  arg yy;
		y = yy;
		view.refresh;
		}

	radius_ { arg newRadius;
		radius = newRadius;
		view.refresh;
		}
	
	value_ { arg val;
		val = val.asPoint;
		x = val.x;
		y = val.y;
		view.refresh;
		}
	
	valueAction_ { arg val;
		this.value_( val );
		this.doAction;
	}
	
	doAction {
		view.action.value( this );
	}
	
	prDraw { |vw| 
		var relBounds = vw.drawBounds;
		var inset = 2;
		var arrowRadius = (relBounds.width.min(relBounds.height) / 2) - inset;
		
		GUI.pen.color = background;
		Pen.roundedRect( relBounds, radius).fill; 
		
		GUI.pen.use({
			GUI.pen.translate( *relBounds.center.asArray );
			
			GUI.pen.color = foreground;
			
			4.do({ |i|
				if( switch( i,
					0, { x > 0  },
					1, { y > 0  },
					2, { x < 0  },
					3, { y < 0  }), 
					{ GUI.pen.width = 2.5 },
					{ GUI.pen.width = 1 });

				Pen.arrow( 0@0, Polar( arrowRadius, (i / 2) * pi ).asPoint, 3 );
				Pen.stroke;
				});
			
		 });
	}
	
	prMouseDown { arg view, x, y, modifiers, buttonNumber, clickCount;
		if( clickCount > 1 ) { this.value = 0@0 }; // reset
		mouseDownPoint = (x@y);
		mouseDownValue = this.value;
	}

	
	prMouseMove { arg view, x, y, modifiers, buttonNumber, clickCount;
		this.value_( mouseDownValue + 
			(((x@y) - mouseDownPoint) * stepSize) );
	}
	
	prMouseUp { arg view, x, y, modifiers, buttonNumber, clickCount;
		if( returnAfterMouseUp ) { this.value_( mouseDownValue ); };
	}
}