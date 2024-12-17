package edu.tyagelsky.financialscheduler.exceptions;

import edu.tyagelsky.financialscheduler.TransactionCategoryFactory;

public class CategoryAlreadyExistsException extends Exception
{
    public CategoryAlreadyExistsException(final TransactionCategoryFactory.TransactionCategory transactionCategory)
    {
        super(transactionCategory.getName() + " is already exists as an " + transactionCategory.getTypeOfOperation() + " category");;
    }
}
