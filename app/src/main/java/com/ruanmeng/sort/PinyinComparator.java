package com.ruanmeng.sort;

import com.ruanmeng.model.CityData;

import java.util.Comparator;

public class PinyinComparator implements Comparator<CityData> {

	public int compare(CityData o1, CityData o2) {
		if (o1.getFirstLetter().equals("@")
				|| o2.getFirstLetter().equals("#")) {
			return -1;
		} else if (o1.getFirstLetter().equals("#")
				|| o2.getFirstLetter().equals("@")) {
			return 1;
		} else {
			return o1.getFirstLetter().compareTo(o2.getFirstLetter());
		}
	}

}
