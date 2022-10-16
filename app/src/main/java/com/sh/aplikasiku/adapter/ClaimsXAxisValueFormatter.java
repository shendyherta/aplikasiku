package com.sh.aplikasiku.adapter;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClaimsXAxisValueFormatter extends ValueFormatter {

    //fungsi untuk menampilkan label secara urut berdasarkan tanggal data dibuat
    List<String> datesList;

    //constructor untuk menerima data list tanggal dan memasukkannya ke variabel global datesList
    public ClaimsXAxisValueFormatter(List<String> arrayOfDates) {
        this.datesList = arrayOfDates;
    }

    //fungsi untuk mendapatkan posisi label
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
    /*
    Depends on the position number on the X axis, we need to display the label, Here, this is the logic
    to convert the float value to integer so that I can get the value from array based on that integer
    and can convert it to the required value here, month and date as value. This is required for my
    data to show properly, you can customize according to your needs.
    */
        //merubah value dari float ke angka bulat
        Integer position = Math.round(value);
        //membuat simple date format dengan pattern dd-MM-yyyy
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        //melakukan perulangan untuk mencari posisi yang berada di antara value > i+1 dan value < i+2
        for (int i = 0; i < datesList.size(); i++) {
            if (value > i+1 && value <i+2) {
                //jika iya, isi variabel position dengan nilai i
                position = i;
                break;
            }
        }

        //jika position kurang dari ukuran list tanggal, kembalikan dengan format milisecond
        if (position < datesList.size())
            return sdf.format(new Date((getDateInMilliSeconds(datesList.get(position)))));
        return "";
    }

    //fungsi untuk merubah data tanggal ke miliseconds
    private long getDateInMilliSeconds(String givenDateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        long timeInMilliseconds = 1;
        try {
            Date mDate = sdf.parse(givenDateString);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }
}

