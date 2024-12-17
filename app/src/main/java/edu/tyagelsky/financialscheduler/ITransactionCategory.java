package edu.tyagelsky.financialscheduler;

import androidx.annotation.NonNull;

public interface ITransactionCategory
{
    String getName();
    TypeOfOperation getTypeOfOperation();
}
