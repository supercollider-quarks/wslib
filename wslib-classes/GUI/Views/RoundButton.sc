// W. Snoei 2006

// a button with ++ functionality and rounded border

// SCv3.2 revision (todo)

RoundButton : SCUserView { 
	
	// requires a version of SuperCollider where String:prBounds is working (after april 2007?)
	
	var <value = 0;
	var <font, <states;
	var <pressed = false;
	var <radius, <>border = 2, <>moveWhenPressed = 1;
	var <extrude = true;
	var <inverse = false;
	var <>textOffset; // not used anymore, still there to prevent code breaking 
	
	*viewClass { ^SCUserView }
	
	init { |parent, bounds|
		relativeOrigin = false;
		super.init( parent, bounds );
		}
		
	mouseDown {
		arg x, y, modifiers, buttonNumber, clickCount;
		mouseDownAction.value(this, x, y, modifiers, buttonNumber, clickCount);		pressed = true; this.refresh;
		}
	
	mouseUp{arg x, y, modifiers;
		mouseUpAction.value(this, x, y, modifiers);
		pressed = false; 
		this.valueAction = value + 1;
		//this.refresh;	
		}
	
	radius_ { |newRadius| radius = newRadius; this.refresh; }
	
	draw {
		var rect, localRadius;
		var shadeSide, lightSide;
		// rect = this.bounds;
		
		if ( relativeOrigin ) // thanks JostM !
			{ rect = this.bounds.moveTo(0,0) }
			{ rect = this.bounds; };
		
		radius = radius ?? { rect.width.min( rect.height ) / 2 };


		if( inverse )
			{ lightSide = Color.black.alpha_(0.5);
		       shadeSide = Color.white.alpha_(0.5); }
			{ lightSide = Color.white.alpha_(0.5);
		       shadeSide = Color.black.alpha_(0.5); };
		
			
		states !? {
			
			GUI.pen.use {
				GUI.pen.color_( states[ value ][ 2 ] ? Color.clear );
				
				GUIPen.roundedRect( rect, radius ).fill; // not swingosc compatible!!
				
				if( extrude )
					{ GUIPen.extrudedRect( rect, radius, border, 0.17pi, pressed,
						[ lightSide, shadeSide ] ); }
					{   if( pressed, { lightSide.set }, { shadeSide.set } ); 
					   GUI.pen.width = border;
					   GUIPen.roundedRect( rect.insetBy( border/2,border/2 ), radius - 
					   	(border/2)  ).stroke; 
					};
										
			};
			
			case { states[value][0].isString }
				{
					states[value][0].drawCenteredIn( 
						rect  + ( if( pressed ) 
							{ Rect( moveWhenPressed, moveWhenPressed, 0, 0 ) } 
							{ Rect(0,0,0,0) } ),
						font,
						states[value][1] ? Color.black)
					
				} 
				{ states[value][0].class == Symbol }
				{
				GUI.pen.use {
					GUI.pen.color_(states[value][1] ? Color.black);
					if( pressed ) { GUI.pen.translate( moveWhenPressed, moveWhenPressed ) };
					DrawIcon.symbolArgs( states[value][0], rect.insetBy( border/2,border/2 ) );
					};
				 }
				{ true }
				{
				GUI.pen.use {
					GUI.pen.color_(states[value][1] ? Color.black);
					if( pressed ) { GUI.pen.translate( moveWhenPressed, moveWhenPressed ) };
					states[value][0].value( this, rect, radius ); // can be a Pen function
					};
				};
			};
		}
		
	*paletteExample { arg parent, bounds;
		var v;
		v = this.new(parent, bounds);
		v.states = [
			["Push", Color.black, Color.red],
			["Pop", Color.white, Color.blue]];
		^v
	}
	
	states_ { |inStates| states = inStates; this.refresh; }

	value_ { arg val;
		value = val % states.size;
		this.refresh;
	}

	valueAction_ { arg val; // changed 08/03/08
		if( val.round(1) != value )
			{ value = val % states.size;
				action.value(this);
				this.refresh;
			};
	}	

	font_ { |newFont| font = newFont; this.refresh; }

	extrude_ { |bool| extrude = bool; this.refresh; }
	
	// same as extrude
	bevel { ^extrude }
	bevel_ { |bool| extrude = bool; this.refresh; }
	
	inverse_ { |bool| inverse = bool; this.refresh; }
	
	
	// from SCButton:
	doAction { arg modifiers;
		action.value(this, modifiers);
	}
	
	defaultKeyDownAction { arg char, modifiers, unicode;
		if (char == $ , { this.valueAction = this.value + 1; ^this });
		if (char == $\r, { this.valueAction = this.value + 1; ^this });
		if (char == $\n, { this.valueAction = this.value + 1; ^this });
		if (char == 3.asAscii, { this.valueAction = this.value + 1; ^this });
		^nil		// bubble if it's an invalid key
	}
	
	defaultGetDrag { 
		^this.value
	}
	defaultCanReceiveDrag {
		^currentDrag.isNumber or: { currentDrag.isKindOf(Function) };
	}
	defaultReceiveDrag {
		if (currentDrag.isNumber) {
			this.valueAction = currentDrag;
		}{
			this.action = currentDrag;
		};
	}
}
