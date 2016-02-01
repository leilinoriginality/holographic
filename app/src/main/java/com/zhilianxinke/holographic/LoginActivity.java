package com.zhilianxinke.holographic;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zhilianxinke.holographic.common.FastJsonRequest;
import com.zhilianxinke.holographic.common.SdkHttpResult;
import com.zhilianxinke.holographic.entity.AppUsers;
import com.zhilianxinke.holographic.utils.CacheUtils;
import com.zhilianxinke.holographic.utils.CharCheckUtil;
import com.zhilianxinke.holographic.utils.KeyBoardUtils;
import com.zhilianxinke.holographic.utils.L;
import com.zhilianxinke.holographic.utils.RegexTools;
import com.zhilianxinke.holographic.utils.WinToast;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ldb on 2016/1/27.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.exit)
    ImageView exit;
    @Bind(R.id.input_phone)
    EditText phone;
    @Bind(R.id.input_delete)
    ImageView delete;
    @Bind(R.id.code)
    EditText code;
    @Bind(R.id.get_code)
    TextView getVCode;
    @Bind(R.id.login)
    TextView login;
    @Bind(R.id.rl_code)
    RelativeLayout rl_code;


    private String IDENTIFYING_CODE = "";
    private boolean flag = true;
    private int countSeconds = 60;        // 倒数秒数
    private final String STATE = "state";
    private boolean isFlag;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initListener() {
        exit.setOnClickListener(this);
        delete.setOnClickListener(this);
        getVCode.setOnClickListener(this);
        login.setOnClickListener(this);

    }

    @Override
    protected void initialized() {

        isFlag = getIntent().getBooleanExtra(STATE, false);
        if (!isFlag) {
            rl_code.setVisibility(View.GONE);
        }
        delete.setVisibility(View.GONE);
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    delete.setVisibility(View.VISIBLE);
                } else {
                    delete.setVisibility(View.GONE);
                }
            }
        });

    }

    @SuppressLint("HandlerLeak")
    private Handler mCountHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (countSeconds > 0) {
                --countSeconds;
                getVCode.setText("发送验证码" + "(" + countSeconds + ")");
                mCountHanlder.sendEmptyMessageDelayed(0, 1000);
            } else {
                countSeconds = 60;
                getVCode.setText("发送验证码");
            }

        }
    };

    // 开始倒计时
    private void startCountBack() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getVCode.setText(countSeconds + "");
                mCountHanlder.sendEmptyMessage(0);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_code:                    // 获取手机号发送请求向手机发送验证码
                KeyBoardUtils.closeKeybord(phone, this);
                if (countSeconds != 60) {
                    WinToast.toast(this, "您的验证码已发送!");
                    return;
                }
                String phoneStr = phone.getText().toString();
                if (RegexTools.isMobileNO(phoneStr)) {
                    getMassageCode(phoneStr);
                    startCountBack();
                } else {
                    WinToast.toast(this, "输入的手机号码有误!");
                }
                break;

            case R.id.login:
                KeyBoardUtils.closeKeybord(phone, this);
                if (LoginCheck()) {
                    //登录
                    if (!isFlag) {
                        loading(phone.getText().toString());
                    } else {
                        register(phone.getText().toString());
                    }

                }
                break;

            case R.id.exit:
                finish();
                break;
            case R.id.input_delete:
                phone.setText("");
                delete.setVisibility(View.GONE);
                break;

        }

    }

    /**
     * 获取短信验证码
     **/
    public void getMassageCode(String phone) {
        Map<String, String> param = new HashMap<>();
        param.put("phone", phone);
        String url = urlBuilder.build(urlBuilder.getMassage, param);
        L.i("获取验证码:" + url);
        FastJsonRequest<SdkHttpResult> fastJsonRequest = new FastJsonRequest<SdkHttpResult>(url, SdkHttpResult.class, new Response.Listener<SdkHttpResult>() {
            @Override
            public void onResponse(SdkHttpResult response) {
                if (response.getCode() != 400) {
                    IDENTIFYING_CODE = response.getDetail();
                    // WinToast.toast(LoginActivity.this, "发送成功");
                } else {
                    WinToast.toast(LoginActivity.this, "发送失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                WinToast.toast(getContext(), R.string.check_noteWork);
            }
        });
        requestQueue.add(fastJsonRequest);
    }


    /**
     * 注册。。
     **/
    public void register(final String phone) {
        mDialog.setText("正在注册...");
        mDialog.show();
        Map<String, String> param = new HashMap<>();
        param.put("phone", phone);
        String url = urlBuilder.build(urlBuilder.register, param);
        L.i("注册:" + url);
        FastJsonRequest<SdkHttpResult> fastJsonRequest = new FastJsonRequest<SdkHttpResult>(url, SdkHttpResult.class, new Response.Listener<SdkHttpResult>() {
            @Override
            public void onResponse(SdkHttpResult response) {
                if (response.getCode() == 200) {
                    loading(phone);
                } else {
                    mDialog.dismiss();
                    WinToast.toast(LoginActivity.this, "注册失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();
                WinToast.toast(getContext(), R.string.check_noteWork);
            }
        });
        requestQueue.add(fastJsonRequest);
    }

    /**
     * 登录
     **/
    public void loading(String phone) {
        mDialog.setText("正在登录...");
        mDialog.show();
        Map<String, String> param = new HashMap<>();
        param.put("phone", phone);
        String url = urlBuilder.build(urlBuilder.connect, param);
        L.i("登录:" + url);
        FastJsonRequest<SdkHttpResult> fastJsonRequest = new FastJsonRequest<SdkHttpResult>(url, SdkHttpResult.class, new Response.Listener<SdkHttpResult>() {
            @Override
            public void onResponse(SdkHttpResult response) {
                if (response.getCode() == 200) {
                    AppUsers appUsers = JSON.parseObject(response.getResult(), AppUsers.class);
                    CacheUtils.saveObject(LoginActivity.this, appUsers);
                    WinToast.toast(LoginActivity.this, "登录成功");
                    mDialog.dismiss();
                    finish();
                } else {
                    mDialog.dismiss();
                    WinToast.toast(LoginActivity.this, "登录失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();
                WinToast.toast(getContext(), R.string.check_noteWork);
            }
        });
        requestQueue.add(fastJsonRequest);
    }

    /**
     * 登录参数校验
     *
     * @param
     * @param
     * @return false || true
     */
    public boolean LoginCheck() {
        if (!CharCheckUtil.isPhoneNum(phone.getText().toString())) {
            WinToast.toast(this, "您输入的手机号码有误，请重新输入!");
            return false;
        }
        if (!isFlag) {
            if (!code.getText().toString().equals(IDENTIFYING_CODE)) {
                WinToast.toast(this, "您输入的验证码有误，请重新输入!");
                return false;
            }
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }


}
