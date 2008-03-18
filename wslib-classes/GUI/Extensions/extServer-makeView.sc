
+ Server {
	makeView { arg w, useRoundButton = true;
		var active, booter, killer, makeDefault, running, booting, stopped;
		var recorder, scoper;
		var countsViews, ctlr;
		var dumping=false;
		var infoString, oldOnClose;
		
		if (window.notNil, { ^window.front });
		
		if(w.isNil,{
			w = window = GUI.window.new(name.asString ++ " server", 
						Rect(10, named.values.indexOf(this) * 120 + 10, 300, 92));
			w.view.decorator = FlowLayout(w.view.bounds);
		});
		
		if(isLocal,{
			if( useRoundButton )
				{ booter = RoundButton(w, Rect(0,0, 24, 24)).canFocus_( false );
				  booter.states = [[ \power, Color.black, Color.clear],
						   		[ \power, Color.red, Color.clear]];
			 	}
				{ booter = GUI.button.new( w, Rect(0,0,24,24));
				 booter.states = [[ "B"],[ "Q" ]]; };
						
			booter.action = { arg view; 
				if(view.value == 1, {
					booting.value;
					this.boot;
				});
				if(view.value == 0,{
					this.quit;
				});
			};
			booter.value = serverRunning.binaryValue;
			/*
			killer = SCButton(w, Rect(0,0, 24, 24));
			killer.states = [["K", Color.black, Color.clear]];
			
			killer.action = { Server.killAll };
			*/	
		});
		
		active = GUI.staticText.new(w, Rect(0,0, 78, 25));
		active.string = this.name.asString;
		active.align = \center;
		active.font = GUI.font.new("Helvetica-Bold", 12);
		active.background = Color.black;
		if(serverRunning,running,stopped);		

		w.view.keyDownAction = { arg ascii, char;
			var startDump, stopDump, stillRunning;
			
			case 
			{char === $n} { this.queryAllNodes }
			{char === $ } { if(serverRunning.not) { this.boot } }
			{char === $s and: {this.inProcess}} { this.scope }
			{char == $d} {
				if(this.isLocal or: { this.inProcess }) {
					stillRunning = {
						SystemClock.sched(0.2, { this.stopAliveThread });
					};
					startDump = { 
						this.dumpOSC(1);
						this.stopAliveThread;
						dumping = true;
						CmdPeriod.add(stillRunning);
					};
					stopDump = {
						this.dumpOSC(0);
						this.startAliveThread;
						dumping = false;
						CmdPeriod.remove(stillRunning);
					};
					if(dumping, stopDump, startDump)
				} {
					"cannot dump a remote server's messages".inform
				}
			
			};
		};
		
		if (isLocal, {
			
			running = {
				active.stringColor_(Color.red);
				booter.value = 1;
				//recorder.enabled = true;
			};
			stopped = {
				active.stringColor_(Color.grey(0.3));
				booter.value = 0;
				//recorder.setProperty(\value,0);
				//recorder.enabled = false;

			};
			booting = {
				active.stringColor_(Color.yellow(0.7));
				//booter.setProperty(\value,0);
			};
			
			oldOnClose = w.onClose.copy;
			w.onClose = {
			
				//OSCresponder.removeAddr(addr);
				//this.stopAliveThread;
				//this.quit;
				
				oldOnClose.value;
				window = nil;
				ctlr.remove;
			};
		},{	
			running = {
				active.background = Color.red;
				//recorder.enabled = true;
			};
			stopped = {
				active.background = Color.black;
				//recorder.setProperty(\value,0);
				//recorder.enabled = false;

			};
			booting = {
				active.background = Color.yellow;
			};
			
			oldOnClose = w.onClose.copy;
			w.onClose = {
				// but do not remove other responders
				
				oldOnClose.value;
				this.stopAliveThread;
				ctlr.remove;
			};
		});
		if(serverRunning,running,stopped);
			
		//w.view.decorator;
		
		infoString = GUI.staticText.new(w, Rect(0,0, 170, 25));
		infoString.string = "Port: %, CPU: %/%\nSynths/Defs: %/%"
			.format( addr.port, "?", "?", "?", "?" );
		infoString.font_( GUI.font.new( "Monaco", 9 ) );
		
		w.view.decorator.nextLine;
		
		
		w.front;

		ctlr = SimpleController(this)
			.put(\serverRunning, {	if(serverRunning,running,stopped) })
			.put(\counts,{
				infoString.string =
					"Port: %, CPU: %/%\nSynths/Defs: %/%"
						.format( addr.port,
							avgCPU.round(0.1),  peakCPU.round(0.1), 
							numSynths, numSynthDefs );
			})
			.put(\cmdPeriod,{
				//recorder.setProperty(\value,0);
			});	
		this.startAliveThread;
	}
	
/////////////////////////////////////////////
	
	makeView2 { arg w, useRoundButton = true; // temp
		var active, booter, killer, makeDefault, running, booting, stopped;
		var recorder, scoper;
		var countsViews, ctlr;
		var dumping=false;
		var infoString, oldOnClose;
		
		if (window.notNil, { ^window.front });
		
		if(w.isNil,{
			w = window = GUI.window.new(name.asString ++ " server", 
						Rect(10, named.values.indexOf(this) * 120 + 10, 300, 92));
			w.view.decorator = FlowLayout(w.view.bounds);
		});
		
		if(isLocal,{
			if( useRoundButton )
				{ booter = RoundButton(w, Rect(0,0, 16, 16)).canFocus_( false );
				  booter.states = [[ \power, Color.black, Color.clear],
						   		[ \power, Color.red, Color.clear]];
			 	}
				{ booter = GUI.button.new( w, Rect(0,0,16,16));
				 booter.states = [[ "B"],[ "Q" ]]; };
						
			booter.action = { arg view; 
				if(view.value == 1, {
					booting.value;
					this.boot;
				});
				if(view.value == 0,{
					this.quit;
				});
			};
			booter.value = serverRunning.binaryValue;
			/*
			killer = SCButton(w, Rect(0,0, 24, 24));
			killer.states = [["K", Color.black, Color.clear]];
			
			killer.action = { Server.killAll };
			*/	
		});
		
		active = GUI.staticText.new(w, Rect(0,0, 60, 16));
		active.string = this.name.asString;
		active.align = \center;
		active.font = GUI.font.new("Helvetica-Bold", 12);
		active.background = Color.white;
		if(serverRunning,running,stopped);		

		w.view.keyDownAction = { arg ascii, char;
			var startDump, stopDump, stillRunning;
			
			case 
			{char === $n} { this.queryAllNodes }
			{char === $ } { if(serverRunning.not) { this.boot } }
			{char === $s and: {this.inProcess}} { this.scope }
			{char == $d} {
				if(this.isLocal or: { this.inProcess }) {
					stillRunning = {
						SystemClock.sched(0.2, { this.stopAliveThread });
					};
					startDump = { 
						this.dumpOSC(1);
						this.stopAliveThread;
						dumping = true;
						CmdPeriod.add(stillRunning);
					};
					stopDump = {
						this.dumpOSC(0);
						this.startAliveThread;
						dumping = false;
						CmdPeriod.remove(stillRunning);
					};
					if(dumping, stopDump, startDump)
				} {
					"cannot dump a remote server's messages".inform
				}
			
			};
		};
		
		if (isLocal, {
			
			running = {
				active.string_( "running" );
				active.stringColor_(Color.green);
				booter.value = 1;
				//recorder.enabled = true;
			};
			stopped = {
				active.string_( "inactive" );
				active.stringColor_(Color.grey(0.3));
				booter.value = 0;
				//recorder.setProperty(\value,0);
				//recorder.enabled = false;

			};
			booting = {
				active.string_( "booting" );
				active.stringColor_(Color.yellow(0.7));
				//booter.setProperty(\value,0);
			};
			
			oldOnClose = w.onClose.copy;
			w.onClose = {
			
				//OSCresponder.removeAddr(addr);
				//this.stopAliveThread;
				//this.quit;
				
				oldOnClose.value;
				window = nil;
				ctlr.remove;
			};
		},{	
			running = {
				active.string_( "running" );
				active.background = Color.green;
				//recorder.enabled = true;
			};
			stopped = {
				active.string_( "inactive" );
				active.background = Color.gray;
				//recorder.setProperty(\value,0);
				//recorder.enabled = false;

			};
			booting = {
				active.string_( "booting" );
				active.background = Color.yellow;
			};
			
			oldOnClose = w.onClose.copy;
			w.onClose = {
				// but do not remove other responders
				
				oldOnClose.value;
				this.stopAliveThread;
				ctlr.remove;
			};
		});
		if(serverRunning,running,stopped);
			
		//w.view.decorator;
		
		infoString = GUI.staticText.new(w, Rect(0,0, 170, 16));
		infoString.string = "%, CPU: %/%S/SD: %/%"
			.format( addr.port, "?", "?", "?", "?" );
		infoString.font_( GUI.font.new( "Monaco", 9 ) );
		
		w.view.decorator.nextLine;
		
		
		w.front;

		ctlr = SimpleController(this)
			.put(\serverRunning, {	if(serverRunning,running,stopped) })
			.put(\counts,{
				infoString.string =
					"%, CPU: %/%S/SD: %/%"
						.format( addr.port,
							avgCPU.round(0.1),  peakCPU.round(0.1), 
							numSynths, numSynthDefs );
			})
			.put(\cmdPeriod,{
				//recorder.setProperty(\value,0);
			});	
		this.startAliveThread;
	}

}
