// wslib 2009
// shortcut for starting a Synth using bundled messaging

+ Synth {
	*sched { arg time = 0.2, defName, args, target, addAction=\addToHead;
		var synth, server, addNum, inTarget;
		inTarget = target.asTarget;
		server = inTarget.server;
		addNum = addActions[addAction];
		synth = this.basicNew(defName, server);

		if((addNum < 2), { synth.group = inTarget; }, { synth.group = inTarget.group; });

		server.sendBundle( time, [9, //"s_new"
			defName, synth.nodeID, addNum, inTarget.nodeID ] ++ args.asOSCArgArray );
		^synth
		}
	
	*newMsg { arg defName, args, target, addAction=\addToHead;
		var synth, server, addNum, inTarget;
		inTarget = target.asTarget;
		server = inTarget.server;
		addNum = addActions[addAction];
		synth = this.basicNew(defName, server);

		if((addNum < 2), { synth.group = inTarget; }, { synth.group = inTarget.group; });

		^[9, //"s_new"
			defName, synth.nodeID, addNum, inTarget.nodeID ] ++ args.asOSCArgArray;
		
		}
	
	}