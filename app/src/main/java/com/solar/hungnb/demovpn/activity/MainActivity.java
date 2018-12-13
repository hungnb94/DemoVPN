package com.solar.hungnb.demovpn.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.VpnService;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.solar.hungnb.demovpn.R;
import com.solar.hungnb.demovpn.service.FavoriteAppService;
import com.solar.hungnb.demovpn.utils.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import de.blinkt.openvpn.core.OpenVPNManagement;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;

public class MainActivity extends Activity implements VpnStatus.ByteCountListener, VpnStatus.StateListener {
    private final String TAG = MainActivity.class.getSimpleName();
    private final int RC_START_VPN = 10;
    private final int RC_USAGE_PERMISSION = 11;

    private static OpenVPNService mService;
    private boolean isBindService = false;
    private boolean firstData = false;
    private long connectionTime;

    private TextView tvStatus;
    private TextView tvTimeConnect;


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            if (binder instanceof OpenVPNService.LocalBinder) {
                mService = ((OpenVPNService.LocalBinder) binder).getService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    Thread threadUpdateConnectionTime = new Thread() {
        @Override
        public void run() {
            while (VpnStatus.isVPNActive()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long timeConnectMillis = System.currentTimeMillis() - connectionTime;
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        Calendar calConnectTime = new GregorianCalendar();
                        calConnectTime.setTimeZone(TimeZone.getTimeZone("GMT"));
                        calConnectTime.setTimeInMillis(timeConnectMillis);
                        String timeConnect = String.format(getString(R.string.connect_time),
                                sdf.format(calConnectTime.getTime()));
                        tvTimeConnect.setText(timeConnect);
                    }
                });
                SystemClock.sleep(1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTimeConnect = findViewById(R.id.tvTimeConnect);
        tvStatus = findViewById(R.id.tvStatus);

        VpnStatus.addByteCountListener(this);
        VpnStatus.addStateListener(this);

        checkMyService();
    }

    private void checkMyService() {
        if (!CommonUtils.isMyServiceRunning(this, FavoriteAppService.class)) {
            startService(new Intent(this, FavoriteAppService.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        isBindService = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBindService) {
            isBindService = false;
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(trafficReceiver);
    }

    private void preprapeVpn() {
        Intent intent = VpnService.prepare(this);
        if (intent != null) {
            startActivityForResult(intent, RC_START_VPN);
        } else {
            startVpn();
        }
    }

    private void startVpn() {
        CommonUtils.startDefaultVpn(this);
    }

    private void stopVpn() {
        ProfileManager.setConntectedVpnProfileDisconnected(this);
        if (mService != null && mService.getManagement() != null) {
            mService.getManagement().stopVPN(false);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_START_VPN) {
            if (resultCode == RESULT_OK) {
                startVpn();
            } else {
                Toast.makeText(this, "Can not open vpn", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == RC_USAGE_PERMISSION){
            if(isUsageAccessGranted()){
                Intent intent = new Intent(this, InstallAppsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Can not doing this action without permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.btnStartVpn:
                firstData = false;
                preprapeVpn();
                break;
            case R.id.btnStopVpn:
                firstData = true;
                stopVpn();
                break;
            case R.id.btnSelectApp:
                selectApp();
                break;
            default:
                break;
        }
    }

    private void selectApp() {
        if (isUsageAccessGranted()) {
            Intent intent = new Intent(this, InstallAppsActivity.class);
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("")
                    .setMessage("")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            startActivityForResult(intent, RC_USAGE_PERMISSION);

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false);
            builder.show();
        }
    }

    private boolean isUsageAccessGranted() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @Override
    public void updateByteCount(long in, long out, long diffIn, long diffOut) {
        if (firstData) {
            firstData = false;
        } else if (connectionTime > 0) {
            final String downloadSession = String.format(getString(R.string.traffic_in),
                    OpenVPNService.humanReadableByteCount(in, false));
            final String uploadSession = String.format(getString(R.string.traffic_out),
                    OpenVPNService.humanReadableByteCount(out, false));
            final String downloadSpeed = String.format(getString(R.string.download_speed),
                    OpenVPNService.humanReadableByteCount(diffIn / OpenVPNManagement.mBytecountInterval, true));
            final String uploadSpeed = String.format(getString(R.string.upload_speed),
                    OpenVPNService.humanReadableByteCount(diffOut / OpenVPNManagement.mBytecountInterval, true));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText(downloadSpeed + "\n" + downloadSession + "\n" + uploadSpeed + "\n" + uploadSession);
                }
            });
        }
    }

    @Override
    public void updateState(String state, final String logmessage, int localizedResId, VpnStatus.ConnectionStatus level) {
        if (level == VpnStatus.ConnectionStatus.LEVEL_CONNECTED) {
            tvTimeConnect.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (OpenVPNService.mConnecttime > 0)
                        connectionTime = OpenVPNService.mConnecttime;
                    else connectionTime = System.currentTimeMillis();

                    threadUpdateConnectionTime.start();
                }
            }, 50);

            firstData = false;
        } else if (level == VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED) {
            firstData = true;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText(logmessage);
                }
            });
        }
    }
}
