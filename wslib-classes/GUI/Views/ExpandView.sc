// wslib 2010

// a CompositeView with a triangle button which can expand itself to a larger size
// automatically re-flows parent view if a decorator is present

// TODO: add correct resize_ behaviour

/*

(
w = Window( "ExpandView", Rect(128,64,208,220) ).front;
w.asView.addFlowLayout;
v = 5.collect({ // create 5 ExpandViews
	ExpandView( w, 200@112, 200@16  ) //
});
)

// typical "accordeon" action:
v.do( _.expandAction = { |vw| v.do({ |vwx| if( vwx != vw ) { vwx.collapse } }) } );

*/

ExpandView {
	
	classvar <>defaultTime = 0.1;
	
	var <bigBounds, <smallBounds;
	var <composite, <view, <header, <button;
	var <>time, <task;
	var <>expandAction, <>collapseAction;
	
	*new { |parent, bigBounds, smallBounds, collapsed = true|
		^super.newCopyArgs( bigBounds.asRect, smallBounds.asRect ).init( parent, collapsed );
	}
	
	init { |parent, collapsed|
		time = defaultTime;
		composite = CompositeView( parent, if( collapsed ) { smallBounds } { bigBounds } );
		
		view = CompositeView( composite, 
			composite.bounds.moveTo(0,0).insetAll(14, 0,0,0 ) 
		).resize_(5);
		
		button = RoundButton( composite, Rect(0,0, 14, smallBounds.height ) )
			.border_(0)
			.label_( [ 'triangle', 'triangle_0.5pi' ] )
			.action_( [ { this.expand }, { this.collapse } ] )
			.canFocus_( false )
			.value_( collapsed.not.binaryValue );
			
		this.background = Color.white.alpha_(0.5);
		
	}
	
	background_ { |color| composite.background = color; }
	
	asView { ^view }
	
	add { |...args| view.add( *args ); }
	
	resize_ { arg resize; composite.resize_( resize ) } 
	
	addFlowLayout { |margin, gap| ^view.addFlowLayout( margin, gap ) }
	
	collapse { 
		if( composite.bounds != smallBounds ) {
			collapseAction.value( this );
			this.changeSize( smallBounds );
			button.value = 0;
		}
	}
	
	expand { 
		if( composite.bounds != bigBounds ) {
			expandAction.value( this );
			this.changeSize( bigBounds );
			button.value = 1;
		}
	}
	
	bounds { ^composite.bounds }

	changeSize { |newBounds|
		var oldBounds, n;
		task.stop;
		if( time == 0 ) { 
			this.changeSizeOnce( newBounds );
		} {
			oldBounds = composite.bounds;
			n = (time / 0.02).floor;
			task = {
				n.do({ |i|
					{ this.changeSizeOnce( oldBounds.blend( newBounds, i/(n-1) ) ); }.defer;
					0.02.wait;
				});
			}.fork;
		};
	}
	
	changeSizeOnce { |newBounds|
		composite.bounds = newBounds;
		this.hideOutside;
		this.reflowParentGUI;
	}
	
	hideOutside {
		var bounds;
		bounds = view.bounds.moveTo(0,0);
		view.children.do({ |child|
			child.visible = bounds.contains( child.bounds.insetBy(3,3) )
		});
	}
	
	reflowParentGUI {
		var parent;
		if( composite.parent.decorator.notNil ) {
			parent = composite.parent;
			parent.decorator.reset;
			parent.children.do({ |widget|
					if(widget.isKindOf( StartRow ),{
						parent.decorator.nextLine
					},{
						parent.decorator.place(widget);
					})
				});
		}
	}
	
	
}