package edu.tyagelsky.financialscheduler;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.tyagelsky.financialscheduler.exceptions.CategoryAlreadyExistsException;
import edu.tyagelsky.financialscheduler.exceptions.CategoryParsingException;

public final class TransactionCategoryFactory
{
    private static int launchChecker = 0;

    public static class TransactionCategory implements ITransactionCategory
    {

        @NotNull
        private final String categoryName;
        @NotNull
        private final TypeOfOperation typeOfOperation;

        private TransactionCategory(@NotNull String categoryName,
                                    @NotNull TypeOfOperation typeOfOperation)
        {
            this.categoryName = categoryName;
            this.typeOfOperation = typeOfOperation;
        }

        @Override
        public String getName() {
            return categoryName;
        }

        @Override
        @NonNull
        public TypeOfOperation getTypeOfOperation() {
            return typeOfOperation;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TransactionCategory that = (TransactionCategory) o;
            return Objects.equals(categoryName, that.categoryName) && typeOfOperation == that.typeOfOperation;
        }

        @Override
        public int hashCode() {
            return Objects.hash(categoryName, typeOfOperation);
        }

        @NonNull
        @Override
        public String toString() {
            return "Category{" +
                    "categoryName='" + categoryName + '\'' +
                    ", typeOfOperation='" + typeOfOperation + '\'' +
                    '}';
        }

        public static TransactionCategory parseCategory(String strCategory)
        {
            final String[] categoryParts = strCategory.split("'", 5);

            final String possibleCatName = categoryParts[1];
            final TypeOfOperation possibleTypeOfOperation = TypeOfOperation.valueOf(categoryParts[3].toUpperCase(Locale.CANADA));

            final TransactionCategory possibleTransactionCategory = new TransactionCategory(possibleCatName,possibleTypeOfOperation);

            if(categories.contains(possibleTransactionCategory))
                return possibleTransactionCategory;
            else
                throw new CategoryParsingException();
        }
    }

    public static final TransactionCategory defaultCategoryForIncome = new TransactionCategory("etc",TypeOfOperation.INCOME);
    public static final TransactionCategory defaultCategoryForExpense = new TransactionCategory("etc",TypeOfOperation.EXPENSE);

    private final static List<TransactionCategory> defaultCategories = new ArrayList<>
            (
            List.of(new TransactionCategory("salary", TypeOfOperation.INCOME),
                    new TransactionCategory("bonus", TypeOfOperation.INCOME),
                    new TransactionCategory("underworking", TypeOfOperation.INCOME),
                    new TransactionCategory("donations", TypeOfOperation.INCOME),
                    new TransactionCategory("loans", TypeOfOperation.INCOME),
                    new TransactionCategory("goods", TypeOfOperation.EXPENSE),
                    new TransactionCategory("transport", TypeOfOperation.EXPENSE),
                    new TransactionCategory("subscriptions", TypeOfOperation.EXPENSE),
                    new TransactionCategory("debts", TypeOfOperation.EXPENSE),
                    new TransactionCategory("education", TypeOfOperation.EXPENSE),
                    new TransactionCategory("customization", TypeOfOperation.EXPENSE),
                    new TransactionCategory("vacation", TypeOfOperation.EXPENSE),
                    new TransactionCategory("pocket", TypeOfOperation.EXPENSE),
                    defaultCategoryForIncome,
                    defaultCategoryForExpense)
            );


    public final static List<TransactionCategory> categories = new ArrayList<>(20);

    public static TransactionCategory createCategory(final String categoryName,
                               final TypeOfOperation typeOfOperation)
            throws CategoryAlreadyExistsException
    {
        final TransactionCategory newTransactionCategory = new TransactionCategory(categoryName, typeOfOperation);
        if (!categories.contains(newTransactionCategory))
        {
            categories.add(newTransactionCategory);
            return newTransactionCategory;
        }
        else
            throw new CategoryAlreadyExistsException(newTransactionCategory);
    }

    public static TransactionCategory obtainCategory(final String categoryName,
                                                     final TypeOfOperation typeOfOperation)
    {
        final TransactionCategory newTransactionCategory = new TransactionCategory(categoryName, typeOfOperation);
        if (!categories.contains(newTransactionCategory))
            categories.add(newTransactionCategory);
        return newTransactionCategory;
    }

    public static Collection<TransactionCategory> getDefaultCategoriesUnmodifiableList()
    {
        return Collections.unmodifiableCollection(defaultCategories);
    }

    public static List<TransactionCategory> getCategoriesByTypeOfOperation(final TypeOfOperation typeOfOperation)
    {
        return Collections.unmodifiableList(categories.stream().
                filter(category -> category.getTypeOfOperation().equals(typeOfOperation)).
                collect(Collectors.toList()));
    }

    public static void launchList(final List<TransactionCategory> catList)
    {
        if (launchChecker == 0)
        {
            categories.clear();
            categories.addAll(defaultCategories);
            categories.addAll(catList);
            launchChecker++;
        }
    }


    public static class CategoryIconCode
    {
        private final static HashMap<String, Integer> iconCodes = new HashMap<>();

        static
        {
            iconCodes.put("salary", R.drawable.ic_category_salary);
            iconCodes.put("bonus", R.drawable.ic_category_bonus);
            iconCodes.put("underworking", R.drawable.ic_category_underworking);
            iconCodes.put("donations", R.drawable.ic_category_donations);
            iconCodes.put("loans", R.drawable.ic_category_loans);
            iconCodes.put("goods", R.drawable.ic_category_goods);
            iconCodes.put("transport", R.drawable.ic_category_transport);
            iconCodes.put("subscriptions", R.drawable.ic_category_subscriptions);
            iconCodes.put("debts", R.drawable.ic_category_debts);
            iconCodes.put("education", R.drawable.ic_category_education);
            iconCodes.put("customization", R.drawable.ic_category_customization);
            iconCodes.put("vacation", R.drawable.ic_category_vacation);
            iconCodes.put("pocket", R.drawable.ic_category_pocket);
            iconCodes.put("etc", R.drawable.ic_category_etc);
        }

        public static void setIcon(final ImageView imageView, final TransactionCategory transactionCategory)
        {
            if(iconCodes.containsKey(transactionCategory.getName()))
            {
                imageView.setImageResource(iconCodes.get(transactionCategory.getName()));
            }
            else{
                imageView.setImageResource(iconCodes.get("etc"));
            }
        }
    }
}
