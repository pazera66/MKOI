package com.example.pazera.mkoi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private Button bbsButton, rsaButton, statisticTestButton, chiTestButton, bmButton, mcTestButton;
    View view;
    EditText p_edittext, q_edittext, seed_edittext;
    BigInteger one = BigInteger.valueOf(1L);
    BigInteger two = BigInteger.valueOf(2L);
    BigInteger three = BigInteger.valueOf(3L);
    BigInteger four = BigInteger.valueOf(4L);
    BigInteger seed = BigInteger.valueOf(3);
    BigInteger p;
    BigInteger q;
    String chosenTest;
    //BigInteger e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bbsButton = (Button) findViewById(R.id.bbsButton);
        rsaButton = (Button) findViewById(R.id.rsaButton);
        statisticTestButton = (Button) findViewById(R.id.statisticTestButton);
        chiTestButton = (Button) findViewById(R.id.chiTestButton);
        bmButton = (Button) findViewById(R.id.bmButton);
        mcTestButton = (Button) findViewById(R.id.mcTestButton);

        bbsButton.setOnClickListener(bbsButtonListener);
        rsaButton.setOnClickListener(rsaButtonListener);
        statisticTestButton.setOnClickListener(statisticTestButtonListener);
        chiTestButton.setOnClickListener(chiTestButtonListener);
        bmButton.setOnClickListener(bmButtonListener);
        mcTestButton.setOnClickListener(mcTestButtonListener);


    }

    private void prepBBS(){
        podajDane();
    }

    // generateBBS true - default, false - user input

    private void generateBBS(boolean bool) {

        StringBuffer result = new StringBuffer();

        int bits = 32;
        Random random = new Random();
        BigInteger M = null;

        if (bool == true) {
            do {
                p = new BigInteger(bits, 100, random);
            }
            while (!p.mod(four).equals(three));

            do {
                do {
                    q = new BigInteger(bits, 100, random);
                    BigInteger test = q.mod(four);
                } while (!q.mod(four).equals(three));
            } while (p == q);

            M = q.multiply(p);

            do {
                seed = BigInteger.valueOf(random.nextInt());
            } while (!(M.gcd(seed) == one));
        }

        if (bool == false) {
            M = q.multiply(p);
        }

        int limit = 31001;
        for (int i = 1; i < limit; i++){
            seed = (seed.multiply(seed)).mod(M);
            if (seed.intValue() % 2 == 1){
                result.append("1");
            } else result.append("0");
            if (i % 8 == 0 && i != limit-1) {
                result.append(":");
            };
        }



        String temp = result.toString();
        String[] temp2 = temp.split(":", (limit - 1) - 1);

        result = new StringBuffer();

        for (int i = 0; i < temp2.length; i++) {
            result.append(Integer.parseInt(temp2[i], 2));
            if (i != temp2.length -1 ){
                result.append(",");
            }
        }


        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {


            File dir = new File ("/storage/sdcard1" + "/MKOI");
            if(!dir.exists()){
            dir.mkdirs();
            }

            File file = new File(dir, "BBS" + ".txt" );

            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.append(result.toString());
                fileWriter.close();
                //Toast.makeText(MainActivity.this, "Sukces", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        Toast.makeText(MainActivity.this, "Ciąg znaków został zapisany do pliku BBS.txt", Toast.LENGTH_SHORT).show();
    }

    private void podajDane() {

        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        view = inflater.inflate(R.layout.bbs_data_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        p_edittext = (EditText) view.findViewById(R.id.primeP_edittext);
        q_edittext = (EditText) view.findViewById(R.id.primeQ_edittext);
        seed_edittext = (EditText) view.findViewById(R.id.seed_edittext);

        builder.setPositiveButton("Wprowadź", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (p_edittext.getText().toString() == "" || q_edittext.getText().toString() == "" || seed_edittext.getText().toString() == "") {
                    Toast.makeText(MainActivity.this, "Podaj wszystkie dane", Toast.LENGTH_SHORT).show();
                    podajDane();
                } else {
                    p = BigInteger.valueOf(Long.parseLong(p_edittext.getText().toString()));
                    q = BigInteger.valueOf(Long.parseLong(q_edittext.getText().toString()));
                    seed = BigInteger.valueOf(Long.parseLong(seed_edittext.getText().toString()));
                    verifyData();
                }
            }
        });

       /* builder.setNegativeButton("Domyślne", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Losuje liczby P, Q i ziarno", Toast.LENGTH_SHORT).show();
                generateBBS(true);
            }
        });*/

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void verifyData() {

            if (!p.isProbablePrime(100)) {
                Toast.makeText(MainActivity.this, "Liczba P nie jest pierwsza", Toast.LENGTH_SHORT).show();
                podajDane();
                return;

            } else if (!p.mod(four).equals(three)) {
               Toast.makeText(MainActivity.this, "P mod 4 nie rowna sie 3", Toast.LENGTH_SHORT).show();
                podajDane();
                return;
            }



            if (!q.isProbablePrime(100)) {
                Toast.makeText(MainActivity.this, "Liczba Q nie jest pierwsza", Toast.LENGTH_SHORT).show();
                podajDane();
                return;

            } else if (!q.mod(four).equals(three)) {
                Toast.makeText(MainActivity.this, "Q mod 4 nie rowna sie 3", Toast.LENGTH_SHORT).show();
                podajDane();
                return;
            }


        BigInteger temp_M = p.multiply(q);
        temp_M = temp_M.gcd(seed);
        if (!temp_M.equals(one)) {
            Toast.makeText(MainActivity.this, "Ziarno nie jest względnie pierwsze do M", Toast.LENGTH_SHORT).show();
            podajDane();
            return;
        }

        generateBBS(false);
    }



    private void prepRSA() {
        podajDaneRSA();
    }

    private void podajDaneRSA() {
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        view = inflater.inflate(R.layout.bbs_data_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        p_edittext = (EditText) view.findViewById(R.id.primeP_edittext);
        q_edittext = (EditText) view.findViewById(R.id.primeQ_edittext);
        seed_edittext = (EditText) view.findViewById(R.id.seed_edittext);

        builder.setPositiveButton("Wprowadź", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (p_edittext.getText().toString() == "" || q_edittext.getText().toString() == "" || seed_edittext.getText().toString() == "") {
                    Toast.makeText(MainActivity.this, "Podaj wszystkie dane", Toast.LENGTH_SHORT).show();
                    podajDaneRSA();
                } else {
                    p = BigInteger.valueOf(Long.parseLong(p_edittext.getText().toString()));
                    q = BigInteger.valueOf(Long.parseLong(q_edittext.getText().toString()));
                    seed = BigInteger.valueOf(Long.parseLong(seed_edittext.getText().toString()));
                    verifyDataRSA();
                }
            }
        });

        builder.setNegativeButton("Domyślne", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Losuje liczby P, Q i ziarno", Toast.LENGTH_SHORT).show();
                generateBBS(true);
            }
        });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void verifyDataRSA() {

            if (!p.isProbablePrime(100)) {
                Toast.makeText(MainActivity.this, "Liczba P nie jest pierwsza", Toast.LENGTH_SHORT).show();
                podajDaneRSA();
                return;
            }



            if (!q.isProbablePrime(100)) {
                Toast.makeText(MainActivity.this, "Liczba Q nie jest pierwsza", Toast.LENGTH_SHORT).show();
                podajDaneRSA();
                return;
            }


        BigInteger temp_N = p.multiply(q);
        if (seed.compareTo(temp_N) >= 0) {
            Toast.makeText(MainActivity.this, "Ziarno musi byc mniejsze od N", Toast.LENGTH_SHORT).show();
            podajDaneRSA();
            return;
        }

        generateRSA();

    }

    private void generateRSA() {
        StringBuffer result = new StringBuffer();

        Random random = new Random();
        BigInteger N = p.multiply(q);
        BigInteger eli;
        BigInteger temp3 = p.subtract(one).multiply(q.subtract(one));
        do {
            eli = new BigInteger(24, random);
            //eli = BigInteger.valueOf(17);
        } while (!(eli.gcd(temp3).equals(one)));

        int limit = 31001;
        for (int i = 1; i < limit; i++){
            seed = (seed.modPow(eli, N));
            if (seed.mod(two).equals(one)){
                result.append("1");
            } else result.append("0");
            if (i % 8 == 0 && i != limit-1) {
                result.append(":");
            };
        }

        String temp = result.toString();
        String[] temp2 = temp.split(":", (limit - 1) - 1);
        result = new StringBuffer();

        for (int i = 0; i < temp2.length; i++) {
            result.append(Integer.parseInt(temp2[i], 2));
            if (i != temp2.length -1 ){
                result.append(",");
            }
        }

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {


            File dir = new File ("/storage/sdcard1" + "/MKOI");
            if(!dir.exists()){
                dir.mkdirs();
            }

            File file = new File(dir, "RSA" + ".txt" );

            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.append(result.toString());
                fileWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        Toast.makeText(MainActivity.this, "Ciąg znaków został zapisany do pliku RSA.txt", Toast.LENGTH_SHORT).show();
    }

    private void prepBM(){
        podajDaneBM();
    }

    private void podajDaneBM() {
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        view = inflater.inflate(R.layout.bbs_data_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        p_edittext = (EditText) view.findViewById(R.id.primeP_edittext);
        q_edittext = (EditText) view.findViewById(R.id.primeQ_edittext);
        seed_edittext = (EditText) view.findViewById(R.id.seed_edittext);

        builder.setPositiveButton("Wprowadź", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (p_edittext.getText().toString() == "" || q_edittext.getText().toString() == "" || seed_edittext.getText().toString() == "") {
                    Toast.makeText(MainActivity.this, "Podaj wszystkie dane", Toast.LENGTH_SHORT).show();
                    podajDane();
                } else {
                    p = BigInteger.valueOf(Long.parseLong(p_edittext.getText().toString()));
                    q = BigInteger.valueOf(Long.parseLong(q_edittext.getText().toString()));
                    seed = BigInteger.valueOf(Long.parseLong(seed_edittext.getText().toString()));
                    verifyDataBM();
                }
            }
        });


        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void verifyDataBM() {
        if (!p.isProbablePrime(100)) {
            Toast.makeText(MainActivity.this, "Liczba P nie jest pierwsza", Toast.LENGTH_SHORT).show();
            podajDaneRSA();
            return;
        }



        if (!q.isProbablePrime(100)) {
            Toast.makeText(MainActivity.this, "Liczba Q nie jest pierwsza", Toast.LENGTH_SHORT).show();
            podajDaneRSA();
            return;
        }

        generateBM();
    }

    private void generateBM() {
        StringBuffer result = new StringBuffer();

        int limit = 31001;
        for (int i = 1; i < limit; i++){
            seed = (q.modPow(seed, p));
            if (seed.compareTo((p.subtract(one).divide(two))) == -1) {
                result.append("1");
            } else {result.append("0");}

            if (i % 8 == 0 && i != limit-1) {
                result.append(":");
            }
        }

        String temp = result.toString();
        String[] temp2 = temp.split(":", (limit - 1) - 1);
        result = new StringBuffer();

        for (int i = 0; i < temp2.length; i++) {
            result.append(Integer.parseInt(temp2[i], 2));
            if (i != temp2.length -1 ){
                result.append(",");
            }
        }

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {


            File dir = new File ("/storage/sdcard1" + "/MKOI");
            if(!dir.exists()){
                dir.mkdirs();
            }

            File file = new File(dir, "BM" + ".txt" );

            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.append(result.toString());
                fileWriter.close();
                //Toast.makeText(MainActivity.this, "Sukces", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        Toast.makeText(MainActivity.this, "Ciąg znaków został zapisany do pliku BM.txt", Toast.LENGTH_SHORT).show();
    }


    private void loadData(String test, String alg){
        File file = new File("/storage/sdcard1/MKOI/" + alg);
        String out = "";
        try {
            InputStream in = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder outstring = new StringBuilder();
            try {
                String outputStream = reader.readLine();
                outstring.append(outputStream);
                out = outstring.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String[] temp = out.split(",");
        int[] intData = new int[temp.length];
        for (int i = 0; i < temp.length; i++){
            intData[i] = Integer.parseInt(temp[i]);
        }

        switch (test) {
            case "stat": statisticTest(intData);
                break;
            case "chi": chiTest(intData);
                break;
            case "MC": MCTest(intData);
        }


    }


    private void statisticTest(int[] data) {
        int n = data.length-1;
        String referenceMean = String.valueOf(Math.round(new Double((255 - 0) / 2) * 100) / 100);
        String referenceVariance = String.valueOf(Math.round(new Double((Math.pow(255, 2))/12) * 100) / 100);
        String referenceStandardDeviance = String.valueOf(Math.round(new Double(255/(2*Math.sqrt(3))) * 100 ) / 100);
        String referenceMeanDeviance = String.valueOf(Math.round(new Double(255/4) * 100 ) / 100);

        Double mean = 0.0;// = Math.round(() / n) * 100 / 100;

        for (int i = 0; i < n; i++) {
            mean = mean + data[i];
        }
        mean = mean / n;
        String meanStr = String.valueOf(Math.round(mean * 100) / 100);

        Double variance = 0.0;

        for (int i = 0; i < n; i++) {
            variance = variance + Math.pow((data[i] - mean), 2);
        }
        variance = variance / (n-1);
        String varianceStr = String.valueOf(Math.round(variance * 100) / 100);

        Double stdDeviance = Math.sqrt(variance);
        String stdDevianceStr = String.valueOf(Math.round(stdDeviance * 100) / 100);

        Double meanDeviance = 0.0;

        for (int i = 0; i < n; i++) {
            meanDeviance = meanDeviance + Math.abs(data[i] - mean);
        }
        meanDeviance = meanDeviance / n;

        String meanDevianceStr = String.valueOf(Math.round(meanDeviance * 100) / 100);

        showResultsStat(referenceMean, referenceMeanDeviance, referenceStandardDeviance, referenceVariance, meanStr, varianceStr, meanDevianceStr, stdDevianceStr);
    }


    private void chiTest(int[] data) {

        int[] rozklad = new int[data.length];

        int test = 0;
        for (int i = 0; i < data.length; i++) {
            test = test + data[i];
        }
        for (int i = 0; i < data.length-1; i++) {
            rozklad[data[i]]++;
        }

            //double chi = () / (data.length * (data.length / rozklad[i]));
            double n_r = data.length / 255;
            double chiSquare = 0;

            for (int i = 0; i < rozklad.length; i++) {
                int f = rozklad[i] - (int) n_r;
                int f2 = f*f;
                chiSquare = chiSquare + (f2/n_r);
            }


        showResultsChi(chiSquare);
    }

    private void showResultsChi(double chi_square) {

        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        view = inflater.inflate(R.layout.test_chi_results, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);

        TextView chi_factor = (TextView) view.findViewById(R.id.chi_factor);
        chi_factor.append(String.valueOf((int) chi_square));





        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });


        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void askForData(String test) {
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        view = inflater.inflate(R.layout.test_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        chosenTest = test;

        builder.setPositiveButton("BBS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadData(chosenTest, "BBS.txt");
            }
        });


        builder.setNegativeButton("Blum-Mikali", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadData(chosenTest, "BM.txt");
            }
        });

        builder.setNeutralButton("RSA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadData(chosenTest, "RSA.txt");
            }
        });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void chooseStatisticTest() {
        askForData("stat");
    }


    private void chooseChiTest(){
        askForData("chi");
    }

    private void chooseMCTest(){
        askForData("MC");
    }

    private void showResultsStat(String referenceMean, String referenceMeanDeviance, String referenceStandardDeviance, String referenceVariance, String meanStr,
                                 String varianceStr, String meanDevianceStr, String stdDevianceStr) {


        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        view = inflater.inflate(R.layout.test_results, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);

        TextView meanTextview = (TextView) view.findViewById(R.id.meanTextview);
        TextView meanRefTextView = (TextView) view.findViewById(R.id.meanRefTextView);
        TextView varianceTextView = (TextView) view.findViewById(R.id.varianceTextView);
        TextView varianceRef = (TextView) view.findViewById(R.id.varianceRef);
        TextView devianceStdTextview = (TextView) view.findViewById(R.id.devianceStdTextview);
        TextView devianceStdRef = (TextView) view.findViewById(R.id.devianceStdRef);
        TextView devianceMeanTextview = (TextView) view.findViewById(R.id.devianceMeanTextview);
        TextView devianceMeanRef = (TextView) view.findViewById(R.id.devianceMeanRef);

        meanTextview.append(meanStr);
        meanRefTextView.append(referenceMean);
        varianceTextView.append(varianceStr);
        varianceRef.append(referenceVariance);
        devianceStdTextview.append(stdDevianceStr);
        devianceStdRef.append(referenceStandardDeviance);
        devianceMeanTextview.append(meanDevianceStr);
        devianceMeanRef.append(referenceMeanDeviance);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });


        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void MCTest(int[] data){
        int r2 = 255*255;
        int cir = 0;
        int sqr = data.length;
        double x = 0;
        double y = 0;

        for (int i = 0; i < data.length-1; i++) {
            if (x == 0) {
                x = ((double) data[i]/255)*2-1;
                continue;
            }
            y = ((double) data[i]/255)*2-1;
            if (Math.sqrt(Math.pow((0-x), 2) + Math.pow((0-y), 2)) <= 1) {
                cir++;
            }
        }
        double idealPI = Math.PI;
        double calculatedPI = (4*cir)/(double) sqr;

        showResultsMC(idealPI, calculatedPI);

    }

    private void showResultsMC(double idealPI, double calculatedPI) {

        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        view = inflater.inflate(R.layout.test_mc_results, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);

        TextView pi_ref = (TextView) view.findViewById(R.id.pi_ref);
        TextView calc_pi =  (TextView) view.findViewById(R.id.calcpi_result);

        pi_ref.append(String.valueOf(idealPI));
        calc_pi.append(String.valueOf(calculatedPI));


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });


        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }


    View.OnClickListener rsaButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            prepRSA();
        }
    };

    View.OnClickListener bbsButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            prepBBS();
        }
    };

    View.OnClickListener statisticTestButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            chooseStatisticTest();
        }
    };

    View.OnClickListener chiTestButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            chooseChiTest();
        }
    };

    View.OnClickListener bmButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            prepBM();
        }
    };

    View.OnClickListener mcTestButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            chooseMCTest();
        }
    };

}
