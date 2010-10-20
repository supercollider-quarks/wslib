// wslib 2010
//
// the lang-side counterpart of SendTrig
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

y.onTrig_( _.postln ); // adds a ReceiveTrig

y.free; // also removes the ReceiveTrig

*/

ReceiveTrig {
	classvar <all;
	
	var <source;
	var <responder, <>action, <>id, <value = 0, <>removeAtReceive = false;
	var <endResponder;
	
	*initClass { CmdPeriod.add( this ); }
	
	*new { |source, action, id|
		^super.new.init( source )
			.id_( id )
			.action_( action ? { |value, time, responder, msg| value.postln; } )
			.addToAll
		}
		
	init { |inSource|
		source = inSource;
		case { (inSource.class == Synth) or: { inSource.isNumber } } // Synth or nodeID
			{ responder = OSCresponderNode( inSource.server.addr, '/tr', 
				{ arg time, responder, msg;
					if( msg[1] == inSource.nodeID ) { this.doAction( time, responder, msg ) }
				}).add;
					
			 endResponder = OSCresponderNode( inSource.server.addr, '/n_end', // remove on end
			 	 { arg time, responder, msg;
				 	 if( msg[1] == inSource.nodeID ) { this.remove };
			 	 }).add; 
			}
			{ inSource.respondsTo( \addr ) } // a Server
			{ responder = OSCresponderNode(inSource.addr, '/tr', 
				{ arg time, responder, msg; 
					this.doAction( time, responder, msg ) 				}).add;
			}
			{ inSource.class == NetAddr } // a NetAddr
			{ responder = OSCresponderNode(inSource, '/tr', 
				{ arg time, responder, msg; 
					this.doAction( time, responder, msg ) 				}).add;
			}
			{ true } // anything else (including nil)
			{ responder = OSCresponderNode( nil, '/tr', 
				{ arg time, responder, msg; 
					this.doAction( time, responder, msg ) 
				}).add;
			};
		}
		
	doAction { |time, responder, msg|
		if( id.isNil or: { msg[2] == id } )
			{ action.value( msg[3], time, responder, msg );
			  value =  msg[3];
			  if( removeAtReceive ) { this.remove };
			};					
	}
		
	addToAll { all = all.asCollection ++ [ this ] }
	
	remove { responder.remove; endResponder.remove; all.remove( this ); }
	*remove { all.do({ |obj| obj.remove }); all = []; }
	
	*cmdPeriod { this.remove; }
	
	oneShot { removeAtReceive = true; }
	*oneShot { |source, action, id| ^this.new( source, action, id ).oneShot; }
	
	}
	
// Node support

+ Node {
	
	onTrig_ { |action, id|
			var rt; 
			if( ( rt = this.onTrig ).notNil )
				{ rt.id = id; rt.action = action; }
				{ ReceiveTrig( this, action, id ); };
	}
	
	onTrig { ^ReceiveTrig.all.detect({ |item| item.source == this }); }
	
}