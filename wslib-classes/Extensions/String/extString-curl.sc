+ String {
	curlMsg { |path|
		if( path.notNil )
			{ ^"curl % -o %".format( this.quote, path.quote ) }
			{  ^"curl %".format( this.quote ) };
		}
	
	curl { |path, action, postOutput = true|
		if( path.notNil )
			{ ^this.curlMsg.unixCmd( action, postOutput ) }
			{ ^this.unixCmdGetStdOut }; // DOESNT WORK YET
		}

	}