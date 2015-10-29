package com.octo.softshake.crunch.stock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pke on 06.10.15.
 */
public class SmartUtil {

    public static Date parse(String date) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String format(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }


    public static Delta fromStock(String stock) {
        String[] fields = stock.split(",");
        assert fields.length == 7;
        Delta mvt = new Delta();
        mvt.marque = fields[6];
        mvt.article = Integer.valueOf(fields[5]);
        mvt.magasin = Integer.valueOf(fields[1]);
        mvt.typeStock = Integer.valueOf(fields[2]);
        mvt.date = fields[4];
        mvt.delta = Integer.valueOf(fields[0]);
        return mvt;
    }

    public static Delta fromMouvement(String mvtStr) {
        String[] fields = mvtStr.split(",");
        assert fields.length == 9;
        Delta mvt = new Delta();
        mvt.marque = fields[0];
        mvt.article = Integer.valueOf(fields[3]);
        mvt.magasin = Integer.valueOf(fields[2]);
        mvt.typeStock = Integer.valueOf(fields[4]);
        mvt.date = fields[1];
        mvt.delta = Integer.valueOf(fields[5]);

        return mvt;
    }
//    enseigne, codemagasin, codeinternearticle, stockdate, (qte + total_delta) as qte, codecptstockmag, CurrentTime() as date_maj;
    public static String toStock(Delta mvtStr) {
        String date = mvtStr.date;
        return "" + mvtStr.delta + ',' + mvtStr.magasin + ',' + mvtStr.typeStock + ",," + date + ',' +mvtStr.article +',' +mvtStr.marque;
    }

}
