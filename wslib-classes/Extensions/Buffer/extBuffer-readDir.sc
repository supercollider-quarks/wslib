
	
+ Buffer { // read all files in directory to a dir

	*readMultiple { |names, path, bufnumOffset, server, notify = true|
		path = path.standardizePath;
		^names.collect({ |item, i|
			var bufnum = nil, buffer;
			
			// if bufnumoffset is not nil, the buffers will load to a series
			// of bufnums starting at bufnum. Otherwise the bufnum will be assigned
			// automatically with .bufferAllocator .
			
			if(bufnumOffset.notNil) { bufnum = bufnumOffset + i };
			buffer = Buffer.read(server, path.standardizePath ++ item
				.standardizePath, bufnum: bufnum);
			if(notify) { ("\t" ++ buffer.bufnum ++ " : " ++ item).postln; };
			buffer;
			});
		}

	*readDir { |path, bufnumOffset=0, ext ="wav", server, notify = true, nlevels = inf|
		var names;
		path = path.standardizePath.withTrailingSlash;
		names = path.getPathsInDirectory(ext.removeItems("."), nlevels);
		if(notify) { ("\nread " ++ names.size ++ " files to buffers:").postln;
			path.postln; };
		^Buffer.readMultiple(names, path, bufnumOffset, server, notify);
		}
		
	}
