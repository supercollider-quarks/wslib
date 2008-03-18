ReceiveTrig {
	classvar <all;
	
	var <responder, <>id, <>action, <value = 0, <>removeAtReceive = false;
	
	*initClass { CmdPeriod.add( this ); }
	
	*new { |source, id = 0, action|
		^super.new.init( source )
			.id_( id )
			.action_( action ? 	
				{ |value, time, responder, msg| value.postln; } )
			.addToAll
		}
		
	init { |inSource, inID|
		case { inSource.class == Synth }
			{ responder = OSCresponderNode( inSource.server.addr, '/tr', 
				{ arg time, responder, msg;
					if( ( msg[2] == id ) && ( msg[1] == inSource.nodeID ) )
						{
						action.value( msg[3], time, responder, msg );
						value =  msg[3];
						if( removeAtReceive ) { this.remove };
						}
					}).add;
			}
			{ inSource.respondsTo( \addr ) }
			{ responder = OSCresponderNode(inSource.addr, '/tr', { arg time, responder, msg;
				if( msg[2] == id )
					{ action.value( msg[3], time, responder, msg );
					  value = msg[3];
					  if( removeAtReceive ) { this.remove };
					}
				}).add;
			}
			{ true }
			{ responder = OSCresponderNode( Server.default.addr, '/tr', 
				{ arg time, responder, msg;
					if( ( msg[2] == id ) )
						{
						action.value( msg[3], time, responder, msg );
						value =  msg[3];
						if( removeAtReceive ) { this.remove };
						}
					}).add;
			};
		}
		
	addToAll { all = all.asCollection ++ [ this ] }
	
	remove { responder.remove; all.remove( this ); }
	*remove { all.do({ |obj| obj.responder.remove }); all = []; }
	
	*cmdPeriod { this.remove; }
	
	oneShot { removeAtReceive = true;  }
	*oneShot { |source, id = 0, action| ^this.new( source, id, action ).oneShot; }
	
	}