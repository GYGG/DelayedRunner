package com.gyg9.android.task.delayedrunner;

import android.util.Log;

import java.util.concurrent.ExecutorService;

/**
 * 后台线程延时任务处理器器
 * Created by gyliu on 15/10/9.
 */
public class BackgroundTaskRunner implements Runnable {

	private static final String TAG = "BackgroundTaskRunner";

	private ExecutorService mExecutorService;
	private volatile boolean executorRunning;

	private PendingTaskQueue mQueue;
	private final long mDelayTime;

	public BackgroundTaskRunner(ExecutorService executorService, long delayTime) {
		mExecutorService = executorService;
		mQueue = new PendingTaskQueue();
		mDelayTime = delayTime;
	}

	public void enqueue(Runnable runnable) {
		PendingTask pendingTask = new PendingTask(runnable);
		synchronized (this) {
			mQueue.enqueue(pendingTask);
			if (!executorRunning) {
				executorRunning = true;
				mExecutorService.execute(this);
			}
		}
	}

	@Override
	public void run() {
		Log.d(TAG, "background runner run");
		try {
			try {
				//TODO 研究一下 sleep 对性能的影响，是否会造成性能问题
				Thread.sleep(mDelayTime);
			} catch (InterruptedException e) {
				Log.e(TAG, Thread.currentThread().getName() + "was Interrupted." + e);
			}
			Log.d(TAG, "background runner wakeup");
			while(true) {
				PendingTask pendingTask;
				synchronized (this) {
					pendingTask = mQueue.poll();
					if (null == pendingTask) {
						executorRunning = false;
						return;
					}
				}
				pendingTask.run();
			}

		} finally {
			executorRunning = false;
		}

	}
}
