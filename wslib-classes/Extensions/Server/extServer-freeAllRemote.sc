+ Server {
	*freeAllRemote  { |includeLocal = true|
		if( includeLocal )
			{ set.do( _.freeAll ) }
			{ set.do({ |server|
				if( server.isLocal.not )
					{ server.freeAll; }
				})
			};
		}
	}