RoundView2 : UserViewHolder {
	
	// called RoundView2 for now, until all Smooth/Round views have been updated

	// fix for drawing slightly outside an SCUserView
	// this class doesn't draw the focusring itself,
	// it only handles the resizing.
	
	classvar <>focusRingSize = 3;
	
	var <expanded = true;
	var <shrinkForFocusRing = false; // only when expanded == false
	
	var <enabled = true, couldFocus = true;
	
	var <backgroundImage;
	
	*new { arg parent, bounds ... rest;
		^super.new( parent, bounds, *rest ).initRoundView( parent, bounds );
	}
	
	*prShouldExpand { arg v;
		^( GUI.id == \cocoa and: { v.isKindOf( GUI.hLayoutView ).not && 
			{ v.isKindOf( GUI.vLayoutView ).not }  } );
	}
	
	initRoundView { |parent, bounds|
		if( this.class.prShouldExpand( parent ).not, { expanded = false });
		if( expanded ) { this.bounds = view.bounds }; // resize after FlowLayout
		view.focusColor = Color.clear;
	}
	
	drawBounds { ^if( expanded ) 
			{ this.bounds.moveTo(focusRingSize,focusRingSize); } 
			{ if( shrinkForFocusRing )
				{ this.bounds.insetBy(focusRingSize,focusRingSize)
						.moveTo(focusRingSize,focusRingSize) }
				{ this.bounds.moveTo(0,0); }; 
			}
		}
			
	bounds { ^if( expanded ) 
			{ view.bounds.insetBy(focusRingSize,focusRingSize); } 
			{ view.bounds; }; 
		}
	
	bounds_ { |newBounds| 
		if( expanded ) 
			{ view.bounds = newBounds.asRect.insetBy(focusRingSize.neg,focusRingSize.neg); }
			{ view.bounds = newBounds; } ;
		}
		
	expanded_ { |bool|
		var bnds;
		bnds = this.bounds;
		expanded = bool ? expanded;
		this.bounds = bnds;
		}
		
	shrinkForFocusRing_ { |bool|
		shrinkForFocusRing = bool ? shrinkForFocusRing;
		this.refresh;
		}
		
	canFocus_ { |bool|
		if( enabled ) { super.canFocus = bool };
		couldFocus = bool;
		}
		
	enabled_ { |bool|
		enabled = (bool != false); // can be anything
		if( enabled == true )
			{ super.canFocus = couldFocus; } 
			{ super.canFocus = false; };
		this.refresh;
		}
		
	backgroundImage_ { arg image, tileMode=1, alpha=1.0, fromRect;
		if( image.notNil )
			{ backgroundImage = [ image, tileMode, alpha, fromRect ]; }
			{ backgroundImage = nil };
		this.refresh;
		}
	
	}