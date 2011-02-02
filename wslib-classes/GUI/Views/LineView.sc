// wslib 2006

LineView : UserViewHolder {
	
	// just a simple line..
	// use as border or separator
	
	var <orientation = \h, <color, <width, <margin = 0;
	
	init { |parent,bounds|
		bounds = bounds.asRect;
		if( bounds.width < bounds.height )
			{ orientation = \v };
		if( orientation == \h )
			{ width = bounds.height }
			{ width = bounds.width };
		color = Color.gray(0.25).alpha_(0.5);
		this.canFocus_( false );		
	}
	
	draw {
		var realMargin;
		Pen.color_( color );
		Pen.width_( width );
		realMargin = margin * width;
		switch ( orientation, 
			\h, { Pen.line( 
					(this.bounds.left + realMargin) @ (this.bounds.center.y),
					(this.bounds.right - realMargin) @ (this.bounds.center.y)
				);
			}, 
			\v, { Pen.line( 
					(this.bounds.center.x) @ (this.bounds.top + realMargin),
					(this.bounds.center.x) @ (this.bounds.bottom - realMargin)
				); 
			});
			 
		Pen.stroke;
	}	
	
	full { DeprecatedError( this, thisMethod ).throw; ^true }
	full_ { DeprecatedError( this, thisMethod ).throw; }
	
	color_ { |newColor| color = newColor; this.refresh; }
	orientation_ { |newOrientation| orientation = newOrientation ? orientation; this.refresh; }
	width_ { |newWidth| width = newWidth ? { 
		if( orientation == \h )
			{ this.bounds.height }
			{ this.bounds.width }; }; 
		this.refresh;
		}
	margin_ { |newMargin| margin = newMargin; this.refresh; }
		
	alpha { ^color.alpha }
	alpha_ { |newAlpha| color.alpha_( newAlpha ); this.refresh; }
	
}
