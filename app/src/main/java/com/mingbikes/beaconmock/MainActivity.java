package com.mingbikes.beaconmock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mingbikes.beaconmock.bluetooth.BeaconManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BeaconManager.getInstance().setContext(this);

        initViews();
    }

    public void onStartClick(View view) {

        if (et_major.getText() == null || et_major.getText().length() == 0) {
            Toast.makeText(this, "存在输入参数major,请输入。", Toast.LENGTH_SHORT).show();
            return;
        }

        if (et_minor.getText() == null || et_minor.getText().length() == 0) {
            Toast.makeText(this, "存在输入参数minor,请输入。", Toast.LENGTH_SHORT).show();
            return;
        }

        if (et_tx_power.getText() == null || et_tx_power.getText().length() == 0) {
            Toast.makeText(this, "存在输入参数txPower,请输入。", Toast.LENGTH_SHORT).show();
            return;
        }

        BeaconManager.getInstance().init(this, 999);

        BeaconManager.getInstance().setMajor(Integer.valueOf(et_major.getText().toString()));
        BeaconManager.getInstance().setMinor(Integer.valueOf(et_minor.getText().toString()));
        BeaconManager.getInstance().setTxPower(Integer.valueOf(et_tx_power.getText().toString()));

        BeaconManager.getInstance().startAdvertising();
    }

    public void onStopClick(View view) {
        BeaconManager.getInstance().stopAdvertising();

        if(tv_status != null) {
            tv_status.setText("停止广播");
        }
    }

    @Override
    protected void onDestroy() {
        BeaconManager.getInstance().stopAdvertising();
        super.onDestroy();
    }

    TextView tv_status;
    EditText et_major;
    EditText et_minor;
    EditText et_tx_power;

    private void initViews() {
        tv_status = (TextView) findViewById(R.id.tv_status);

        et_major = (EditText) findViewById(R.id.et_major);
        et_minor = (EditText) findViewById(R.id.et_minor);
        et_tx_power = (EditText) findViewById(R.id.et_tx_power);
    }
}
