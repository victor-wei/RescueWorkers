package com.fg114.main.util;

import java.util.*;

/**
 * 基础Observer，可广播数据
 * @author wufucheng
 *
 */
public class UpdateObserver extends Observable {

	private static UpdateObserver mBaseObserver;

	private UpdateObserver() {

	}

	public synchronized static UpdateObserver getInstance() {
		if (mBaseObserver == null) {
			mBaseObserver = new UpdateObserver();
		}
		return mBaseObserver;
	}
	
	public void sendBroadcast() {
		setChanged();
		notifyObservers();
	}
}
