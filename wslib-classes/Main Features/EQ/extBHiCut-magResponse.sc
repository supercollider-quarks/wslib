+ BHiCut {
	
	*magResponse { arg freqs, sr, freq, order = 2;
		var rqs, in; 
		rqs = allRQs.clipAt(order); 
		in = 1!freqs.size;
		rqs.do {Ê|rq|Êin = in * this.filterClass.magResponse( freqs, sr, freq, rq ) };
		^in
	}
	
}