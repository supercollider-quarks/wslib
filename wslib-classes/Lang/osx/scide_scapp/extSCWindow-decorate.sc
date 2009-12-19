+ SCWindow {   // shortcut for FlowLayout -- returns the Window !!
	decorate { |margin, gap| view.decorator_( FlowLayout( view.bounds, margin, gap ) ); }
	}
	
+ SCView {   // shortcut for FlowLayout -- returns the Window !!
	decorate { |margin, gap| this.decorator_( FlowLayout( this.bounds, margin, gap ) ); }
	}