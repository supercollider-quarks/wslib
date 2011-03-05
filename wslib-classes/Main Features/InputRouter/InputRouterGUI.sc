InputRouterGUI {
	
	var <inputRouter, <view, <views, <controller, <skin;
	var <rowHeight = 15;
	
	*new { |inputRouter, parent, bounds|
		^super.new.init( parent, bounds ).inputRouter_( inputRouter );
	}
	
	init { |parent, bounds|
		var resize =0;
		
		
		if( parent.isNil ) {
			bounds = bounds ? Rect( 300,128, 220, 300 );
		};
	
		view = SmoothScrollView( parent, bounds );
		view.hasHorizontalScroller = false;
		view.asView.addFlowLayout( 2@0, 4@4 );
		view.asView.onClose_({ 
			 inputRouter.removeDependant( this );
		});
		skin = ( hiliteColor: Color.gray(0.3), font: Font( Font.defaultSansFace, 10 ) );
	}
	
	inputRouter_ { |ir|
		inputRouter.removeDependant( this );
		inputRouter = ir;
		this.rebuildViews;
	}
	
	rebuildViews { 
		var collapsed;
		collapsed = views.collect({ |item| item.view.collapsed }); 
		this.removeViews;
		this.createViews( collapsed );
	}
	
	removeViews {
		 inputRouter.removeDependant( this );
		 view.asView.removeAll;
		 view.asView.decorator.reset;
	}
		
	update { |ir, what ...args| // this object is also it's own controller
		if( what.notNil )
			{ switch( what,
				\level, { this.setLevelView( *args ); },
				\meterLevel, { this.setMeterView( *args ) },
				\input, { this.setInputView( *args ) },
				\settings, { this.rebuildViews; },
				\stop, { { views.do({ |item|
						item.channelViews.do({ |iitem|
							iitem.meter.value = 0;
							iitem.meter.peakLevel = 0;
						});
					}); }.defer(0.1); 
				});
			};
	}
	
	setLevelView { |i = 0, ii = 0, dB = 0|
		views[i].channelViews[ii].level.value = dB;
	}
	
	setMeterView { |i = 0, ii = 0, array|
		var channelView;
		{	channelView =  views[i].channelViews[ii];
			if( channelView.notNil && { channelView.meter.isClosed.not } ) {
				views[i].channelViews[ii].meter.value = array[0].linlin(-80,0,0,1,\none);
				views[i].channelViews[ii].meter.peakLevel = array[1].linlin(-80,0,0,1,\none);
			};
		}.defer;
	}
	
	setInputView { |i = 0, ii = 0, in = 0|
		views[i].channelViews[ii].input.value = in;
	}
	
	createViews { |collapsed|
		var width;
		width = view.asView.bounds.width - 4;
		
		collapsed = collapsed ?? { false!(inputRouter.settings.size) };
		
		RoundView.useWithSkin( skin, {
			views = inputRouter.settings.collect({ |setting, i|
				var el, inWidth;
				el = ();
				el.view = ExpandView( view, 
					width @ ( ( setting.size * (rowHeight + 4) ) + rowHeight + 8 ),
					width @ ( rowHeight + 8 ), 
					collapsed[i] );
				el.view.asView.addFlowLayout;
				inWidth = el.view.asView.bounds.width - 8;
				el.label = StaticText( el.view, inWidth  @ rowHeight )
					.applySkin( skin )
					.string_( inputRouter.outputLabels[i] );
				el.view.asView.decorator.nextLine;
				el.channelViews = setting.collect({ |value, ii| 
					var out = this.createChannelView( el.view, inWidth, i, ii );
					el.view.asView.decorator.nextLine;
					out;
				});
				el.view.hideOutside;
				el;
				
			});
		});
		
		inputRouter.addDependant( this );
	}
	
	createChannelView { |parent, width, i, ii|
		var el;
		el = ();
				
		el.input = PopUpMenu( parent, 80 @ rowHeight )
			.items_( inputRouter.inputLabels )
			.value_( inputRouter.settings[i][ii] )
			.action_({ |pu| inputRouter.setInput( i, ii, pu.value ); })
			.applySkin( skin );

		el.level = SmoothNumberBox( parent, 20 @ rowHeight )
			.step_(1).clipLo_(-100).clipHi_(20).scroll_step_(1)
			.action_({ |sl|  inputRouter.setLevel( i, ii, sl.value ); })
			.value = inputRouter.inputLevels[i][ii];
		el.meter = LevelIndicator( parent, ( width - (20 + 80 + 8)) @ rowHeight )
			.drawsPeak_( true );

		^el;
	}

	
}