package edu.tyagelsky.financialscheduler;

import java.util.ArrayList;
import java.util.List;

public class CurrentTransactionList
{
    private static int launchChecker = 0;

    private static final List<Transaction> transactions = new ArrayList<>(20);

    public static void launchList(final List<Transaction> trList)
    {
        if(launchChecker != 0)
        {
            transactions.clear();
            transactions.addAll(trList);
            launchChecker++;
        }
        else
            throw new RuntimeException();
    }

//    public static boolean addTransaction()


}
