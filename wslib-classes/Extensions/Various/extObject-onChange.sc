+ Object {
	
	doOnChange { |what, action, oneShot = true|
		var controllerClass, controller;
		
		if( oneShot ) 
			{ controllerClass = OneShotController } // from wslib
			{ controllerClass = SimpleController };
			
		controller = this.dependants.detect({ |item| item.class == controllerClass }) ?? 
			{ controllerClass.new( this ); };
		
		controller.put( what, action );
		}
	}

+ Synth {

	freeAction_ { |action| // performs action once and then removes it
		this.register;
		this.doOnChange( \n_end, action, true );
		}
		
	startAction_ { |action| // only if not running yet
		this.register( false );
		this.doOnChange( \n_go, action, true );
		}
		
	pauseAction_ { |action, oneShot = false|
		this.register;
		this.doOnChange( \n_off, action, oneShot );
		}
	
	runAction_ { |action, oneShot = false|
		this.register;
		this.doOnChange( \n_on, action, oneShot );
		}
	
	}