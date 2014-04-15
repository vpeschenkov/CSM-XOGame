package org.ultimate.xoandroid;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements ActionListener {

	private EditText userNameTextEdit;
	private EditText userPasswordTextEdit;
	private SharedPreferences sharedPrefs;
	private final XOClient client = XOClient.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		client.addActionListener(this);

		sharedPrefs = getSharedPreferences("prefference", MODE_PRIVATE);
		userNameTextEdit = (EditText) findViewById(R.id.userNameTextEdit);
		userPasswordTextEdit = (EditText) findViewById(R.id.userPasswordTextEdit);
		final Button authorizationButton = (Button) findViewById(R.id.authorizationButton);
		authorizationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (userNameTextEdit.getText().length() != 0) {
					SharedPreferences.Editor editor = sharedPrefs.edit();
					editor.putString("USER_NAME", userNameTextEdit.getText()
							.toString());
					editor.putString("USER_PASSWORD", userPasswordTextEdit
							.getText().toString());
					editor.commit();

					client.sendMessage("user "
							+ userNameTextEdit.getText().toString());
					client.sendMessage("password "
							+ String.valueOf(userPasswordTextEdit.getText()
									.toString()));

				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(MainActivity.this,
									"Неверные данные", Toast.LENGTH_SHORT)
									.show();
						}
					});
				}
			}
		});

		userNameTextEdit.setText(sharedPrefs.getString("USER_NAME", ""));
		userPasswordTextEdit
				.setText(sharedPrefs.getString("USER_PASSWORD", ""));
	}

	@Override
	public void actionPerformed(String message) {
		int replyStatus = Integer.valueOf(message.split(" ")[1]);

		if (replyStatus == XOServerReply.AUTHENTIFICATIONS_IS_SUCCESFULL) {
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(MainActivity.this,
							"Авторизация прошла успешно!", Toast.LENGTH_SHORT)
							.show();
				}
			});

			client.removeActionListener(this);
			startActivity(new Intent(this, XOBoardActivity.class));
			this.finish();
		}

		if (replyStatus == XOServerReply.THIS_NAME_IS_ALREADY_TAKEN) {
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(MainActivity.this, "Такое имя уже занято!",
							Toast.LENGTH_SHORT).show();
				}
			});

		}

		if (replyStatus == XOServerReply.OPERATION_WAS_NOT_SUCCESFUL) {
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(MainActivity.this, "Неизвестная ошибка!",
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	@Override
	public void onBackPressed() {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(MainActivity.this, "Приложение завершено!",
						Toast.LENGTH_SHORT).show();
			}
		});
		System.exit(0);
	}
}
