RoundSlider : SmoothSlider {

	// a SmoothSlider with different default styling
	// matches with RoundButton and RoundNumberBox
	
	init { arg parent, bounds;
		super.init( parent, bounds );
		
		// background, hilightColor, borderColor, knobColor, stringColor
		color = [ nil, nil, Color.clear, Color.clear, Color.black ];
		extrude = true;
		border = 1;
		knobSize = 0.5;
		thumbSize = 12;
		
		}
	
	}
	
SmoothButton : RoundButton {
	
	init { |parent, bounds|
		super.init( parent, bounds );
		extrude = false;
		moveWhenPressed = 0;
		}
	
	}
	
SmoothNumberBox : RoundNumberBox {
	
	init { |parent, bounds|
		super.init( parent, bounds );
		extrude = false;
		border = 0;
		background = Color.white.alpha_(0.5);
		typingColor = Color.red(0.5).alpha_(0.75);
		}
	
	}