package com.plusonelabs.dashclock.gerrit;

public class Change {

	private int _number;

	public int getChangeId() {
		return _number;
	}

	public void setChangeId(int changeId) {
		this._number = changeId;
	}

	@Override
	public String toString() {
		return "Change [changeId=" + _number + "]";
	}

}
