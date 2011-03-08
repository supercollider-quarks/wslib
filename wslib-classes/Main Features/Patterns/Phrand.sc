Phrand : ListPattern {
	var <>histSize;
	
	*new { arg list, histSize=2, repeats=1;
		^super.new(list, repeats).histSize_(histSize)
	}
	
	embedInStream { arg inval;
		var item, size, history = [];
		var index;
		repeats.value(inval).do({ arg i;
			var count = 0;
			size = list.size;
			index = size.rand;
			while { history.includes( index ) && { count < 1000 }; } // prevent inf loop
				{ index = size.rand; count = count + 1 };
			item = list.at(index);
			history = ([ item ] ++ history)[..histSize-1];
			inval = item.embedInStream(inval);
		});
		^inval;
	}
}