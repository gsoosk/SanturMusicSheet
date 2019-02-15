package far.zad.gsoosk.musiccomposer.Stream;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import far.zad.gsoosk.musiccomposer.MainActivity;
import far.zad.gsoosk.musiccomposer.R;

public class BluetoothActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 504;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice mBTDevice;
    public ArrayList<BluetoothDevice> mPBTDevices = new ArrayList<>();
    public ArrayList<BluetoothDevice> mDBTDevices = new ArrayList<>();
    public BluetoothConnectionService connectionService;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private String TAG = "BLUETOOTH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);



        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null)
        {
            Toast.makeText(this ,R.string.bluetooth_not_supported,
                    Toast.LENGTH_LONG).show();
            return ;
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else
        {
            handleBluetooth();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT)
        {
            if(resultCode == RESULT_OK)
            {
                handleBluetooth();
            }
            else if(resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this ,"nok",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
    public void handleBluetooth()
    {
        connectionService = new BluetoothConnectionService(this);
        showPairedDevices();
        showDiscovarableDevices();
        setDiscoverBtn();

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(pairingBroadcast, filter);
    }
    public void showPairedDevices()
    {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.paired_devices_layout);


        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                mPBTDevices.add(device);
                addNewPairedDevice(deviceName, deviceHardwareAddress, device);
            }
        }
    }
    public void addNewPairedDevice(String deviceName, String deviceMac, final BluetoothDevice device )
    {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.paired_devices_layout);
        Button newDevice = new Button(this);
        newDevice.setText(deviceName + ":" + deviceMac);
        newDevice.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        newDevice.setHeight(0);
        TypedValue outValue = new TypedValue();
        getApplicationContext().getTheme().resolveAttribute(
                android.R.attr.selectableItemBackground, outValue, true);
        newDevice.setBackgroundResource(outValue.resourceId);

        newDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleItemsClicked(device);
            }
        });

        linearLayout.addView(newDevice);
    }
    public void showDiscovarableDevices()
    {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.discover_layout);
        linearLayout.removeAllViews();
        mDBTDevices = new ArrayList<>();

        if(bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkBTPermissions();
        }
        bluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                mDBTDevices.add(device);
                addNewDiscoverdDevice(deviceName, deviceHardwareAddress, device);
            }

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }
    }
    public void setDiscoverBtn()
    {
        ImageButton btn = (ImageButton) findViewById(R.id.discover_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDiscovarableDevices();
            }
        });
    }
    public void addNewDiscoverdDevice(String deviceName, String deviceMac, final BluetoothDevice device)
    {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.discover_layout);
        Button newDevice = new Button(this);
        newDevice.setText(deviceName + ":" + deviceMac);
        newDevice.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        newDevice.setHeight(0);
        TypedValue outValue = new TypedValue();
        getApplicationContext().getTheme().resolveAttribute(
                android.R.attr.selectableItemBackground, outValue, true);
        newDevice.setBackgroundResource(outValue.resourceId);

        newDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleItemsClicked(device);
            }
        });

        linearLayout.addView(newDevice);
    }
    private void  handleItemsClicked(BluetoothDevice device)
    {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + device.getName());
            if(device.getBondState() == BluetoothDevice.BOND_BONDED)
            {

                mBTDevice = device;
                connect();
            }
            else
            {
                device.createBond();
            }


        }
    }

    private final BroadcastReceiver pairingBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    mBTDevice = mDevice;
                    connect();
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };
    public void ok()
    {
        Toast.makeText(getBaseContext(), "Connected!",
                Toast.LENGTH_LONG).show();
    }
    public void connect()
    {
        ParcelUuid[] uuids = mBTDevice.getUuids();
        // Ot you can use your own UUID if you have
        connectionService.setBlutoothListener(new BluetoothConnectionService.BluetoothListener() {
            @Override
            public void onConnected(Context context) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);


            }

            @Override
            public void canNotConnected() {

            }
        });
        connectionService.startClient(mBTDevice, uuids[0].getUuid());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(pairingBroadcast);
    }

}
