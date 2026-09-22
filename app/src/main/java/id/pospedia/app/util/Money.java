package id.pospedia.app.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class Money {
    private static final NumberFormat IDR=NumberFormat.getCurrencyInstance(new Locale("id","ID"));
    public static String idr(long value){
        String s=IDR.format(value);
        return s.replace(",00","").replace("Rp","Rp ");
    }
    private Money(){}
}
