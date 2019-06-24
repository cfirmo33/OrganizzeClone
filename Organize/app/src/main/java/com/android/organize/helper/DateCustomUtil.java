package com.android.organize.helper;

import java.text.SimpleDateFormat;

public class DateCustomUtil {

    public static String getDataAtual() {

        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataString  = simpleDateFormat.format(data);

        return dataString;
    }

    public static String mesAnoDataEscolhida(String data){

        String retornoData[] = data.split("/");
        String mes = retornoData[1];//mes 01
        String ano = retornoData[2];//ano 2018

        String mesAno = mes + ano;
        return mesAno;

    }
}
