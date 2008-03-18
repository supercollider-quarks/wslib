// A simple window for controlling the MIDIClient
// will init client upon creation and give control
// over which device input to connect to a predefined port
//
// wslib 2005

MIDIWindow {
	
	classvar <currentDevice = 0;
	classvar <inIsOn = nil;
	classvar <>toPort = 0;
	classvar <window = nil;
	classvar <sourceNames = nil;
	
	
	*new { arg connect = false, current; // connect can be array or single
		var clientConnectInButton, portPopUp, clientPopUp;
		var nSources, noMidi = false;
		var getSourceNames;
		// only one port so far
				
		/* if(MIDIClient.initialized.not)
			{ MIDIClient.init; }; */
			
				
	if(window.isNil) {
		MIDIClient.init;
		
		nSources = MIDIClient.sources.size;
		if(nSources == 0) { noMidi=true };
		if(noMidi)
			{currentDevice = 0;
			if(connect.size == 0) {connect = connect.dup(1); };
			inIsOn = inIsOn ? [false]; } {
			currentDevice = current ? currentDevice;
			if(connect.size == 0) {connect = connect.dup(nSources); };
			inIsOn = { |i|( connect[i] ? false) or: ((inIsOn ? [])[i] ? false); }!nSources;
			};
		
		getSourceNames = {
			if(noMidi) { sourceNames = ["(no sources available" /*)*/]; }
				{ sourceNames =  MIDIClient.sources.collect({|item, i| 
				if(inIsOn[i])
					{(item.device + item.name).replaceBrackets + ":" + toPort }
					{(item.device + item.name).replaceBrackets }
				}); };
			sourceNames;
		};
		
		getSourceNames.value;
		
		//sourceNames.postln;
		
		inIsOn.do({ |item, i|
			if(item) {MIDIIn.connect(toPort, i) } });

		window = SCWindow("MIDIClient", Rect(326, 30, 200, 78), false);
		SCStaticText(window, Rect(95, 30, 80, 18)).string_("MIDIIn port" + toPort);
		clientConnectInButton = SCButton(window, Rect(10, 30, 80, 18))
			.states_([["connect"],["disconnect"]])
			.value_(inIsOn[currentDevice].binaryValue)
			.action_({ |button|
				case {button.value == 1}
					{MIDIIn.connect(toPort,currentDevice);
						inIsOn[currentDevice] = true;
						clientPopUp.action.value(clientPopUp);
						
					}
					{button.value == 0}
					{MIDIIn.disconnect(toPort,currentDevice);
						inIsOn[currentDevice] = false;
						clientPopUp.action.value(clientPopUp);}
				});
		
		clientPopUp = SCPopUpMenu(window, Rect(10, 10, 180, 18))
			.items_( sourceNames )
			.value_(currentDevice)
			.action_({ |popup|
				currentDevice = popup.value;
				clientConnectInButton.value_(inIsOn[currentDevice].binaryValue); 
				popup.items_( getSourceNames.value );
				
				[	{popup.background_(Color.clear).stringColor_(Color.black)},
					{popup.background_(Color.black).stringColor_(Color.red)}]
					[inIsOn[currentDevice].binaryValue].value; 
			})
			.stringColor_(Color.black)
			.background_(Color.clear);
		
		if(noMidi) {clientConnectInButton.enabled=false; clientPopUp.enabled=false; }
			{ clientPopUp.action.value(clientPopUp);};
		
		};
		
		window.onClose_({ window = nil });
		window.front;
		
		SCButton(window, Rect(10, 50, 80, 18)).states_([["restart"]])
			.action_({ window.close; window = nil; { MIDIWindow.new }.defer(0.2) });
		}
	
}