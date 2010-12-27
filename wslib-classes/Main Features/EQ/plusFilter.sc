// Sciss 2008
// part of wslib 2009

+ MidEQ {
	// XXX they are not correct XXX
	*coeffs { arg sr, freq = 440, rq = 1.0, db = 0.0;
		var amp, pfreq, pbw, c, d, a0, b1, b2;
		
		amp		= db.dbamp - 1.0;
		pfreq	= freq * 2pi / sr;
		pbw		= rq   * pfreq * 0.5;	// bw == rq?
		
		c		= 1.0 / tan( pbw );
		d		= 2.0 * cos( pfreq );
	
		a0		= 1.0 / (1.0 + c);
		b1		= c * d * a0 ;
		b2		= (1.0 - c) * a0;
		a0		= a0 * amp;

		^[[ a0 ], [ b1, b2 ]]
	}
}

+ HPZ1 {
	* coeffs { ^[[ 0.5, -0.5 ]]}
}

+ HPZ2 {
	* coeffs { ^[[ 0.25, -0.5, 0.25 ]]}
}

+ LPZ1 {
	* coeffs { ^[[ 0.5, 0.5 ]]}
}

+ LPZ2 {
	* coeffs { ^[[ 0.25, 0.5, 0.25 ]]}
}

+ OnePole {
	* coeffs { |sr = 44100, coef = 0.5|
		var a0, b0;
		a0 = 1 - abs(coef);
		b0 = coef;
		^[[a0],[b0]]
		}
}

+ OneZero {
	* coeffs { |sr = 44100, coef = 0.5|
		var a0, a1;
		a0 = 1 - abs(coef);
		a1 = coef;
		^[[a0, a1]]		
	}
}

+ SOS {
	* coeffs { |sr = 44100, a0 = 0.0, a1 = 0.0, a2 = 0.0, b1 = 0.0, b2 = 0.0|
		^[[a0, a1, a2], [b1, b2].neg ]
		}
}

+ Filter {
	*coeffs { ^this.subclassResponsibility( thisMethod )}
	
	*magResponse { arg freqs, sr ... rest;
		var pfreq, cos1, cos2, cos3, cos4, cos5, nom, denom;
		var a0, a1, a2, a3, a4, a5, b1, b2, b3, b4, b5;
		var ar, ma;
		var radPerSmp; //= 2pi / sr;
		
		sr = sr ?? { Server.default.sampleRate };
		radPerSmp = 2pi / sr;

		#ma, ar = this.coeffs( sr, *rest );
		#a0, a1, a2, a3, a4, a5 = ma ++ [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ];
		#b1, b2, b3, b4, b5     = ar ++ [ 0.0, 0.0, 0.0, 0.0, 0.0 ];
		
		^freqs.collect({ arg freq;
			var complex;
			pfreq	= freq * radPerSmp;
			cos1	= cos( pfreq );
			cos2	= cos( pfreq * 2 );
			cos3	= cos( pfreq * 3 );
			cos4	= cos( pfreq * 4 );
			cos5	= cos( pfreq * 5 );
		
			// http://www.musicdsp.org/showone.php?id=108
			nom =   (a0*a0) + (a1*a1) + (a2*a2) + (a3*a3) + (a4*a4) + (a5*a5) + (2 * (
				(((a0*a1) + (a1*a2) + (a2*a3) + (a3*a4) + (a4*a5)) * cos1) +
				(((a0*a2) + (a1*a3) + (a2*a4) + (a3*a5)) * cos2) +
				(((a0*a3) + (a1*a4) + (a2*a5)) * cos3) +
				(((a0*a4) + (a1*a5)) * cos4) +
				  (a0*a5 * cos5)));
		
			denom = 1.0 + (b1*b1) + (b2*b2) + (b3*b3) + (b4*b4) + (b5*b5) + (2 * (
				 ((b1 + (b1*b2) + (b2*b3) + (b3*b4) + (b4*b5)) * cos1) +
				 ((b2 + (b1*b3) + (b2*b4) + (b3*b5)) * cos2) +
		             ((b3 + (b1*b4) + (b2*b5)) * cos3) +
		             ((b4 + (b1*b5)) * cos4) +
		   		  (b5 * cos5)));
		
			sqrt( nom / denom );
		});
	}
	
}