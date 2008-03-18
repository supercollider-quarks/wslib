// part of wslib 2005

+ String { 
	
	// general flexibility improvements:
	
	collect { | function | 
		// this changes functionality of some other methods (like .tr) too
		var result = "";
		this.do {|elem, i| result = result ++ function.value(elem, i); }
		^result;
		}
		
	*fill { | size, function | // any output for function is accepted
		var obj = "";
		size.do { | i | obj = obj ++ function.value(i); };
		^obj
	}

	removeItems { |items = "\n\t"| // defaults to removing returns and tabs
		// !! not in place !!
		items = items.asString;
		^this.collect({ |char| if( items.includes(char) ) {""} {char} });
		}
	replaceItems { |items = "\n\t", replaceWith = "  "| 
		// !! not in place !!
		// replaceWith can also be array of strings
		// similar to tr, but replaces multiple chars
		// "hello".replaceItems("ho", ["oh h",$!])
		^this.collect({ |char|
			var index = items.indexOf(char);
			if( index.notNil )
				{ replaceWith[index] }
				{ char }
			});
		}
		
	// shortcuts:
	
	removeBrackets { ^this.removeItems("()") } // for SCPopUpMenu items
	replaceBrackets { arg replaceWith = "[]"; 
		^this.replaceItems("()", replaceWith) }
	replaceSpaces { arg replaceWith = "_";
		^this.replaceItems(" ", replaceWith) }
}