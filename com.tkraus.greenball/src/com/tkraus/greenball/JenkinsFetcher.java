package com.tkraus.greenball;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.tkraus.greenball.model.BallEnum;
import com.tkraus.greenball.model.JenkinsException;
import com.tkraus.greenball.model.JenkinsFailEnum;
import com.tkraus.greenball.model.JenkinsResult;

public class JenkinsFetcher extends AsyncTask<String, Integer, JenkinsResult> {

	private StringBuilder inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();

		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		// Read response until the end
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Return full string
		return total;
	}

	private HttpRequestInterceptor preemptiveAuth;

	@Override
	protected JenkinsResult doInBackground(String... params) {

		preemptiveAuth = new HttpRequestInterceptor() {
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				AuthState authState = (AuthState) context
						.getAttribute(ClientContext.TARGET_AUTH_STATE);
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context
						.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				if (authState.getAuthScheme() == null) {
					AuthScope authScope = new AuthScope(
							targetHost.getHostName(), targetHost.getPort());
					Credentials creds = credsProvider.getCredentials(authScope);
					if (creds != null) {
						authState.setAuthScheme(new BasicScheme());
						authState.setCredentials(creds);
					}
				}
			}
		};

		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope("", 8080),
				new UsernamePasswordCredentials("", ""));

		DefaultHttpClient httpclient = new DefaultHttpClient();

		httpclient.setCredentialsProvider(credsProvider);
		httpclient.addRequestInterceptor(preemptiveAuth, 0);
		JenkinsResult jenkinsResult = new JenkinsResult();
		try {
			HttpGet httpget = new HttpGet(
					"http://4:8080/api/json?tree=jobs[name,color]");
			HttpResponse jobGet = httpclient.execute(httpget);
			int status = jobGet.getStatusLine().getStatusCode();

			if (status == 401 || status == 403) {
				throw new JenkinsException(JenkinsFailEnum.CREDENTIALS);
			} else if (status != 200) {
				throw new JenkinsException(JenkinsFailEnum.WTF);
			}

			String content = inputStreamToString(
					jobGet.getEntity().getContent()).toString();

			Log.d("content", content);

			JSONObject hudsonReturn = new JSONObject(content);
			JSONArray jobs = hudsonReturn.getJSONArray("jobs");
			for (int jobId = 0; jobId < jobs.length(); jobId++) {
				JSONObject job = jobs.getJSONObject(jobId);
				String jobName = job.getString("name");
				BallEnum jobStatus = BallEnum.fromColor(job.getString("color"));
				jenkinsResult.addJob(jobName, jobStatus);
			}
			System.out.println(jenkinsResult.toString());
			jenkinsResult.setStatus(JenkinsFailEnum.SUCCESS);

		} catch (ClientProtocolException e) {
			jenkinsResult.setStatus(JenkinsFailEnum.IO);
			e.printStackTrace();
		} catch (IOException e) {
			jenkinsResult.setStatus(JenkinsFailEnum.IO);
			e.printStackTrace();
		} catch (JenkinsException e) {
			jenkinsResult.setStatus(e.getReason());
			e.printStackTrace();
		} catch (JSONException e) {
			jenkinsResult.setStatus(JenkinsFailEnum.JSON);
			e.printStackTrace();
		}
		return jenkinsResult;
	}
}
