// wslib 2010
//
// the lang-side counterpart of SendTrig and SendReply
//

/*
// example 1:

x = { SendTrig.kr( Impulse.kr(1), 10, WhiteNoise.kr ) }.play;

ReceiveTrig( x, _.postln );

x.free; // automatic removal

// example 2:

r = ReceiveTrig( s, { |val, time, responder, msg| msg.postln } ); // receive any trig from s

x = { SendTrig.kr( Impulse.kr(1), 10, WhiteNoise.kr ) }.play;
y = { SendTrig.kr( Impulse.kr(1), 11, WhiteNoise.kr ) }.play;

r.id = 10; // only from x

r.remove; // stop receiving (or cmd-.)


// example 3: as a method for Node or Synth

y = { SendTrig.kr( Impulse.kr(1), 10, WhiteNoise.kr ) }.play;

y.onTrig_( _.postln ); // adds a ReceiveTrig to the Synth

y.free; // also removes the ReceiveTrig


// example 4: ReceiveReply

y = { SendReply.kr( Impulse.kr(1), '/noise', WhiteNoise.kr(1.dup) ) }.play;

y.onReply_( _.postln, '/noise' ); // adds a ReceiveReply to the Synth

y.free; // also removes the ReceiveReply


// example 5: even shorter

y = { SendTrig.kr( Impulse.kr(1), 10, WhiteNoise.kr(1) ) }.play.onTrig_( _.postln );

y.free;

*/

ReceiveReply {
	classvar <all;

	var <source;
	var <oscFunc, <>action, <>id, <value = 0, <>removeAtReceive = false;
	var <endOSCFunc;
	var <cmdName;

	*defaultCmdName { ^'/reply' }

	*initClass { CmdPeriod.add( this ); }

	*new { |source, action, cmdName, id|
		^super.new.init( source, cmdName )
			.id_( id )
			.action_( action ? { |value, time, responder, msg| value.postln; } )
			.addToAll
		}

	init { |inSource, inCmdName|
		source = inSource;
		cmdName = inCmdName ? this.class.defaultCmdName;
		this.startResponders;
	}

	startResponders {
		var addr;

		case { (source.class == Synth) or: { source.isNumber } } // Synth or nodeID
			{	
				addr = NetAddr( source.server.addr.hostname, source.server.addr.port );
				oscFunc = OSCFunc({ arg msg, time;
					if( removeAtReceive == true ) { this.free };
					this.doAction( time, msg );
				}, cmdName, addr, argTemplate: [ source.nodeID ]);
	
				endOSCFunc = OSCFunc({ arg msg, time;
					 this.free;
				}, '/n_end', addr, argTemplate: [ source.nodeID ]).oneShot;
			}
			{ source.respondsTo( \addr ) } // a Server
			{ 
				addr = NetAddr( source.addr.hostname, source.addr.port );
				oscFunc = OSCFunc({ arg msg, time;
					if( removeAtReceive == true ) { this.free };
					this.doAction( time, msg );
				}, cmdName, addr );
			}
			{ source.class == NetAddr } // a NetAddr
			{ 
				oscFunc = OSCFunc({ arg msg, time;
					if( removeAtReceive == true ) { this.free };
					this.doAction( time, msg ); 	
				}, cmdName, source );
			}
			{ 
				oscFunc = OSCFunc({ arg msg, time;
					if( removeAtReceive == true ) { this.free };
					this.doAction( time, msg );
				}, cmdName );
			};
		}

	doAction { |time, msg|
		if( id.isNil or: { msg[2] == id } )
			{ value =  msg[3..];
			if( value.size == 1 ) { value = value[0]; };
			action.value( value, time, this, msg );
		};
	}

	addToAll { all = all.asCollection ++ [ this ] }
	
	free { oscFunc.free; endOSCFunc.free; all.remove( this ); }
	*free { all.do({ |obj| obj.free }); all = []; }

	remove { this.free } // shortcut for backwards compatibility
	*remove { this.free }
	
	*cmdPeriod { this.free; }

	oneShot { removeAtReceive = true; }
	*oneShot { |source, action, cmdName, id| ^this.new( source, action, cmdName, id ).oneShot; }

	}

ReceiveTrig : ReceiveReply {

	*defaultCmdName { ^'/tr' }

	*new { |source, action, id|
		^super.new( source, action, this.defaultCmdName, id );
		}

	// more optimized; SendTrig doesn't do multichannel expansion
	doAction { |time, msg|
		if( id.isNil or: { msg[2] == id } ) { 
			value = msg[3];
			action.value( value, time, this, msg );
		};
	}

}

// Node support

+ Node {

	onReply_ { |action, cmdName, id|
			var rt;
			if( ( rt = this.onReply( cmdName ) ).notNil )
				{ rt.id = id; rt.action = action; }
				{ ReceiveReply( this, action, cmdName, id ); };
	}

	onReply { |cmdName|
		cmdName = cmdName ? '/reply';
		^ReceiveReply.all.detect({ |item|
			item.source == this && { item.cmdName == cmdName }; });
		}

	onTrig_ { |action, id|
			var rt;
			if( ( rt = this.onTrig ).notNil )
				{ rt.id = id; rt.action = action; }
				{ ReceiveTrig( this, action, id ); };
	}

	onTrig { ^ReceiveTrig.all.detect({ |item|
		 item.source == this && { item.cmdName == '/tr' }; });
		 }

}