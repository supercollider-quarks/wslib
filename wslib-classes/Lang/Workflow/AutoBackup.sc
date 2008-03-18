AutoBackup {
	
	classvar <currentDocuments, <>backupFolder =  "~/Documents/SuperCollider/_Backup/";
	classvar <active = false;
	
	// keeps a backup of all opened and edited files 
	// this will save your every move and keep them when sc freezes or unexpectedly quits
	// - deletes file after closing document
	// - duplicate filenames not supported
	
	*initClass { currentDocuments = [] }
	
	*start {
		if( backupFolder.isFolder.not )
			{ backupFolder.makeDir };
			  
		Document.globalKeyUpAction = { |doc|
			var file;
			file = File((backupFolder ++ doc.title.basename ++ ".sc").standardizePath, "w");
			file.write( doc.string );
			file.close;
			if( currentDocuments.includes( doc ).not )
				{ currentDocuments = currentDocuments.add( doc ); };
			 
			doc.onClose = doc.onClose ? { 
				(backupFolder ++ doc.title.basename ++ ".sc").removeFile( false, false, true );
				currentDocuments.remove( doc );
				};
			}; 
		active = true;
		
		"AutoBackup is active".postln;
		
		} 
		
	
	*stop { |deleteAll = true|
		Document.globalKeyUpAction = nil;
		if( deleteAll ) { this.deleteBackups };
		active = false;
		
		"AutoBackup has stopped".postln;
		
		}
	
	*deleteBackups {
		currentDocuments.do({ |doc|
			(backupFolder ++ doc.title.basename ++ ".sc").removeFile( false, false, true );
			});
		}
	
	*cleanBackupFolder {
		(backupFolder.standardizePath ++ "/*.sc").pathMatch.do({ |path|
			path.removeFile( false, false, true ); });
		}
		
	*show { backupFolder.openInFinder }
	
	}
			





