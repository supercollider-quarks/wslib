InputRouter {

	classvar <>defaultPath = "~/Documents/SuperCollider";
	classvar <>all;

	var <server;
	var <name, <outputLabels, <inputLabels;
	var <synths;
	var <>path;
	var <inOffset = 0, <outOffset = 0, <private = true;
	var <meter = true, <isRunning = false;
	
	var <settings, <inputLevels;
	
	var <meterLevels, <updateFreq = 10;
	
	var serverQuitFunc;
	
	var <>surviveCmdPeriod = true;
	
	*new { |server, name, outputLabels|
		^super.newCopyArgs( server, name, outputLabels ).init.addToAll;
	}
	
	addToAll { all = all.add( this ); }
	
	*unique { |server, name, outputLabels|
		var out;
		server = server ? Server.default ? Server.local;
		name = (name ? server.name).asSymbol;
		out = all.detect({ |item| item.name == name });
		^out ?? { this.new( server, name, outputLabels ) };
	}
	
	*remove { |name|
		var out;
		if( name.isKindOf( Server ) ) { name = name.name };
		name = name ?? { (Server.default ? Server.local).name };
		name = name.asSymbol;
		out = all.detect({ |item| item.name == name });
		if( out.notNil ) { out.stop; all.remove( out ); };
	}
	
	*removeAll { 
		all.do({ |item| item.stop; });
		all = nil;
	}
	
	gui { |parent, bounds| ^InputRouterGUI( this, parent, bounds ) }
	
	init { 
		server = server ? Server.default ? Server.local;
		name = (name ? server.name).asSymbol;
		if( outputLabels.isNumber )	{ outputLabels = ("input " ++ _ )!outputLabels; };
		outputLabels = (outputLabels ? ["input 0"]).collect(_.asString);
		inputLabels = server.options.numInputBusChannels.collect({ |i|
			"AudioIn %".format( i+1 );
		});
		settings = [_]!outputLabels.size;
		inputLevels = settings.collect( _.collect( 0 ) );
		path = path ?? 
			{ thisProcess.nowExecutingPath !? { thisProcess.nowExecutingPath.dirname; }; };
		this.readSettings; // replace with settings from file if available
		serverQuitFunc = {
			synths = nil; 
		 	isRunning = false;
		 	this.changed( \stop );
		 	CmdPeriod.remove( this ); 
		};
	}
	
	start { // calls stop internally -- also used for restart
		this.stop;
		this.prStart;	
		this.changed( \start );
		CmdPeriod.add( this );
	}
	
	stop { this.freeSynths; 
		  isRunning = false;
		  this.changed( \stop ); 
		  CmdPeriod.remove( this ); 
		  ServerQuit.remove( serverQuitFunc, server );
	}
	
	prStart {
		server.waitForBoot({	
			this.synthDef.send(server);
			server.sync;
			isRunning = true;
			this.startSynths;
			this.startResponders;
			ServerQuit.add( serverQuitFunc, server );
			});
	}
	
	cmdPeriod { if( surviveCmdPeriod ) { 
			{ this.prStart }.defer(0.01) 
		} { 
			synths = nil; 
		 	isRunning = false;
		 	this.changed( \stop );
		 	CmdPeriod.remove( this ); 
		 	ServerQuit.remove( serverQuitFunc, server );
		};
	}
	
	startSynths {
			synths = settings.collect({ |item, i|
				item.collect({ |iitem, ii|
				 	Synth.before( server, this.synthDefName, 
				 		[	// per channel:
				 			\in, iitem,
				 			\out, i + outOffset,  
				 			\amp, (1/item.size),
				 			\gain, (inputLevels[i][ii] ? 0).dbamp,
				 			
				 			// global:
				 			\meter, meter.binaryValue,
				 			\updateFreq, updateFreq,
				 			\inOffset, inOffset, 
				 			\outOffset, outOffset, 
				 			\private, private.binaryValue,
				 			
				 		 	] ); 
				});
			});
			
			meterLevels = synths.collect({ |item|
					item.collect({ |synth|
						[0,0]; // level, peak
					});
			});
	}
	
	freeSynths { synths !? { synths.flatten(1).do( _.free ); synths = nil; }; }
	
	startResponders { // only after synths have started
		synths.do({ |item, output|
			item.do({ |synth, index|
				synth.onReply_({ |value|
					var val, peak;
					#val, peak = value;
					val = (val.max(0.0) * (updateFreq/server.sampleRate)).sqrt;
					meterLevels[ output ][ index ] = [val, peak].ampdb;
					this.changed( \meterLevel, output, index, 
						meterLevels[ output ][ index ] ); // enable easy mvc
				}, "/%_meterLevel".format( this.synthDefName ).asSymbol );
			});	
		});
	}
	
	setSynth { |output = 0, index = 0, key = \gain, value = 0|
		synths !? { synths[output][index].set( key, value ); };
	}
	
	setAllSynths { |key = \private, val = 1|
		synths !? { synths.do({ |sn| sn.do( _.set( key, val ) ) }); };
	}
	
	
	setLevel { |output = 0, index = 0, dB = 0|
		this.setSynth( output, index, \gain, dB.dbamp );
		inputLevels[ output ][ index ] = dB;
		this.changed( \level, output, index, dB );
	}
	
	setInput { |output = 0, index = 0, in = 0|
		this.setSynth( output, index, \in, in + inOffset );
		settings[ output ][ index ] = in;
		this.changed( \input, output, index, in );
	}

	meter_ { |bool = true|
		meter = bool.booleanValue;
		this.setAllSynths( \meter, meter.binaryValue );
		this.changed( \meter, meter );
	}
	
	settings_ { |newSettings|
		
		if( newSettings.size < outputLabels.size ) { 
			newSettings = newSettings ++ settings[ newSettings.size-1.. ] 
		};
		
		if( newSettings.size > outputLabels.size ) { 
			newSettings = newSettings[ ..outputLabels.size-1 ]; 
		};
		
		newSettings = newSettings.collect(_.asCollection);
			
		settings = newSettings;
		
		inputLevels = settings.collect({ |item, i|
			item.collect({ |iitem, ii|
				inputLevels[i][ii] ? 0;
			});
		});
		
		this.changed( \settings, settings );
		if( isRunning ) { this.start; }
	}
	
	addInput { |output = 0, in, level = 0|
		settings[ output ] = settings[ output ].add( in ? ((settings[ output ].last ? -1) + 1) );
		inputLevels[ output ] = inputLevels[ output ].add( level );
		this.settings = settings;
	}
	
	removeInput { |output = 0, i = 0|
		if( settings[ output ].size >= (i+1) ) { 
			settings[ output ].removeAt( i );
			inputLevels[ output ].removeAt( i );
			this.settings = settings;
		} { 
			"%:removeInput : index out of range".warn;
		};
	}
		
	private_ { |bool = true|
		private = bool.booleanValue;
		this.setAllSynths( \private, private.binaryValue );
		this.changed( \private, private );
	}
	
	inOffset_ { |val = 0|
		inOffset = val;
		this.setAllSynths( \inOffset, inOffset );
		this.changed( \inOffset, inOffset );
	}
	
	outOffset_ { |val = 0|
		outOffset = val;
		this.setAllSynths( \outOffset, outOffset );
		this.changed( \outOffset, outOffset );
	}
	
	synthDefName { ^this.class.name.asString }
	
	synthDef {		 
		^SynthDef( this.synthDefName, { |in = 0, out = 0, amp = 1, gain = 1, 
				private = 1, outOffset = 0, inOffset = 0, updateFreq = 10, meter = 1|
			var input, imp;
			input = SoundIn.ar( in + inOffset ) * amp * gain;
			
			imp = Impulse.ar(updateFreq) * meter;
			
			SendReply.ar(imp, "/%_meterLevel".format( this.synthDefName ).asSymbol,
				[ 	RunningSum.ar(input.squared, SampleRate.ir / updateFreq ),
					Peak.ar(input, Delay1.ar(imp)).lag(0, 3)
				] );
			
			// to private bus if private == 1
			out = out + ( private * (NumInputBuses.ir + NumOutputBuses.ir) ); 
			Out.ar( out + outOffset, input );
			});
		}
	
	settingsPath { ^( path ?? { 
			(thisProcess.nowExecutingPath !? { thisProcess.nowExecutingPath.dirname; }) ?
			defaultPath } ) +/+ 
		"%_%_prefs.txt".format( name, this.class.name );
	}


	// read / write
	
	readSettings {		
		var readPath;
		readPath = this.settingsPath;	
		if( File.exists( readPath ) )
			{ "reading file %\n".postf( readPath );
			File.use( readPath,
				"r",
				{ |f| var evt;
					evt = f.readAllString.interpret;
					settings = evt[ \settings ];
					inputLabels = evt[ \inputLabels ];
					inputLevels = evt[ \inputLevels ] ??  
						{ settings.collect( _.collect( 0 ) ) };
					this.settings = settings;
			});
		};	
	}
	
	writeSettings {
		path = path ?? 
			{ thisProcess.nowExecutingPath !? { thisProcess.nowExecutingPath.dirname; }; };
		path.makeDir;
		File.use( this.settingsPath,
		"w",
		{ |f|
			f.putString( ( 
					settings: settings, 
					inputLabels: inputLabels,
					inputLevels: inputLevels
					 ).asCompileString );
		});
	}
	
}