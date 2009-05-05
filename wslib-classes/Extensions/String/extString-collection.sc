// part of wslib 2005
//
// conversion String <-> SequenceableCollection

+ SequenceableCollection {
	toString { ^String.fill(this.size, { |i| 
		if(this[i].notNil) {this[i]} {" "} }) }
	asAscii {  ^String.fill(this.size, { |i| {this[i].asInteger.asAscii}.try ? "" }) } 
	asDigit {  ^String.fill(this.size, { |i| {this[i].asInteger.asDigit}.try ? "" }) }
	
}

+ String {
	digit { ^Array.fill(this.size, { |i| {this[i].digit}.try }); }
	// ascii { ^Array.fill(this.size, { |i| this[i].ascii }); } //-> remove for 3.3
	asUnicode { ^this.ascii }
}