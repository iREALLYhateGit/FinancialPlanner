package edu.tyagelsky.financialscheduler;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.lang.ref.Cleaner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Transaction implements Parcelable
{

    private static int launchChecker = 0;

    private static final Map<Long,Transaction> transactions = new HashMap<>(20);


    private static final Cleaner cleaner = Cleaner.create();
    private final Cleaner.Cleanable cleanable;

    private final Long objectID;
    private String trName;
    private TransactionCategoryFactory.TransactionCategory trCategory;
    private double trRate;
    private LocalDate trDate;

    private Transaction(String trName,
                        TransactionCategoryFactory.TransactionCategory trCategory,
                        double trRate,
                        LocalDate trDate)
    {
        this.objectID = IDGenerator.generateID();
        this.cleanable = cleaner.register(this, () -> IDGenerator.removeID(objectID));
        this.trName = trName;
        this.trCategory = trCategory;
        this.trRate = trRate;
        this.trDate = trDate;
    }

    private Transaction(Long objectID,
                        String trName,
                        TransactionCategoryFactory.TransactionCategory trCategory,
                        double trRate,
                        LocalDate trDate)
    {
        this.objectID = objectID;
        this.cleanable = cleaner.register(this, () -> IDGenerator.removeID(objectID));
        this.trName = trName;
        this.trCategory = trCategory;
        this.trRate = trRate;
        this.trDate = trDate;
    }

    public static Transaction createNewTransaction(String trName,
                                                TransactionCategoryFactory.TransactionCategory trCategory,
                                                double trRate,
                                                LocalDate trDate)
    {
        final Transaction transaction = new Transaction(trName, trCategory, trRate, trDate);
        transactions.put(transaction.getObjectID(), transaction);
        return transaction;
    }

    public static Transaction createTrueTransaction(Long trID,
                                                    String trName,
                                                   TransactionCategoryFactory.TransactionCategory trCategory,
                                                   double trRate,
                                                   LocalDate trDate)
    {
        final Transaction transaction;
        if(!transactions.containsKey(trID))
        {
            transaction = new Transaction(trID, trName, trCategory, trRate, trDate);
            transactions.put(transaction.getObjectID(), transaction);
        }
        else
            transaction = transactions.get(trID);
        return transaction;
    }

    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel sourceParcel)
        {
            final Long trObjectID = sourceParcel.readLong();

            return transactions.get(trObjectID);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public void updateName(final String opName)
    {
        this.trName = opName;
    }

    public void updateRate(final double opRate)
    {
        this.trRate = opRate;
    }

    public void updateCategory(final TransactionCategoryFactory.TransactionCategory opTransactionCategory)
    {
        this.trCategory = opTransactionCategory;
    }

    public void updateDate(final LocalDate date)
    {
        this.trDate = date;
    }

    @NonNull
    public String getTrName()
    {
        return trName;
    }

    @NonNull
    public TransactionCategoryFactory.TransactionCategory getTrCategory() {
        return trCategory;
    }

    public double getTrRate() {
        return trRate;
    }

    @NonNull
    public TypeOfOperation getTypeOfOperation() {
        return trCategory.getTypeOfOperation();
    }

    @NonNull
    public LocalDate getTrDate() {
        return trDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i)
    {
        parcel.writeLong(objectID);
        parcel.writeString(trName);
        parcel.writeString(trCategory.toString());
        parcel.writeDouble(trRate);
        parcel.writeString(trDate.toString());
    }

    public Long getObjectID() {
        return objectID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(objectID, that.objectID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(objectID);
    }

    public static void launchList(final List<Transaction> trList)
    {
        if (launchChecker == 0) {
            transactions.clear();
            trList.forEach(transaction -> transactions.putIfAbsent(transaction.getObjectID(), transaction));
            launchChecker++;
        }
    }
}
