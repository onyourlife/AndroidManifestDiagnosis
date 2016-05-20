package kr.ac.hanyang.infosec.checkrootpermission;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Process p;

        try {
            p = Runtime.getRuntime().exec("su");

            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes("echo \"Do I have root?\" > /system/sd/temp.txt\n");

            dos.writeBytes("exit\n");
            dos.flush();

            try {
                p.waitFor();
                if(p.exitValue() != 255){
                    Toast.makeText(MainActivity.this, "Root", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Not Root", Toast.LENGTH_SHORT).show();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Not Root", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Not Root", Toast.LENGTH_SHORT).show();
        }


    }
}
