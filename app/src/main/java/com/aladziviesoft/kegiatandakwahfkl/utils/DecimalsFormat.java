package com.aladziviesoft.kegiatandakwahfkl.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


public class DecimalsFormat {

    static String[] angkaTerbilang = {"", "Satu", "Dua", "Tiga", "Empat", "Lima", "Enam", "Tujuh", "Delapan", "Sembilan", "Sepuluh", "Sebelas"};

    public static String priceWithDecimal(String price) {

        String result = "";

        try {

            Double ps = Double.parseDouble(price);
            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            kursIndonesia.setDecimalFormatSymbols(formatRp);

            System.out.printf("Harga Rupiah: %s %n", kursIndonesia.format(ps));

            result = kursIndonesia.format(ps);

        } catch (Exception e) {

        }

        return result;
    }


    public static String priceWithDecimalOld(String price) {

        String result = price;

        try {

            Double doubleFromString = Double.parseDouble(price);
            DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
            result = formatter.format(doubleFromString);

        } catch (Exception e) {

        }

        return result;
    }


    public static String priceWithoutDecimal(String price) {

        String result = price;

        try {

            Double doubleFromString = Double.parseDouble(price);
            DecimalFormat formatter = new DecimalFormat("###,###,###.#");
            result = formatter.format(doubleFromString);
        } catch (Exception e) {

        }

        return result;
    }


    public static String angkaToTerbilang(Long angka) {
        if (angka < 12)
            return angkaTerbilang[angka.intValue()];
        if (angka >= 12 && angka <= 19)
            return angkaTerbilang[angka.intValue() % 10] + " Belas";
        if (angka >= 20 && angka <= 99)
            return angkaToTerbilang(angka / 10) + " Puluh " + angkaTerbilang[angka.intValue() % 10];
        if (angka >= 100 && angka <= 199)
            return "Seratus " + angkaToTerbilang(angka % 100);
        if (angka >= 200 && angka <= 999)
            return angkaToTerbilang(angka / 100) + " Ratus " + angkaToTerbilang(angka % 100);
        if (angka >= 1000 && angka <= 1999)
            return "Seribu " + angkaToTerbilang(angka % 1000);
        if (angka >= 2000 && angka <= 999999)
            return angkaToTerbilang(angka / 1000) + " Ribu " + angkaToTerbilang(angka % 1000);
        if (angka >= 1000000 && angka <= 999999999)
            return angkaToTerbilang(angka / 1000000) + " Juta " + angkaToTerbilang(angka % 1000000);
        if (angka >= 1000000000 && angka <= 999999999999L)
            return angkaToTerbilang(angka / 1000000000) + " Milyar " + angkaToTerbilang(angka % 1000000000);
        if (angka >= 1000000000000L && angka <= 999999999999999L)
            return angkaToTerbilang(angka / 1000000000000L) + " Triliun " + angkaToTerbilang(angka % 1000000000000L);
        if (angka >= 1000000000000000L && angka <= 999999999999999999L)
            return angkaToTerbilang(angka / 1000000000000000L) + " Quadrilyun " + angkaToTerbilang(angka % 1000000000000000L);
        return "";
    }

    /*public static String priceToString(Double price) {
        String toShow = priceWithoutDecimal(price);
        if (toShow.indexOf(".") > 0) {
            return priceWithDecimal(price);
        } else {
            return priceWithoutDecimal(price);
        }
    }*/
}
