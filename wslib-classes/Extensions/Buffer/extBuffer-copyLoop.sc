// wslib 2007 - copy a part of a buffer to another buffer, loop if index out of range

+ Buffer {
	copyLoop { arg buf, dstStartAt = 0, srcStartAt = 0, numSamples = -1;
		var srcPointer, dstPointer, numSamplesLeft, numSamplesThisTime;
		if( numSamples.isNegative ) { numSamples = buf.numFrames - dstStartAt };
		srcStartAt = srcStartAt.wrap(0, numFrames );
		numSamplesLeft = numSamples;
		srcPointer = srcStartAt;
		dstPointer = dstStartAt;
		while { numSamplesLeft > 0 }
			{ 	numSamplesThisTime = numSamplesLeft.min( numFrames - srcPointer );
				this.copyData( buf, dstPointer, srcPointer, numSamplesThisTime );
				//"copied: %,%,%\n".postf( dstPointer, srcPointer, numSamplesThisTime );
				dstPointer =  dstPointer + numSamplesThisTime;
				numSamplesLeft = numSamplesLeft - numSamplesThisTime;
				srcPointer = 0;
				
			 };
		}

	}