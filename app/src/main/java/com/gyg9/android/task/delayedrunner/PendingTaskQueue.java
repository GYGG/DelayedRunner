package com.gyg9.android.task.delayedrunner;

import android.util.Log;

/**
 * 计划任务队列
 * Created by gyliu on 15/10/9.
 */
public class PendingTaskQueue {
	private static final String TAG = "PendingTaskQueue";
	private PendingTask head;
	private PendingTask tail;

	synchronized void enqueue(PendingTask pendingTask) {
		Log.d(TAG, "enqueue at " + Thread.currentThread().getName());
		if (pendingTask == null) {
			throw new NullPointerException("null cannot be enqueued");
		}
		if (tail != null) {
			tail.next = pendingTask;
			tail = pendingTask;
		} else if (head == null) {
			head = tail = pendingTask;
		} else {
			throw new IllegalStateException("Head present, but no tail");
		}
//		notifyAll();
	}

	synchronized PendingTask poll() {
		Log.d(TAG, "poll at " + Thread.currentThread().getName());
		PendingTask pendingTask = head;
		if (head != null) {
			head = head.next;
			if (head == null) {
				tail = null;
			}
		}
		return pendingTask;
	}

}
