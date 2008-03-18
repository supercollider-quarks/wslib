+ Class {
	
	instVarDict {
		var dict = ();
		instVarNames.do({ |varname, i|
			dict.put( varname, iprototype[ i ] );
			});
		^dict;
		}
	
	}
	
+ Object {
	
	instVarDict {
		var dict = ();
		this.getSlots.pairsDo({ |varname, item|
			dict.put( varname, item );
			});
		^dict;
		}
		
	*newFromDict { |dict|
		var out, slots;
	
		out = this.prNew;
		if( dict.notNil )
			{ 
			slots = out.class.instVarNames;
			dict.keysValuesDo({ |argname, value|
				if( value.notNil )
					{ 
					if( slots.includes( argname ) )	
						{ out.instVarPut( argname, value ); }
						{ (this.class.name ++ ":newFromDict instVarName '" 
							++ argname ++ "' not found").warn };
						}
				})
			};
		^out;
		
		}
		
	*testMethod { |a = 0,b = 1,c|
			var names, values, dict;
			dict = ();
			names = thisMethod.argNames[1..];
			values = [ a,b,c ];
			names.do({ |name, i|
				if( values[i].notNil )
					{ dict.put( name, values[i] ) };
				});
			^dict;
		}
	
	}