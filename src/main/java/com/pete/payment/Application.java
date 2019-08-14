package com.pete.payment;

import com.pete.payment.app.PaymentApplication;
import com.pete.payment.dataload.DataLoader;

public class Application {
    public static void main(String [] args) {
        if (args.length == 1) {
            if (args[0].equals("dataload")) {
                DataLoader.main(args);
                return;
            } else if (args[0].equals("webapp")) {
                PaymentApplication.main(args);
                return;
            }
        }

        System.err.println("Please specify a single argument: dataload or webapp");
    }
}
