// wslib 2007
// made this after student's question why it wasn't there

// NOT TESTED YET

ProgramChangeResponder : MIDIResponder {
	classvar <pcinit = false,<pcr;

	*new { arg function, src, chan, value, install=true;
		^super.new.function_(function)
			.matchEvent_(MIDIEvent(nil, this.fixSrc(src), chan, nil, value))
			.init(install)
	}
	*init {
		if(MIDIClient.initialized.not,{ MIDIIn.connect });
		pcinit = true;
		pcr = [];
		MIDIIn.program = { arg src, chan, val;
			pcr.do({ arg r;
				if(r.matchEvent.match(src, chan, nil, val))
					{ r.value(src,chan,val) };
			});
		}
	}
	value { arg src,chan,val;
		function.value(src,chan,val);
	}
	*initialized { ^pcinit }
	*responders { ^pcr }

	*add { arg resp;
		pcr = pcr.add(resp);
	}
	*remove { arg resp;
		pcr.remove(resp);
	}
}

PCResponder : ProgramChangeResponder {}  // shortcut