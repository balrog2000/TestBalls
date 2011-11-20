package com.tkraus.greenball;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.tkraus.greenball.model.BallEnum;
import com.tkraus.greenball.model.JenkinsFailEnum;
import com.tkraus.greenball.model.JenkinsResult;

public class WatchActivity extends Activity {
	private ImageView firstBall;
	private TableLayout table;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		firstBall = (ImageView) findViewById(R.id.imageView1);
		table = (TableLayout) findViewById(R.id.tableLayout1);
	}

	public void updateStatuses(View view) {
		Toast.makeText(this, "Pre string", Toast.LENGTH_SHORT);
		switch (view.getId()) {
		case R.id.updateButton:

			JenkinsResult result = new JenkinsFetcher()
					.doInBackground("http://zadane.pl");
			Toast.makeText(this, result.getStatus().toString(),
					Toast.LENGTH_SHORT).show();
			if (result.getStatus() != JenkinsFailEnum.SUCCESS) {
				return;
			}

			table.removeAllViews();

			for (String job : result.getJobs()) {
				BallEnum status = result.getJobStatus(job);

				TableRow newRow = new TableRow(this);

				ImageView newBall = new ImageView(this);
				newBall.setImageResource(status.getBallResourceId());

				TextView jobTextView = new TextView(this);
				jobTextView.setText(job);

				TextView statusTextView = new TextView(this);
				statusTextView.setText(status.isAnimated() ? "RUNNING"
						: "STABLE");

				TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
						TableLayout.LayoutParams.WRAP_CONTENT,
						TableLayout.LayoutParams.WRAP_CONTENT);

				int margin = (int) getResources().getDimension(
						R.dimen.regular_margin);

				tableRowParams.setMargins(margin, margin, margin, margin);

				newRow.setLayoutParams(tableRowParams);

				newRow.addView(newBall);
				newRow.addView(jobTextView);
				newRow.addView(statusTextView);
				table.addView(newRow);
			}

			break;
		}
	}
}