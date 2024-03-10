package com.example.einzelbeispiel;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private final String serverName = "se2-submission.aau.at";
    private final int serverPort = 20080;

    private EditText editTxt;
    private TextView txt;
    private Button btn, btn2;

    String matNr;
    String messageReceived = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editTxt = findViewById(R.id.editTextNumber);

        txt = findViewById(R.id.textView);

        btn = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matNr = editTxt.getText().toString();
                new NetworkCallTask().execute(matNr);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matNr = editTxt.getText().toString();
                result(matNr);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }//onCreate

    public void result(String matNr){
        int sum = calculate(matNr);
        String parity = "ungerade";

        if (sum % 2 == 0){
            parity = "gerade";
        }

        txt.setText("Summe: " + sum + "\nDiese Zahl ist " + parity + ".");
    }//result

    public int calculate(String matNr){
        int sum = 0;

        for (int i = 0; i <= matNr.length()-1; i++){
            int nr = Character.getNumericValue(matNr.charAt(i));

            if(i % 2 == 0){
                sum = sum + nr;
            }else{
                sum = sum - nr;
            }
        }

        return sum;
    }//calculate

    public class NetworkCallTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                Socket socket = new Socket(serverName, serverPort);

                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.writeBytes(strings[0] + "\n");
                out.flush();

                messageReceived = in.readLine();
                socket.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return messageReceived;
        }

        protected void onPostExecute(String messageReceived) {
            txt.setText(messageReceived);
        }

    }//NetworkCallTask
}//main