package ps.database;

import java.util.Comparator;

public class CompareSites implements Comparator<Site>{

	@Override
	public int compare(Site s1, Site s2) {
		int fwf = s1.getWordFreq();
		int swf = s2.getWordFreq();
		
		if(fwf > swf){
			return 1;
		}else if(fwf < swf){
			return -1;
		}else {
			return 0;
		}
	}

}
