+ SCWindow {   // shortcut for FlowLayout
	decorate { |margin, gap| view.decorator_( FlowLayout( view.bounds, margin, gap ) ); }
	}