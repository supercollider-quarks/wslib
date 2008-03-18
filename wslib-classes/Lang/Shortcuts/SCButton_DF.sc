// wslib 2006

// uses Font:default as font at initiation; returns a regular view

SCButton_DF {
	*viewClass { ^SCButton }
	
	*new { arg parent, bounds;
		^this.viewClass.new(parent, bounds).font_( Font.default ? Font( "Helvetica", 12) );
		}
	}

SCPopUpMenu_DF : SCButton_DF { *viewClass { ^SCPopUpMenu } }
	
SCStaticText_DF : SCButton_DF { *viewClass { ^SCStaticText } }

SCNumberBox_DF : SCButton_DF { *viewClass { ^SCNumberBox } }

SCListView_DF : SCButton_DF { *viewClass { ^SCListView } }

