// wslib 2009/2010

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

+ Node {

	freeAction_ { |action| // performs action once and then removes it
		this.register;
		this.doOnChange( \n_end, action, true );
		}
		
	startAction_ { |action|
		this.register( false );
		this.doOnChange( \n_go, action, true );
		}
		
	pauseAction_ { |action, oneShot = false| // memory leak if false?
		this.register; 
		this.doOnChange( \n_off, action, oneShot );
		}
	
	runAction_ { |action, oneShot = false|
		this.register;
		this.doOnChange( \n_on, action, oneShot );
		}
	
	}