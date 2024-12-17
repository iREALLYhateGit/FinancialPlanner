package edu.tyagelsky.financialscheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CurrentTransactionList
{
    private static int launchChecker = 0;

    private static final Map<Long,Transaction> transactions = new HashMap<>(20);

    public static void launchList(final List<Transaction> trList)
    {
        if(launchChecker == 0)
        {
            transactions.clear();
            trList.forEach(transaction -> transactions.putIfAbsent(transaction.getObjectID(),transaction));
            launchChecker++;
        }
        else
            throw new RuntimeException();
    }

    public static Optional<Transaction> obtainTrByID(final Long trID)
    {
        return Optional.ofNullable(transactions.get(trID));
    }

    public static boolean checkPresence(final Long trID)
    {
        return transactions.containsKey(trID);
    }


}
