package edu.tyagelsky.financialscheduler;

import androidx.annotation.NonNull;

public enum TypeOfOperation
{
    INCOME, EXPENSE;

    @NonNull
    @Override
    public String toString() {
        return this.name();
    }
}
