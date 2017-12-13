package com.ruanmeng.sort;

import com.ruanmeng.model.FriendData;

import java.util.Comparator;

public class PinyinFriendComparator implements Comparator<FriendData> {

	public int compare(FriendData o1, FriendData o2) {
		if (o1.getLetter().equals("@")
				|| o2.getLetter().equals("#")) {
			return -1;
		} else if (o1.getLetter().equals("#")
				|| o2.getLetter().equals("@")) {
			return 1;
		} else {
			return o1.getLetter().compareTo(o2.getLetter());
		}
	}

}
