SCRoundNumberBox : RoundView {
	
	classvar <>defaultFormatFunc, <>defaultInterpretFunc, <>defaultFont;
	
	var <value = 0; // from roundbutton
	var <string;
	var <font;
	var <align = \left;
	var <radius, <border = 1;
	var <extrude = true;
	var <inverse = false;
	var <focusColor, <stringColor;
	var <formatFunc, <>interpretFunc, <>allowedChars = "+-.eE*/()%";
	var <innerShadow;
	
	var <keyString, <>step=1, <>scroll_step=1;
	var <typingColor, <normalColor, <stringColor;
	var <background;
	var <>clipLo = -inf, <>clipHi = inf, hit, inc=1.0, 
		<>scroll=true, <>shift_step=0.1, <>ctrl_step=10;
	
	var <>shift_scale = 100.0, <>ctrl_scale = 10.0, <>alt_scale = 0.1;
		
	var <charSelectColor, <charSelectIndex = -1;
		
	*viewClass { ^SCUserView }
	
	*initClass { 
		defaultFormatFunc = { |value| value };
		defaultFont = Font( "Helvetica", 12 );
		defaultInterpretFunc = { |string| string.interpret };
		 }
	
	doesNotUnderstand { |selector ... args| // dirty maybe, but any reason not to do this?
		if( selector.isSetter )
			{ if( this.class.instVarNames.includes( selector.asGetter ) )
				{ this.slotPut( selector.asGetter, args[0] ); this.refresh }
				{ ^super.doesNotUnderstand( selector, *args ) }; }
			{ ^super.doesNotUnderstand( selector, *args ) };
		}
	
	init { |parent, bounds|
		/*
		// now handled by RoundView
		relativeOrigin = true;
		super.init( parent, bounds.asRect.insetBy(-3,-3) );
		super.focusColor = Color.clear;
		*/
		super.init( parent, bounds );
		typingColor = Color.red;
		normalColor = Color.black;
		background = Color.white;
		stringColor = normalColor;
		formatFunc = defaultFormatFunc;
		interpretFunc = defaultInterpretFunc;
		font = defaultFont;
		}
		
	/*
	drawBounds { ^this.bounds.moveTo(3,3); }
	bounds { ^super.bounds.insetBy(3,3); }
	bounds_ { |newBounds| super.bounds = newBounds.asRect.insetBy(-3,-3); } 
	*/
	
	getScale { |modifiers| 
		^case
			{ modifiers & 131072 == 131072 } { shift_scale }
			{ modifiers & 262144 == 262144 } { ctrl_scale }
			{ modifiers & 524288 == 524288 } { alt_scale }
			{ 1 };
	}

	valueAction_ { arg val;
		value = val;
		this.prClipValue;
		action.value(this);
		this.refresh;
		}
	
	value_ { arg val;
		value = val;
		this.prClipValue;
		this.refresh;
		}
		
	interpret {	
		value = interpretFunc.value(keyString) ? value;
		keyString = nil;
		action.value( this, value );
		stringColor = normalColor;
		this.refresh;
		}
		
	prClipValue {
		if( value.respondsTo( 'clip' ) && { value.class != String } )
			{ value = value.clip(clipLo, clipHi); };
		}
		
	defaultGetDrag { 
		^value
	}
	
	defaultCanReceiveDrag {
		^(currentDrag.isNumber) or: { currentDrag.class == String };
	}
	defaultReceiveDrag {
		if( currentDrag.class == String )
			{ this.valueAction = currentDrag.interpret ? value; }
			{ this.valueAction = currentDrag;  }	
	}
	
	draw {
		var rect, localRadius;
		var shadeSide, lightSide, stringRect, stringBounds, stringStart, stringWidth;
		rect = this.drawBounds;
		
		radius = radius ?? { (rect.height/4).min( rect.width/2 ) };
		
		if( this.hasFocus ) // rounded focus rect
			{
			Pen.use({
				Pen.color = focusColor ?? { Color.gray(0.2).alpha_(0.8) };
				Pen.width = 2;
				Pen.roundedRect( rect.insetBy(-2,-2), radius + 1 );
				Pen.stroke;
				});
			};

		if( inverse )
			{ lightSide = Color.black.alpha_(0.5);
		       shadeSide = Color.white.alpha_(0.5); }
			{ lightSide = Color.white.alpha_(0.5);
		       shadeSide = Color.black.alpha_(0.5); };
		
		Pen.use {
			Pen.color_( background ?? { Color.clear } );
				
			Pen.roundedRect( rect, radius ).fill; // not swingosc compatible!!
			
			if( extrude )
				{ Pen.extrudedRect( rect, radius, border, 0.17pi, false,
					[ shadeSide, lightSide ] ); }
				{  Pen.color =  shadeSide;
				   Pen.width = border;
				   Pen.roundedRect( rect.insetBy( border/2,border/2 ), radius - 
				   	(border/2)  ).stroke; 
				};
			
			if( innerShadow.notNil )
				{
				Pen.use({
					Pen.roundedRect( rect.insetBy(border - 0.1, border - 0.1), radius ).clip;
					if( innerShadow.isNumber )
							{ Pen.setShadow( innerShadow@(innerShadow.neg), innerShadow, 
								Color.black.alpha_(0.5) ); }
							{ Pen.setShadow( *innerShadow.asCollection );  };
					Pen.width = 5;
					Pen.color = Color.black;
					Pen.roundedRect(  rect.insetBy( border - 3.5, border - 3.5 ),
						 radius ).stroke;
					});
				};
										
			Pen.use({
				Pen.roundedRect( rect.insetBy( border ), radius ).clip;
				
				string = keyString ?? { formatFunc.value(value).asString };
				stringRect = this.stringRect;
						
				if( (charSelectIndex >= 0) and: { charSelectIndex < string.size } )
					{
					Pen.color = charSelectColor ?? { Color(0.75, 0.75, 0.875, 0.5) };
					Pen.addRect( this.charSelectRect( stringRect,
						charSelectIndex, 1 ) ).fill;
					
					};
					
				Pen.font_( font );
				Pen.color_( stringColor ? Color.black );
				
				Pen.perform( 
					( left: \stringLeftJustIn, right: \stringRightJustIn, 
						center: \stringCenteredIn, middle: \stringCenteredIn )[ align ]
						? \stringLeftJustIn, string, stringRect );
						
					});
			} 
		}
		
	stringRect { ^this.drawBounds.insetAll( (radius.asCollection@@[0,3]).maxItem / 2,
				0, (radius.asCollection@@[1,2]).maxItem / 2, 0 );
			}
			
	charSelectRect { |stringRect, start = 0, range = 1|
		var rect, stringStart, stringWidth;
		rect = this.drawBounds;
		stringRect = stringRect ?? { this.stringRect; };
		stringStart = string[..start-1].bounds( font ).width;
		stringWidth = string[start..(start+range)-1].bounds( font ).width;
		^Rect( switch( align,
				\left, { (stringStart + stringRect.left) + 1 },
				\right, { ((stringStart + stringRect.right) - string.bounds(font).width) - 2 },
				\center, { stringStart + stringRect.left +
							((stringRect.width - string.bounds(font).width)/2)  },
				\middle, { stringStart + stringRect.left +
							((stringRect.width - string.bounds(font).width)/2)
						 }) ?? {  stringStart + stringRect.left },
			rect.top, stringWidth + 0.5, rect.height );
		}
		
	charIndexFromPoint { |point, exclude|
		var i = 0, stringWidth, stringSize, stringRect;
		stringSize = string.size;
		stringRect = this.charSelectRect(nil, 0, stringSize );
		//stringWidth = stringRect.width;
		exclude = exclude ? [];
		if( exclude.includes(i) ) { i = i+1 };
		while { (i < string.size) && { 
			(stringRect.left + string[..i].bounds( font ).width) < point.x; }  }
			{ i = i+1; while { exclude.includes(i) } { i = i+1 };  
				};
		^i;
		}
		
	increment {arg mul=1; this.valueAction = this.value + (step*mul); }
	decrement {arg mul=1; this.valueAction = this.value - (step*mul); }
	
	*paletteExample { arg parent, bounds;
		var v;
		v = this.new(parent, bounds);
		v.value = 123.456;
		^v
	}
	
	mouseDown { arg x, y, modifiers, buttonNumber, clickCount;
		hit = Point(x,y);
		if (scroll == true, { inc = this.getScale(modifiers) });			
		mouseDownAction.value(this, x, y, modifiers, buttonNumber, clickCount)
	}

	mouseMove { arg x, y, modifiers;
		var direction;
		if (scroll == true, {
			direction = 1.0;
				// horizontal or vertical scrolling:
			if ( (x - hit.x) < 0 or: { (y - hit.y) > 0 }) { direction = -1.0; };
			
			if( value.respondsTo( '+' ) && { value.class != String }  )
				{ this.valueAction = (this.value + (inc * this.scroll_step * direction)); };
			hit = Point(x, y);
		});
		mouseMoveAction.value(this, x, y, modifiers);	
	}
	
	mouseUp{ inc=1 }
	
	defaultKeyDownAction { arg char, modifiers, unicode;
		var zoom = this.getScale(modifiers);
		
		// standard chardown
		if (unicode == 16rF700, { this.increment(zoom); ^this });
		if (unicode == 16rF703, { this.increment(zoom); ^this });
		if (unicode == 16rF701, { this.decrement(zoom); ^this });
		if (unicode == 16rF702, { this.decrement(zoom); ^this });
		
		if ((char == 3.asAscii) || (char == $\r) || (char == $\n), { // enter key
			if (keyString.notNil,{ // no error on repeated enter
				value = interpretFunc.value(keyString) ? value;
				keyString = nil;
				action.value( this, value );
				stringColor = normalColor;
				this.refresh;
			});
			^this
		});
		if (char == 127.asAscii, { // delete key
			keyString = nil;
			//this.value = object;
			stringColor = normalColor;
			this.refresh;
			
			^this
		});
		if (char.isDecDigit || allowedChars.includes(char), { 
		
			// simple expressions will be interpreted
			if (keyString.isNil, { 
				keyString = String.new;
				stringColor = typingColor;
			});
			keyString = keyString.add(char);
			this.refresh;
			//this.value = keyString.interpret;
			^this
		});
		^nil		// bubble if it's an invalid key
	}

	}
	
RoundNumberBox : SCRoundNumberBox { } // make redirect later