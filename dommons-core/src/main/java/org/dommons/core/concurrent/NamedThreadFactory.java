/*
 * @(#)NamedThreadFactory.java     2018-10-23
 */
package org.dommons.core.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.dommons.core.string.Stringure;
import org.dommons.core.util.Arrayard;

/**
 * 命名线程工厂
 * @author demon 2018-10-23
 */
public class NamedThreadFactory implements ThreadFactory {

	static ConcurrentMap<String, AtomicInteger> ns = new ConcurrentHashMap();

	private final ThreadGroup group;
	private final AtomicInteger threadNumber;
	private final String namePrefix;

	public NamedThreadFactory(String name) {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.namePrefix = Stringure.concat("pool-", name(name), "-thread-");
		threadNumber = new AtomicInteger(1);
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
		if (t.isDaemon()) t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}

	/**
	 * 生成线程工厂名
	 * @param name 名称
	 * @return 工厂名
	 */
	protected String name(String name) {
		name = Stringure.trim(name);
		AtomicInteger aci = ns.get(name);
		if (aci == null) aci = Arrayard.theOne(ns.putIfAbsent(name, aci = new AtomicInteger(1)), aci);
		int n = aci.getAndIncrement();
		if (n > 1) name = name + '-' + n;
		return name;
	}
}
