+ Buffer {
	*readDefault { |server, action, bufnum|
		^this.read( server, "sounds/a11wlk01.wav", action: action, bufnum: bufnum );
		}
	}