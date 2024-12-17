package edu.tyagelsky.financialscheduler;

import java.util.HashSet;
import java.util.Set;

public final class IDGenerator
{
    private static final Set<Long> occupiedIDset = new HashSet<>(50);
    private static final Set<Long> freeIDset = new HashSet<>(50);

    private static Long lastTakenID = 0L;

    public static long generateID()
    {
        final long IDtoGenerate;

        if(!freeIDset.isEmpty())
        {
            IDtoGenerate = freeIDset.iterator().next();
            freeIDset.remove(IDtoGenerate);
        }
        else
            IDtoGenerate = ++lastTakenID;

        occupiedIDset.add(IDtoGenerate);
        return IDtoGenerate;
    }

    public static void removeID(Long ID)
    {
        if(!occupiedIDset.contains(ID) || freeIDset.contains(ID))
            throw new RuntimeException();

        occupiedIDset.remove(ID);
        freeIDset.add(ID);
    }

}
