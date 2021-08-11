+ Server {
	*freeAllRemote  { |includeLocal = true|
		if( includeLocal )
			{ Server.all.do( _.freeAll ) }
			{ Server.all.do({ |server|
				if( server.isLocal.not )
					{ server.freeAll; }
				})
			};
		}
	}