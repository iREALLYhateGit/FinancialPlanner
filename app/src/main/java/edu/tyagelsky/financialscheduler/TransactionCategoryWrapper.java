package edu.tyagelsky.financialscheduler;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransactionCategoryWrapper implements ITransactionCategory
{
    private final TransactionCategoryFactory.TransactionCategory transactionCategory;

    public TransactionCategoryWrapper(TransactionCategoryFactory.TransactionCategory transactionCategory)
    {
        this.transactionCategory = transactionCategory;
    }

    public static List<TransactionCategoryWrapper> convertFromCategoryList(final List<TransactionCategoryFactory.TransactionCategory> transactionCategoryList)
    {
        return Collections.unmodifiableList(transactionCategoryList.stream().map(TransactionCategoryWrapper::new).collect(Collectors.toList()));
    }

    public TransactionCategoryFactory.TransactionCategory getTrCategory() {
        return transactionCategory;
    }

    @Override
    public String getName() {
        return transactionCategory.getName();
    }

    @Override
    public TypeOfOperation getTypeOfOperation() {
        return transactionCategory.getTypeOfOperation();
    }

    @Override
    public String toString() {
        return transactionCategory.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionCategoryWrapper that = (TransactionCategoryWrapper) o;
        return Objects.equals(transactionCategory, that.transactionCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(transactionCategory);
    }
}
