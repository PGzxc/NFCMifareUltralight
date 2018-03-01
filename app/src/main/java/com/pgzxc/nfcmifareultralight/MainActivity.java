package com.pgzxc.nfcmifareultralight;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    private CheckBox mWriteData;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWriteData = (CheckBox) findViewById(R.id.checkbox_write);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null,
                    null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }
    @Override
    public void onNewIntent(Intent intent)
    {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String[] techList =tag.getTechList();
        boolean haveMifareUltralight = false;
        for(String tech: techList)
        {
            if(tech.indexOf("MifareUltralight") >= 0)
            {
                haveMifareUltralight = true;
                break;

            }
        }
        if(!haveMifareUltralight)
        {
            Toast.makeText(this, "不支持MifareUltralight数据格式", Toast.LENGTH_LONG).show();
            return;
        }
        if(mWriteData.isChecked())
        {
            writeTag(tag);
        }
        else {
            String data = readTag(tag);
            if(data != null)
                Toast.makeText(this, data, Toast.LENGTH_LONG).show();
        }


    }
    public void writeTag(Tag tag) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
            ultralight.writePage(4, "中国".getBytes(Charset.forName("GB2312")));
            ultralight.writePage(5, "美国".getBytes(Charset.forName("GB2312")));
            ultralight.writePage(6, "英国".getBytes(Charset.forName("GB2312")));
            ultralight.writePage(7, "法国".getBytes(Charset.forName("GB2312")));

            Toast.makeText(this, "成功写入MifareUltralight格式数据!", Toast.LENGTH_LONG)
                    .show();
        } catch (Exception e) {
            // TODO: handle exception
        }
        finally
        {
            try
            {
                ultralight.close();
            }
            catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public String readTag(Tag tag)
    {
        MifareUltralight ultralight = MifareUltralight.get(tag);

        try
        {
            ultralight.connect();
            byte[] data = ultralight.readPages(4);
            return new String(data, Charset.forName("GB2312"));
        }
        catch (Exception e) {
            // TODO: handle exception
        }
        finally
        {
            try
            {
                ultralight.close();
            }
            catch (Exception e) {
                // TODO: handle exception
            }
        }
        return null;
    }
}
