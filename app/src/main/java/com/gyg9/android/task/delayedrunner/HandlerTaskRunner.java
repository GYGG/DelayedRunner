package com.gyg9.android.task.delayedrunner;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

/**
 * 主线程延时任务处理器
 * Created by gyliu on 15/10/9.
 */
public class HandlerTaskRunner extends Handler {

	private static final String TAG = "HandlerTaskRunner";
	private final PendingTaskQueue mQueue;
	private final long mDelayTime;
	private final int maxMillisInsideHandleMessage;
	private boolean handlerActive;

	public HandlerTaskRunner(Looper looper, long delayTime, int maxMillisInsideHandleMessage) {
		super(looper);
		this.mQueue = new PendingTaskQueue();
		this.mDelayTime = delayTime;
		this.maxMillisInsideHandleMessage = maxMillisInsideHandleMessage;
	}

	/**
	 * <p>添加一个延时实行任务到队列中。</p>
	 * @param runnable 任务
	 */
	public void enqueue(Runnable runnable) {
		PendingTask pendingTask = new PendingTask(runnable);
		synchronized (mQueue) {
			mQueue.enqueue(pendingTask);
			if (!handlerActive) {
				handlerActive = true;
				if (!sendMessageDelayed(obtainMessage(), mDelayTime)) {
					throw new RuntimeException("Could not send handler message");
				}
			}
		}
	}

	@Override
	public void handleMessage(Message msg) {
		Log.d(TAG, "handlemessage");
		boolean rescheduled = false;
		try {
			long started = SystemClock.uptimeMillis();
			while(true) {
				PendingTask task = mQueue.poll();
				if (null == task) {
					synchronized (this) {
						// Check again, this time in synchronized
						task = mQueue.poll();
						if (null == task) {
							handlerActive = false;
							return;
						}
					}
				}
				Log.d(TAG, "handlemessage run");
				task.run();
				//in order to keep from bocking main thread, the process will be relooper .
				//为了避免主进程阻塞，重新开始一次handleMessage过程。
				long timeInMethod = SystemClock.uptimeMillis() - started;
				if (timeInMethod >= maxMillisInsideHandleMessage) {
					if (!sendMessage(obtainMessage())) {
						throw new RuntimeException("Could not send handler message");
					}
					rescheduled = true;
					return;
				}
			}
		} finally {
			handlerActive = rescheduled;
		}

	}

}
