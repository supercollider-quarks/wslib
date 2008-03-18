// wslib 2007
// GUI compat (for the time being )

+ Color {
	guiSet {
		 if( GUI.pen.respondsTo( \color_ ) )
			{ GUI.pen.color = this }
			{ this.set  }
		}
	}
	
+ String {
	guiDraw {
		this.guiDrawAtPoint(Point(0,0), GUI.font.default, Color.black);
	}
	guiDrawAtPoint { arg point, font, color;
		if( GUI.pen.respondsTo( \stringAtPoint ) )
			{ 	if( color.notNil ) 
					{ GUI.pen.color = color; };
				if( font.notNil )
					{ GUI.pen.font = GUI.font.new( font.name, font.size ); };
				GUI.pen.stringAtPoint( this, point ) }
			{ ^this.drawAtPoint( point, font, color );  }
		}
		
	guiDrawInRect { arg rect, font, color;
		if( GUI.pen.respondsTo( \stringInRect ) )
			{ 	if( color.notNil ) 
					{ GUI.pen.color = color; };
				if( font.notNil )
					{ GUI.pen.font = GUI.font.new( font.name, font.size ); };
				GUI.pen.stringAtPoint( this, rect ) }
			{ ^this.drawInRect( rect, font, color );  }
	}
	
	guiDrawCenteredIn { arg inRect, font, color;
		this.guiDrawAtPoint(this.bounds( font ).centerIn(inRect), font, color);
	}
	guiDrawLeftJustIn { arg inRect, font, color;
		var pos, bounds;
		bounds = this.bounds( font );
		pos = bounds.centerIn(inRect);
		pos.x = inRect.left + 2;
		this.guiDrawAtPoint(pos, font, color);
	}
	guiDrawRightJustIn { arg inRect, font, color;
		var pos, bounds;
		bounds = this.bounds( font );
		pos = bounds.centerIn(inRect);
		pos.x = inRect.right - 2 - bounds.width;
		this.guiDrawAtPoint(pos, font, color);
	}

	}