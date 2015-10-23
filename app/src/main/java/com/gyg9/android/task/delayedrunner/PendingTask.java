package com.gyg9.android.task.delayedrunner;

/**
 * <p>计划任务类</p>
 * Created by gyliu on 15/10/9.
 */
public class PendingTask implements Runnable {

	private final Runnable mRunnable;
	PendingTask next;

	public PendingTask(Runnable runnable) {
		mRunnable = runnable;
	}

	@Override
	public void run() {
		mRunnable.run();
	}
}
