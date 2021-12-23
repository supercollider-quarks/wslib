+ SimpleMIDIFile {
	
	p { |inst, amp = 0.2, useTempo = true| // amp: amp when velo == 127
		var thisFile;
		inst = ( inst ? 'default' ).asCollection;
		
		// todo: create multi-note events from chords, detect rests
		
		if( useTempo ) {
			if( timeMode == 'seconds' )
				{ thisFile = this }
				{ thisFile = this.copy.timeMode_( 'seconds' ); };
		} {
			if( timeMode == 'ticks' )
				{ thisFile = this }
				{ thisFile = this.copy.timeMode_( 'ticks' ); };
		};
		 ^Ppar(
			({ |tr|
				var sustainEvents, deltaTimes;
				sustainEvents = thisFile.noteSustainEvents( nil, tr );
				if( sustainEvents.size > 0 )
					{ 
					sustainEvents = sustainEvents.flop;
					if( useTempo ) {
						deltaTimes = sustainEvents[1].differentiate;
					} {
						// always use 120BPM
						deltaTimes = (sustainEvents[1] / (division*2)).differentiate;
						sustainEvents[6] = sustainEvents[6] / (division*2);
					};
					Pbind(
						\instrument, inst.wrapAt( tr + 1 ),
						\dur, Pseq( deltaTimes ++ [0], 1 ),
						\chan, Pseq( [0] ++ sustainEvents[3], 1 ),
						\midinote, Pseq( [\rest] ++ sustainEvents[4], 1 ),
						\amp, Pseq( [0] ++ ( sustainEvents[5] / 127 ) * amp, 1 ),
						\sustain, Pseq( [0] ++ sustainEvents[6], 1 )
						)   
					}
					{ nil }
				}!this.tracks).select({ |item| item.notNil })
			);
		}
	
	play { |clock, protoEvent, quant, inst,  amp = 0.2|  
			^this.p( inst, amp ).play( clock, protoEvent, quant );
	}
		
	*fromPattern { |pattern, maxEvents = 1000, maxAmp = 0.2, startTime = 0, 
			inTracks = 2, inTempo = 120, timeSignature|
		var mf;
		mf = this.new( "~/Desktop/fromPattern.mid" ).init1( inTracks, inTempo, timeSignature );
		^mf.fromPattern( pattern, maxEvents, maxAmp, startTime );
	}
		
	fromPattern {	|pattern, maxEvents = 1000, maxAmp = 0.2, startTime = 0|
		var stream, time, count = 0, event, tmode;
		var defaultEvent, instruments = [];
		
		// todo: add key information
		
		time = startTime ? 0;
		if( this.timeMode != \seconds ) 
			{ tmode = this.timeMode; 
			  this.timeMode = \seconds; };
		
		stream = pattern.asStream;
		
		defaultEvent = Event.default;
		defaultEvent[ \velocity ] = { ~amp.value.linlin(0,maxAmp,1,127,\minmax) };
		defaultEvent[ \midinote2 ] = { 
				if( ~freq.isFunction.not )
					{ ~freq.cpsmidi } 
					{ ~midinote.value } 
			};
		
		if( format > 0 )
			{
			defaultEvent[ \track ] = { // auto recognize tracks by instrument
					if( ~instrument.value.isString,
							{ [ ~instrument.value ] },
							{ ~instrument.value.asCollection })
						.collect({ |inst|
							var track;
							track = instruments.indexOf( inst.asSymbol );
							if( track.notNil )
								{ track + 1 }
								{ instruments = instruments.add( inst.asSymbol );
								  track = instruments.size;
								  track;
								};
							 });
				};
			};
		
		// add the notes:
		while { (event = stream.next( defaultEvent )).notNil && 
					{ (count = count + 1) <= maxEvents } } 
			{ event.use({
				if( event.isRest.not ) // not a \rest
					{ 	[	
							event.midinote2,
						 	event.velocity,
						 	time,
						 	event.sustain,
						 	event.upVelo, // addNote copies noteNumber if nil
						 	event.channel ? 0,
						 	event.track ?? {format.min(1)}, // format 0: all in track 0
						 	false // don't sort (yet)
						].multiChannelExpand // allow multi-note events
							.do({ |array| this.addNote( *array ); });
						
				};
				time = time + event.delta;
			});	
		};
			
		// sorting and post-processing
		this.adjustTracks;
		this.sortMIDIEvents;
		this.adjustEndOfTrack;
		
		if( format > 0 )
			{ // add instrument names if found
			(tracks-1).do({ |i|
				if( instruments[i].notNil )
					{ this.setInstName( instruments[i].asString, i+1 ); };
			});
			};
		
		if( tmode.notNil ) { this.timeMode = tmode }; // change back to original
		}


// ------------------------------------------------------------
//// the following methods were kindly added by Jascha Narveson
		
	generatePatternSeqs {	
/*

Returns an array of [note, dur] sequence info for each track, with proper accounting of rests. Ê

Thus, if m is a SimpleMIDIFile, you could do the following:

t = m.generatePatternSeqs; // t is now an arry of [note, dur] info for each track

Pbind( [\midinote, \dur], Pseq(t[1], 1)).play;

Note that the first track in a SimpleMIDIFile often contains no note events if imported from an external midi file (since it's used for metadata), so that the first track of interest is usually the one in index 1 of the getSeqs array.Ê I decided to leave the first blank track in so preserve the mapping from midi track # to getSeqs array #.

Parameters:

	* withVelocity: Instead of a 2-element tuple (as above), returns a 3-element with a velocity value [0.0 - 1.0].  Velocity is 0.0 for rests.
	* padStart: If the MIDI file starts with a gap, inserts a rest into the pattern filling the gap from the start of the file until the first note.
	* totalDurationForPadEnd: In order to create perfect looping patterns, a duration can be specified here.  If there is a gap between the end of the last note event and this duration, a rest will be inserted at the end of the pattern to ensure the pattern is this duration in total.

*/	
		arg withVelocity = false, padStart = false, totalDurationForPadEnd = false;
		var trackSeqs, durationSum, addTrackEventToSeq, addRestEventToSeq;

		// Helper method to add a note to the seq.
		addTrackEventToSeq = {
			arg seq, event;
			if (withVelocity, {
				seq.add([event.note, event.dur, event.vel]);
			}, {
				seq.add([event.note, event.dur]);
			});
		};

		// Helper method to add a rest event to the seq.
		addRestEventToSeq = {
			arg seq, dur;
			if (withVelocity, {
				seq.add([\rest, dur, 0.0]);
			}, {
				seq.add([\rest, dur]);
			});
		};
		
		this.timeMode_('ticks');
		trackSeqs = Array.fill(tracks, {List.new(0)});
		
		this.noteEvents.do({|event| // sort the tracks in to separate Lists, which are stored in ~tracks
			var trackNum = event[0];
			trackSeqs[trackNum].add(event);
		});
		
		trackSeqs = trackSeqs.collect({|track|
			var trackEvents, seq, seqAsDur;
			seq = List.new(0);
			
			trackEvents = track.clump(2).collect({|pair|
				(
					'dur': pair[1][1] - pair[0][1],
					'note': pair[0][4],
					'vel': pair[0][5] / 127.0,
					'startPos': pair[0][1],
					'endPos': pair[1][1]
				)
			});
			
			trackEvents.do({|event, i|
				var diff;
				if (i==0,Ê
					{	
						// If first note in MIDI file is not at beginning of file, add a
						// rest at the beginning of the pattern to fill the empty space.
						if (padStart.and(event.startPos != 0), {
							addRestEventToSeq.value(seq, event.startPos);
						});
						addTrackEventToSeq.value(seq, event);
					},
					{
						diff = event.startPos - trackEvents[i-1].endPos;
						if (diff > 0,
							{
								addRestEventToSeq.value(seq, diff);
								addTrackEventToSeq.value(seq, event);
							},
							{
								addTrackEventToSeq.value(seq, event);
							}
						)
					}
				);
			});
			if (withVelocity, {
				seqAsDur = seq.collect({|e| [e[0], e[1] / division, e[2]]});
			}, {
				seqAsDur = seq.collect({|pair| [pair[0], pair[1] / division]});
			});

			// Appends a rest at the end of the notes list if `totalDurationForPadEnd`
			// is set.
			if (totalDurationForPadEnd != false, {
				// Sums all durations
				durationSum = 0;
				seqAsDur.do({
					arg midiEvent;
					durationSum = durationSum + midiEvent[1];
				});
				// Adds rest to fill remaining time
				if (totalDurationForPadEnd > durationSum, {
					addRestEventToSeq.value(seqAsDur, totalDurationForPadEnd - durationSum);
				});
			});

			seqAsDur;
		});
		
		^trackSeqs;
	}
	
 // better use fromPattern here 
	renderPattern {|pattern, maxEvents = 1000, timeSig = "4/4", numTicks = 1024|
/* This method will accept a pattern and a maximum number of events and turn it in to a SimpleMIDIFile, suitable for writing to disc or using in any other way.

Patterns wishing to involve several tracks should be sure to specify \track and \channel info in the pattern itself. Ê

Currently, only pitch and rhythm and dynamic info is rendered.Ê Pitch may be specified in any of the usual pattern ways, as \freq or \midinote or \degree.Ê The \amp key is used to generate velocity information, with the 0-1 range of amp mapped to the normal midi 0-127.

Example:

~high = Pbind(
	\track, 1, \channel, 0, \degree, Pwhite(-8, 8), \octave, 6, \scale, #[0,2,3,5,7,8,10],Ê
	\dur, Prand( [0.25, 0.5, 0.75, 1], inf)
);

~low = Pbind(\track, 2, \channel, 0, \degree, Pwhite(-3, 8), \octave, 3, \scale, #[0,2,3,5,7,8,10], \dur, 1);

~pat = Ppar([~high, ~low]);
	
~file = SimpleMIDIFile("~/Desktop/nu.mid".standardizePath);	

~file.renderPattern(~pat, 200);

~file.write;
*/
		var times, stream, tracklists;
		stream = pattern.asStream;
		times = Array.fill(16, {0});
		tracklists = Array.fill(16, {List.new(0)});
		this.init1(16, 120, timeSig.asString);
		this.division = numTicks;
		maxEvents.do({
			var event;
			event = stream.next(Event.default);
			if (event.isNil.not, {	
				event.use({
					var temp;
					~finish.value;
					if (~track.isNil,Ê
						{~track = 1},
						{~track = ~track.asInteger}
					);
					if (~channel.isNil, {~channel = 0});
					if (event.isRest.not,
						{
							if (~velo.isNil, {~velo = ~amp.value.linlin(0, 1, 0, 127).round(1)});
							if (~upVelo.isNil, {~upVelo = ~velo});
							tracklists[~track].add(
								(
									noteNumber: ~freq.value.cpsmidi.round(0.01),// noteNumber
									velo: ~velo,								// velo
									startTime: (times[~track] * this.division).round(64),	// startTime
									dur: (~dur * this.division).round(64),	// dur
									upVelo: ~upVelo,							// upVelo
									channel: ~channel,							// channel
									track: ~track,								// track
									sort: true									// sort
								);
							);
						}
					);
					times[~track] = times[~track] + ~dur;
				});
			});
		});
		tracklists.do({|track|Ê
			track.do({|info|
				this.addNote(
					noteNumber: info[\noteNumber],
					velo: info[\velo],	
					startTime: info[\startTime],
					dur: info[\dur],
					upVelo: info[\upVelo],
					channel: info[\channel],
					track: info[\track],
					sort: info[\sort]		
				);
			});
		});
		16.do({|i| this.adjustEndOfTrack(i)});
	}

	
	}
