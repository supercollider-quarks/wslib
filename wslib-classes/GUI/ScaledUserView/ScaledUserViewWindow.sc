// wslib 2006
// scaled SCUserView

ScaledUserViewWindow {
	
	classvar <>defaultBounds, <>defaultFromBounds;
	
	var <window, <userView, <scaleSliders, <moveSliders, <>drawHook; 
	var <viewOffset;
	var <>maxZoom = 8;
	var <>onClose;
	
	*initClass { 
		defaultBounds = Rect( 350, 128, 400, 400 );
		defaultFromBounds = Rect(0,0,1,1); // different from ScaledUserView
		}
		
	doesNotUnderstand { arg selector ... args;
		var res;
		res = userView.perform(selector, *args);
		if (res.class == ScaledUserView )
			{ ^this }
			{ ^res }
		}
		
	*new { |name, bounds, fromBounds, viewOffset|
		name = name ? "ScaledUserViewWindow";
		bounds = (bounds ? defaultBounds).asRect;
		fromBounds = (fromBounds ? defaultFromBounds).asRect;
		viewOffset = viewOffset ? [1,1];
		^super.new.init( name, bounds, fromBounds, viewOffset);
		}
		
	front { window.front }
	
	init { | inName, inBounds, inFromBounds, inViewOffset|
		var sliderKnobColor, sliderBackColor;
		sliderKnobColor = Color.gray(0.2);
		sliderBackColor = Color.black.alpha_(0.33);
		window = SCWindow( inName, inBounds );
		//window.view.background = Color.gray(0.8);
		case { inViewOffset.class == Point }
			{ viewOffset = [ inViewOffset.x, inViewOffset.y ]; }
			{ inViewOffset.size == 0 }
			{ viewOffset = [ inViewOffset, inViewOffset ]; }
			{ inViewOffset.size == 1 }
			{ viewOffset = inViewOffset ++ inViewOffset; }
			{ true }
			{ viewOffset = inViewOffset; };
		
		window.onClose = { onClose.value( this, window ) };
			
		userView = ScaledUserView( window, (window.asView.bounds + 
			Rect(0,0,-19,-19)).insetAll( *(viewOffset ++ [0,0]) ), inFromBounds );
			
		userView.view.resize_(5);
		userView.background = Color.white.alpha_(0.5);
		
		scaleSliders = [ 
			SmoothSlider( window,  Rect( 
		 			window.asView.bounds.width - 75,  
		 			window.asView.bounds.height - 16, 48, 13 )  )
				.value_(0).action_({ |v| 
					userView.scaleH = 1 + (v.value * maxZoom.asPoint.x);
					this.setMoveSliderWidths( window.asView.bounds );
					/*
					if( userView.keepRatio )
						{ scaleSliders[1].valueAction = (1 - v.value); };
					*/
					})
				.knobColor_( sliderKnobColor )
		 		.hilightColor_( nil )
		 		.background_(sliderBackColor)
		 		.knobSize_( 1 )
		 		.canFocus_( false )
				.resize_(9),
			SmoothSlider( window,  Rect( 
		 			window.asView.bounds.width - 16,  
		 			window.asView.bounds.height - 75, 13, 48 ) )
				.value_(1).action_({ |v| 
					userView.scaleV = 1 + ((1 - v.value) * maxZoom.asPoint.y);
					this.setMoveSliderWidths( window.asView.bounds );
					/*
					if( userView.keepRatio )
						{ scaleSliders[0].valueAction = (1 - v.value); };
					*/
					})
				.knobColor_( sliderKnobColor )
				.hilightColor_( nil )
				.background_(sliderBackColor)
				.knobSize_( 1 )
		 		.canFocus_( false )
				.resize_(9) ];
		 
		moveSliders = [ 
			SmoothSlider( window,  Rect( viewOffset[0],  
				 	window.asView.bounds.height - 16, 
				 	(window.asView.bounds.width - 78) - viewOffset[0], 13 )  )
				 .value_(0.5).action_({ |v| userView.moveH = v.value; })
				 .knobColor_( sliderKnobColor)
				 .hilightColor_( nil )
				 .background_(sliderBackColor)
				 .resize_(8)
		 		.canFocus_( false ),
			SmoothSlider( window,  Rect( 
					window.asView.bounds.width - 16,  viewOffset[1], 13, 
					(window.asView.bounds.height - 78) - viewOffset[1] )  )
				 .value_(0.5).action_({ |v| userView.moveV = 1 - (v.value); })
				 .knobColor_( sliderKnobColor )
				 .hilightColor_( nil )
				 .background_(sliderBackColor)
				 .resize_(6)
		 		.canFocus_( false ) ];
		
		this.setMoveSliderWidths;
		
		
		
		
		window.drawHook = { |w|
			this.setMoveSliderWidths;
			drawHook.value( window );
			};
			
		window.front;
		}
		
	updateSliders { |scaleFlag = true, moveFlag = true|
		if( scaleFlag )
			{ scaleSliders[0].value = (userView.scaleH - 1) / maxZoom.asPoint.x;
			scaleSliders[1].value = 1 - ((userView.scaleV - 1) / maxZoom.asPoint.y );
			this.setMoveSliderWidths;
			};
		if( moveFlag )
			{
			moveSliders[0].value = userView.moveH;
			moveSliders[1].value = 1 - userView.moveV;
			};
		}
		
	setMoveSliderWidths { // |rect| // not used anymore
		moveSliders[0].relThumbSize = (1 / userView.scaleH).min(1);
		moveSliders[1].relThumbSize = (1 / userView.scaleV).min(1);
			}


	scaleH_ { |newScaleH, refreshFlag, updateFlag|
		updateFlag = updateFlag ? true;
		userView.scaleH_( newScaleH, refreshFlag );
		this.updateSliders( updateFlag, false );
		 }
		 
	scaleV_ { |newScaleV, refreshFlag, updateFlag|
		updateFlag = updateFlag ? true;
		userView.scaleV_( newScaleV, refreshFlag );
		this.updateSliders( updateFlag, false );
		 }
		 
	scale_ { |newScaleArray, refreshFlag, updateFlag| // can be single value, array or point
		updateFlag = updateFlag ? true;
		userView.scale_( newScaleArray, refreshFlag );
		this.updateSliders( updateFlag, false );
		}
		
		
	moveH_ { |newMoveH, refreshFlag, updateFlag|
		updateFlag = updateFlag ? true;
		userView.moveH_( newMoveH, refreshFlag );
		this.updateSliders( false, updateFlag );
		 }
		 
	moveV_ { |newMoveV, refreshFlag, updateFlag|
		updateFlag = updateFlag ? true;
		userView.moveV_( newMoveV, refreshFlag );
		this.updateSliders( false, updateFlag );
		 }
		 
	move_ { |newMoveArray, refreshFlag, updateFlag| // can be single value, array or point
		updateFlag = updateFlag ? true;
		userView.move_( newMoveArray, refreshFlag );
		this.updateSliders( false, updateFlag );
		}
		
	movePixels_ {  |newPixelsArray, limit, refreshFlag, updateFlag| 
		updateFlag = updateFlag ? true;
		userView.movePixels_( newPixelsArray, limit, refreshFlag );
		this.updateSliders( false, updateFlag );
		}
		
	keepRatio_ { |bool = false|
		userView.keepRatio = bool;
		scaleSliders[1].visible = bool.not;
		this.scaleH = this.scaleH; 
		 }
		
	refresh { ^window.refresh }
		
	} 