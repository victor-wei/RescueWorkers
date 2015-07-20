package com.fg114.main.util;

import java.util.*;

/**
 * 被监听器者，该类与系统类的不同之处在于，可以传递一个hint 做数据
 * 
 * @author xujianjun,2012-09-17
 * 
 */
public class CommonObservable  {

	private Vector<CommonObserver> obs;
	private volatile static CommonObservable mObservable;
	static {
		mObservable = new CommonObservable();
	}

	private CommonObservable() {
		obs = new Vector();
	}

	public static CommonObservable getInstance() {
		return mObservable;
	}

	public synchronized void addObserver(CommonObserver o) {
		if (o == null)
			throw new NullPointerException();
		Vector<CommonObserver> shouldDelete=new Vector<CommonObserver>();
		//不允许重复的监听者 
		for(CommonObserver obj : obs){
			if(obj==o||obj.getClass()==o.getClass()){
				shouldDelete.add(obj);			}
		}
		for(CommonObserver obj : shouldDelete){
			deleteObserver(obj);
		}
		shouldDelete.clear();
		obs.addElement(o);
	}

	public synchronized void deleteObserver(CommonObserver o) {
		obs.removeElement(o);
	}
	public synchronized void deleteObserver(Class<?> hint) {
		if(hint==null){
			return;
		}

		Object[] arrLocal;
		synchronized (this) {
			arrLocal = obs.toArray();
		}

		for (int i = arrLocal.length - 1; i >= 0; i--){
			if(hint.isInstance(arrLocal[i])){
				deleteObserver((CommonObserver) arrLocal[i]);
			}
		}
	}

	public void notifyObservers() {
		notifyObservers(null);
	}
	/**
	 * 通知某个特定observer，该observer是hint类的实例
	 * @param hint
	 */
	public void notifyObservers(Class<?> hint) {
		notifyObservers(hint, null);
	}
	
	/**
	 * 通知某个特定observer，该observer是hint类的实例
	 * @param hint
	 */
	public void notifyObservers(Class<?> hint, Object data) {
		if(hint==null){
			return;
		}

		Object[] arrLocal;
		synchronized (this) {
			arrLocal = obs.toArray();
		}

		for (int i = arrLocal.length - 1; i >= 0; i--){
			if(hint.isInstance(arrLocal[i])){
				((CommonObserver) arrLocal[i]).update(this, data);
			}
		}
	}

	public void notifyObservers(Object arg) {

		Object[] arrLocal;

		synchronized (this) {
			arrLocal = obs.toArray();
		}

		for (int i = arrLocal.length - 1; i >= 0; i--)
			((CommonObserver) arrLocal[i]).update(this, arg);
	}

	public synchronized void deleteObservers() {
		obs.removeAllElements();
	}

	public synchronized int countObservers() {
		return obs.size();
	}
}
