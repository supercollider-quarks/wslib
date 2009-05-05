ColPenGUI : CocoaGUI { // wslib 2007 - for use with ColPen to make { ... }.asGUICode possible
	classvar	<inited = false;
	
	*initClass { |mustInitGUI = true|
		inited = true;
		mustInitGUI.if({ Class.initClassTree( GUI ); });
		GUI.add( this );
		GUI.schemes[ \colpen, this ];
	}
	
	*id { ^\colpen }
	
	*pen { ^ColPen }
	
	}