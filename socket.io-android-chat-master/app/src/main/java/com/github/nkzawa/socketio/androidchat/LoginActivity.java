package com.github.nkzawa.socketio.androidchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * A login screen that offers login via username.
 * 登录屏幕，通过用户名登录。
 */
public class LoginActivity extends Activity {

    private EditText mUsernameView;

    private String mUsername;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();

        // Set up the login form.
        // 设置登录表单。
        mUsernameView = (EditText) findViewById(R.id.username_input);
        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mSocket.on("login", onLogin);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.off("login", onLogin);
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    /**
     * 尝试登录登录表单指定的帐户。如果有表单错误（无效的用户名，缺少的字段等），
     * 则会显示错误，并且不会进行实际的登录尝试。
     */

    private void attemptLogin() {
        // Reset errors.
        // 复位错误
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        // 存储登录尝试时的值
        String username = mUsernameView.getText().toString().trim();

        // Check for a valid username.
        // 检查一个有效的用户名
        if (TextUtils.isEmpty(username)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            /**
             * 有一个错误; 不要尝试登录并重点关注第一个
             * 表单域出现错误
             */
            mUsernameView.setError(getString(R.string.error_field_required));
            mUsernameView.requestFocus();
            return;
        }

        mUsername = username;

        // perform the user login attempt.
        // 执行用户登录尝试
        mSocket.emit("add user", username);
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("username", mUsername);
            intent.putExtra("numUsers", numUsers);
            setResult(RESULT_OK, intent);
            finish();
        }
    };
}



