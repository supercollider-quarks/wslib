// wslib 2006

LineView : SCUserView {
	
	// just a simple line..
	// use as border or separator
	
	var <orientation = \h, <full = true, <color, <width, <margin = 0;
	
	*viewClass { ^SCUserView }
	
	init { |parent, bounds|
		bounds = bounds.asRect;
		if( bounds.width < bounds.height )
			{ orientation = \v };
		color = Color.gray(0.25).alpha_(0.5);
		if( orientation == \h )
			{ width = bounds.height }
			{ width = bounds.width }; 
		super.init( parent, bounds.asRect );
		this.canFocus_( false );
		parent.refresh; 
		}
	
	draw {
		var realMargin;
		color.set;
		Pen.width_( width );
		realMargin = margin * width;
		case { orientation == \h }
			{ if( full )	
				{ Pen.line( 
					realMargin@(this.bounds.center.y),
					(this.parent.bounds.width - realMargin)@(this.bounds.center.y)
					);  }
				{ Pen.line( 
					(this.bounds.left + realMargin)@(this.bounds.center.y),
					(this.bounds.right - realMargin)@(this.bounds.center.y)
					);  };
			}
			{ orientation == \v }
			{ if( full )	
				{ Pen.line( 
					(this.bounds.center.x)@realMargin,
					(this.bounds.center.x)@(this.parent.bounds.height - realMargin)
					);  }
				{ Pen.line( 
					(this.bounds.center.x)@(this.bounds.top + realMargin),
					(this.bounds.center.x)@(this.bounds.bottom - realMargin)
					);  };
			 };
			 
		Pen.stroke;
		}
	
	refreshView { if( full ) { parent.refresh; } { this.refresh; } }
	
	color_ { |newColor| color = newColor; this.refreshView; }
	orientation_ { |newOrientation| orientation = newOrientation ? orientation; this.refreshView; }
	full_ { |bool| full = bool; parent.refresh; } // always refresh parent when changing this
	width_ { |newWidth| width = newWidth ? { 
		if( orientation == \h )
			{ this.bounds.height }
			{ this.bounds.width }; }; this.refreshView;
		}
	margin_ { |newMargin| margin = newMargin; this.refreshView; }
		
	alpha { ^color.alpha }
	alpha_ { |newAlpha| color.alpha_( newAlpha ); this.refreshView; }
	
	}
