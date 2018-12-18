package com.example.emam.rayatransportation;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Route {


    private static ArrayList<LatLng> R3_6th_Of_October = new ArrayList<>();


    private static LatLng PickuppointR3_1 = new LatLng(29.9726403, 31.090503);
    private static LatLng PickuppointR3_2 = new LatLng(29.951876, 31.088178);
    private static LatLng PickuppointR3_3 = new LatLng(29.964844, 31.034833);
    private static LatLng PickuppointR3_4 = new LatLng(29.958455, 30.943198);
    private static LatLng PickuppointR3_5 = new LatLng(29.951124, 30.911907);
    private static LatLng PickuppointR3_6 = new LatLng(29.963618, 30.963618);
    private static LatLng PickuppointR3_7 = new LatLng(29.969886, 30.915744);
    private static LatLng PickuppointR3_8 = new LatLng(29.976092, 30.932739);
    private static LatLng PickuppointR3_9 = new LatLng(29.952217, 30.952217);
    private static LatLng PickuppointR3_10 = new LatLng(29.973935, 30.943512);
    private static LatLng Pickuppoint_office = new LatLng(29.972285, 30.950589);

    public static LatLng getPickuppointR3_1() {
        return PickuppointR3_1;
    }

    public static LatLng getPickuppointR3_2() {
        return PickuppointR3_2;
    }

    public static LatLng getPickuppointR3_3() {
        return PickuppointR3_3;
    }

    public static LatLng getPickuppointR3_4() {
        return PickuppointR3_4;
    }

    public static LatLng getPickuppointR3_5() {
        return PickuppointR3_5;
    }

    public static LatLng getPickuppointR3_6() {
        return PickuppointR3_6;
    }

    public static LatLng getPickuppointR3_7() {
        return PickuppointR3_7;
    }

    public static LatLng getPickuppointR3_8() {
        return PickuppointR3_8;
    }

    public static LatLng getPickuppointR3_9() {
        return PickuppointR3_9;
    }

    public static LatLng getPickuppointR3_10() {
        return PickuppointR3_10;
    }

    public static LatLng getPickuppoint_office() {
        return Pickuppoint_office;
    }

    public static ArrayList<LatLng> getRout(Context context, String stratPoint, String endPoint) {


        if (endPoint.equals(context.getResources().getStringArray(R.array.DriverRout)[4])) {
            if (stratPoint.equals(context.getResources().getStringArray(R.array.DriverRout)[3])) {

                return new Route().getR3_6th_Of_OctobertoOffice();
            }

        }

        if (stratPoint.equals(context.getResources().getStringArray(R.array.DriverRout)[4])) {
            if (endPoint.equals(context.getResources().getStringArray(R.array.DriverRout)[3])) {

                return new Route().getOfficetoR3_6th_Of_October();
            }

        }


        return new Route().getR3_6th_Of_OctobertoOffice();

    }

    public static LatLng getPoint(Context context, String employeepickuppoint) {


        if (employeepickuppoint.equals(context.getResources().getStringArray(R.array.DriverRout)[4])) {
            return getPickuppoint_office();
        }


        String[] separated = employeepickuppoint.split(" ");

        String frist = separated[0];
        String secound = separated[1];

        Log.v("ROUTCLASS", "DATA" + frist);
        Log.v("ROUTCLASS", "DATA" + secound);


        if (secound.equals("R3")) {
            if (frist.equals("P1")) {
                return getPickuppointR3_1();
            }
            if (frist.equals("P2")) {
                return getPickuppointR3_2();
            }
            if (frist.equals("P3")) {
                return getPickuppointR3_3();
            }
            if (frist.equals("P4")) {
                return getPickuppointR3_4();
            }
            if (frist.equals("P5")) {
                return getPickuppointR3_5();
            }
            if (frist.equals("P6")) {
                return getPickuppointR3_6();
            }
            if (frist.equals("P7")) {
                return getPickuppointR3_7();
            }
            if (frist.equals("P8")) {
                return getPickuppointR3_8();
            }
            if (frist.equals("P9")) {
                return getPickuppointR3_9();
            }
            if (frist.equals("P10")) {
                return getPickuppointR3_10();
            }

        }

        return null;
    }

    public ArrayList<LatLng> getR3_6th_Of_OctobertoOffice() {
        setR3_6th_Of_OctobertoOffice(R3_6th_Of_October);
        return R3_6th_Of_October;
    }

    private void setR3_6th_Of_OctobertoOffice(ArrayList<LatLng> r3_6th_Of_October) {

        r3_6th_Of_October.add(PickuppointR3_1);
        r3_6th_Of_October.add(PickuppointR3_2);
        r3_6th_Of_October.add(PickuppointR3_3);
        r3_6th_Of_October.add(PickuppointR3_4);
        r3_6th_Of_October.add(PickuppointR3_5);
        r3_6th_Of_October.add(PickuppointR3_6);
        r3_6th_Of_October.add(PickuppointR3_7);
        r3_6th_Of_October.add(PickuppointR3_8);
        r3_6th_Of_October.add(PickuppointR3_9);
        r3_6th_Of_October.add(PickuppointR3_10);
        r3_6th_Of_October.add(Pickuppoint_office);
    }

    public ArrayList<LatLng> getOfficetoR3_6th_Of_October() {
        setOfficetoR3_6th_Of_October(R3_6th_Of_October);
        return R3_6th_Of_October;
    }

    private void setOfficetoR3_6th_Of_October(ArrayList<LatLng> r3_6th_Of_October) {

        r3_6th_Of_October.add(Pickuppoint_office);
        r3_6th_Of_October.add(PickuppointR3_10);
        r3_6th_Of_October.add(PickuppointR3_9);
        r3_6th_Of_October.add(PickuppointR3_8);
        r3_6th_Of_October.add(PickuppointR3_7);
        r3_6th_Of_October.add(PickuppointR3_6);
        r3_6th_Of_October.add(PickuppointR3_5);
        r3_6th_Of_October.add(PickuppointR3_4);
        r3_6th_Of_October.add(PickuppointR3_3);
        r3_6th_Of_October.add(PickuppointR3_2);
        r3_6th_Of_October.add(PickuppointR3_1);
    }
}



