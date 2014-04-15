package org.ultimate.xoandroid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class XOBoardActivity extends Activity implements ActionListener {

	private final static int IDD_GAME_OVER = 0x1000;
	private final static int IDD_GAME_WIN = 0x1001;
	private final static int IDD_GAME_DREW = 0x1002;
	private final static int IDD_GAME_EXIT = 0x1003;

	private final static int IDM_NEW_GAME = 0x2000;
	private final static int IDM_EXIT = 0x2001;

	private XOInfoPanel infoGame;
	private XOInfoPanel infoUser;
	private final List<XOSquare> square = new ArrayList<XOSquare>();
	private final XOClient client = XOClient.getInstance();
	private SharedPreferences sharedPrefs;
	private String userName;
	private AlertDialog.Builder alert;
	private boolean isMove = false;
	private boolean isGame = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.xo_border_activity);
		client.addActionListener(this);

		infoUser = (XOInfoPanel) findViewById(R.id.xOInfoPanel1);
		infoGame = (XOInfoPanel) findViewById(R.id.xOInfoPanel2);

		sharedPrefs = getSharedPreferences("prefference", MODE_PRIVATE);
		userName = sharedPrefs.getString("USER_NAME", "");
		setUserText(userName + ": " + "win = "
				+ sharedPrefs.getInt("USER_WIN", 0) + " " + " losse = "
				+ sharedPrefs.getInt("USER_LOSSE", 0));

		square.add((XOSquare) findViewById(R.id.xOSquare1));
		square.add((XOSquare) findViewById(R.id.xOSquare2));
		square.add((XOSquare) findViewById(R.id.xOSquare3));
		square.add((XOSquare) findViewById(R.id.xOSquare4));
		square.add((XOSquare) findViewById(R.id.xOSquare5));
		square.add((XOSquare) findViewById(R.id.xOSquare6));
		square.add((XOSquare) findViewById(R.id.xOSquare7));
		square.add((XOSquare) findViewById(R.id.xOSquare8));
		square.add((XOSquare) findViewById(R.id.xOSquare9));

		for (int i = 0; i < 9; i++) {
			square.get(i).setCount(i);
			square.get(i).addActionListener(new OnClickListener() {
				@Override
				public void onClick(final View view) {
					int index = ((XOSquare) view).getCount();
					if (isMove) {
						client.sendMessage("mov " + (int) (index / 3) + " "
								+ index % 3);

						runOnUiThread(new Runnable() {
							public void run() {
								((XOSquare) view).invalidate();
								((XOSquare) view).setState(0);

							}
						});
						setMove(false);
						client.sendMessage("whomove");
						setInfoText("Ходит противник!");
					} else {
						if (isGame)
							setInfoText("Ход противника!");
					}
				}
			});
		}

		setInfoText("Ожидаем противника!");
		client.sendMessage("stats");
		client.sendMessage("start");
		setMove(false);
	}

	@Override
	public void actionPerformed(String message) {
		int replyStatus = Integer.valueOf(message.split(" ")[1]);
		System.out.println(message);
		if (message.split(" ")[0].equalsIgnoreCase("+mov")) {
			System.out.println(message);
			final int x = Integer.valueOf(message.split(" ")[1]);
			final int y = Integer.valueOf(message.split(" ")[2]);
			square.get(x * 3 + y).setState(1);

			runOnUiThread(new Runnable() {
				public void run() {
					square.get(x * 3 + y).invalidate();
				}
			});

			setMove(true);
			setInfoText("Ваш ход");
		} else {
			if (message.split(" ")[0].equalsIgnoreCase("+stats")) {
				int win = Integer.valueOf(message.split(" ")[1]);
				int losse = Integer.valueOf(message.split(" ")[2]);

				SharedPreferences.Editor editor = sharedPrefs.edit();
				editor.putInt("USER_WIN", win);
				editor.putInt("USER_LOSSE", losse);
				editor.commit();
				setUserText(userName + ": " + "win = " + win + " "
						+ " losse = " + losse);
			} else {
				switch (replyStatus) {

				case XOServerReply.ADDE_IN_QUEUE:
					setInfoText("Ожидаем противника");
					break;

				case XOServerReply.START_GAME:
					isGame = true;
					setInfoText("Игра началась");
					client.sendMessage("whomove");
					break;

				case XOServerReply.YOU_MOV:
					setInfoText("Ваш ход");
					setMove(true);
					break;

				case XOServerReply.END_GAME:
					setInfoText("Противник покинул игру");
					client.sendMessage("start");
					isGame = false;
					setMove(false);
					squareUp(9);
					break;

				case XOServerReply.YOU_NOT_MOV:
					setInfoText("Ходит противник");
					setMove(false);
					break;

				case XOServerReply.PARTHNER_MAKE_A_MOVE:
					setInfoText("Ваш ход");
					setMove(true);
					break;

				case XOServerReply.DREW:
					setInfoText("Ничья!");
					setMove(false);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							createDialog(IDD_GAME_DREW);
						}
					});

					break;

				case XOServerReply.YOU_LOSSE:
					setMove(false);
					isGame = false;
					client.sendMessage("stats");
					setInfoText("Вы проиграли!");
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							createDialog(IDD_GAME_OVER);
						}
					});

					break;

				case XOServerReply.YOU_WIN:
					setMove(false);
					isGame = false;
					client.sendMessage("stats");
					setInfoText("Вы победили!");

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							createDialog(IDD_GAME_WIN);
						}
					});

					break;
				}
			}
		}
	}

	public void setUserText(final String message) {
		infoUser.setMessage(message);
		runOnUiThread(new Runnable() {
			public void run() {

				infoUser.invalidate();
			}
		});

	}

	public void setInfoText(final String message) {
		infoGame.setMessage(message);
		runOnUiThread(new Runnable() {
			public void run() {

				infoGame.invalidate();
			}
		});

	}

	private void squareUp(final int length) {
		runOnUiThread(new Runnable() {
			public void run() {
				for (int i = 0; i < length; i++) {
					square.get(i).setClicable(true);
					square.get(i).setState(-1);
					square.get(i).setClicable(true);
					square.get(i).invalidate();
				}
			}
		});

	}

	/**
	 * @return the isMove
	 */
	public boolean isMove() {
		return isMove;
	}

	/**
	 * @param isMove
	 *            the isMove to set
	 */
	public void setMove(boolean isMove) {
		this.isMove = isMove;
	}

	private void createDialog(int id) {

		switch (id) {
		case XOBoardActivity.IDD_GAME_WIN:
			alert = new AlertDialog.Builder(this);
			alert.setMessage("Поздравляем, Вы победили! Начать новую игру?");
			alert.setTitle("Проирали!");
			alert.setPositiveButton("Да",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							client.sendMessage("start");
							setInfoText("Ожидание противника!");
							squareUp(9);
						}
					});

			alert.setNegativeButton("Нет",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							squareUp(9);
						}
					});
			alert.create().show();
			break;

		case XOBoardActivity.IDD_GAME_OVER:
			alert = new AlertDialog.Builder(this);
			alert.setMessage("Вы проиграли! Начать новую игру?");
			alert.setTitle("Проирали!");
			alert.setPositiveButton("Да",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							client.sendMessage("start");
							setInfoText("Ожидание противника!");
							squareUp(9);
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Нет",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							squareUp(9);
							dialog.cancel();
						}
					});
			alert.show();
			break;

		case XOBoardActivity.IDD_GAME_DREW:
			alert = new AlertDialog.Builder(this);
			alert.setMessage("Ничья! Начать новую игру?");
			alert.setTitle("Ничья!");
			alert.setPositiveButton("Да",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							client.sendMessage("start");
							setInfoText("Ожидание противника!");
							squareUp(9);
							dialog.cancel();
						}
					});

			alert.setNegativeButton("Нет",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							squareUp(9);
							dialog.cancel();
						}
					});
			alert.show();
			break;

		case IDD_GAME_EXIT:
			alert = new AlertDialog.Builder(this);
			alert.setMessage("Покинуть игру?");
			alert.setTitle("Подтверждение.");
			alert.setPositiveButton("Да",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							client.sendMessage("exit");
							dialog.cancel();
							client.setRunning(false);
							System.exit(0);
						}
					});

			alert.setNegativeButton("Нет",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							squareUp(9);
							dialog.cancel();
						}
					});
			alert.show();
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, IDM_NEW_GAME, Menu.NONE, "Новая игра");
		menu.add(Menu.NONE, IDM_EXIT, Menu.NONE, "Выход");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case IDM_NEW_GAME:
			client.sendMessage("start");
			setInfoText("Ожидание противника!");
			break;

		case IDM_EXIT:
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					createDialog(IDD_GAME_EXIT);
				}
			});

			break;
		default:
			return false;
		}

		return true;
	}
	
	@Override
	public void onBackPressed() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				createDialog(IDD_GAME_EXIT);
			}
		});
	}
}
