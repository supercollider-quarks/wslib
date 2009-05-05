// wslib 2007
// wrapper classes for GUI-style coding
// returns the appropriate GUI object
//
// also provides a bit of SC2 compat..

//// ALL OBSOLETE IN SC3.3, except GUIPen for backward compat

/*
// use like this:
(
w = GUIWindow( "panel", Rect( 128, 64, 300, 300 ) ).front;
w.view.decorator = FlowLayout( w.view.bounds );
20.do({ |i| GUIButton( w, 60@20 ).states_( [[ i.asString, Color.black, Color.rand ]] ); });
w.drawHook = {
	GUIPen.moveTo( 290@290 );
	10.do({ GUIPen.lineTo( 300.rand@(100.rand + 200 ) ) });
	GUIPen.stroke;
	};
w.refresh;
)

GUI.swing; // change scheme and run again
GUI.cocoa;

*/

/*
GUIWindow {

	*new { |...args|
		^GUI.perform(this.guiSchemeClassName).new( *args );
		}
	
	*guiSchemeClassName { // valid for most classnames
		var classNameString;
		classNameString = this.name.asString;
		^( classNameString[3].toLower ++ classNameString[4..] ).asSymbol; 
		}
	
	}

///////////////// Common -> GUI -> Base /////////////////
	
GUIView : GUIWindow { }

GUICompositeView : GUIView { }
GUIHLayoutView : GUIView { }
GUIVLayoutView : GUIView { }
GUISlider : GUIView { }
GUIRangeSlider : GUIView { }
GUISlider2D : GUIView { }
GUITabletSlider2D : GUIView { }
GUIButton : GUIView { }
GUIPopUpMenu : GUIView { }
GUIStaticText : GUIView { }
GUIListView : GUIView { }
GUIDragSource : GUIView { }
GUIDragSink : GUIView { }
GUIDragBoth : GUIView { }
GUINumberBox : GUIView { }
GUITextField : GUIView { }
GUIUserView : GUIView { }
GUIMultiSliderView : GUIView { }
GUIEnvelopeView : GUIView { }
GUITabletView : GUIView { }
GUISoundFileView : GUIView { }
GUIMovieView : GUIView { }
GUITextView : GUIView { }
GUIQuartzComposerView : GUIView { }
GUIScopeView : GUIView { }
GUIFreqScope : GUIView { }
GUIFreqScopeView : GUIView { }
GUIEZSlider : GUIView { *guiSchemeClassName { ^\ezSlider }  }
GUIEZNumber : GUIView { *guiSchemeClassName { ^\ezNumber }  }
GUIStethoscope : GUIView { }

GUIFont : GUIView { }

*/

// Pen: different use
GUIPen { 
	*doesNotUnderstand { arg selector ...args; 
		^GUI.pen.perform( selector, *args ); 
		} 
	}
	
/*

///////////////// Common -> Audio /////////////////

GUIMouseX : GUIView { }
GUIMouseY : GUIView { }
GUIMouseButton : GUIView { }
GUIKeyState : GUIView { }

///////////////// Common -> OSX /////////////////

GUIDialog : GUIView { }
GUISpeech : GUIView { }

///////////////// extras /////////////////

GUICheckBox : GUIView { }
GUITabbedPane : GUIView { }

///////////////// crucial /////////////////

GUIStartRow : GUIView { }

///////////////// other /////////////////

GUIKnob : GUIView { }

*/