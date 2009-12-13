+ SCWindow {   // shortcut for FlowLayout -- returns the Window !!
	decorate { |margin, gap| view.decorator_( FlowLayout( view.bounds, margin, gap ) ); }
	}