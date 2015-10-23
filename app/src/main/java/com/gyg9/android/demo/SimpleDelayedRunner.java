package com.gyg9.android.demo;

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.gyg9.android.task.delayedrunner.BackgroundTaskRunner;
import com.gyg9.android.task.delayedrunner.HandlerTaskRunner;
import com.gyg9.android.task.delayedrunner.R;

import java.util.concurrent.Executors;

public class SimpleDelayedRunner extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_delayed_runner);

		doDelayTask();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_simple_delayed_runner, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void doDelayTask() {
		HandlerTaskRunner handlerRunner = new HandlerTaskRunner(Looper.getMainLooper(), 2000, 100);
		BackgroundTaskRunner backgroundRunner = new BackgroundTaskRunner(Executors.newCachedThreadPool(), 3000);

		handlerRunner.enqueue(new Runnable() {
			@Override
			public void run() {
				((TextView) findViewById(R.id.tv_hello)).append("\nAsync Handler  task done");
			}
		});

		backgroundRunner.enqueue(new Runnable() {
			@Override
			public void run() {
				HandlerTaskRunner handlerRunner = new HandlerTaskRunner(Looper.getMainLooper(), 0, 100);
				handlerRunner.enqueue(new Runnable() {
					@Override
					public void run() {
						((TextView) findViewById(R.id.tv_hello)).append("\nAsync background task done");
					}
				});
			}
		});

	}
}
