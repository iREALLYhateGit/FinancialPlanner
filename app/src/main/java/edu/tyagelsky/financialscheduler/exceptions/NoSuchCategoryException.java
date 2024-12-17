package edu.tyagelsky.financialscheduler.exceptions;

import edu.tyagelsky.financialscheduler.TransactionCategoryFactory;

public class NoSuchCategoryException extends Exception
{
    public NoSuchCategoryException(final TransactionCategoryFactory.TransactionCategory transactionCategory)
    {
        super("Category" + transactionCategory.getName() + " with typeOfOperation \""
                + transactionCategory.getTypeOfOperation() +  "\" does not exist.");
    }
}
