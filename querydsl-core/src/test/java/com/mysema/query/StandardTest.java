package com.mysema.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import com.mysema.query.types.expr.EBoolean;
import com.mysema.query.types.expr.ECollection;
import com.mysema.query.types.expr.EDate;
import com.mysema.query.types.expr.EDateTime;
import com.mysema.query.types.expr.EList;
import com.mysema.query.types.expr.EMap;
import com.mysema.query.types.expr.ENumber;
import com.mysema.query.types.expr.EString;
import com.mysema.query.types.expr.ETime;
import com.mysema.query.types.expr.Expr;

/**
 * @author tiwe
 *
 */
public abstract class StandardTest {
    
    private int total;
    
    private final List<String> failures = new ArrayList<String>();
    
    private final List<String> errors = new ArrayList<String>();
    
    private boolean runFilters = true, runProjections = true;
    
    private final StandardTestData testData;
    
    public StandardTest(){
        this(new StandardTestData());
    }
    
    public StandardTest(StandardTestData data){
        this.testData = data;
    }
    
    public void booleanTests(EBoolean expr, EBoolean other){
        execute(testData.booleanFilters(expr, other), false);
    }
    
    public <A> void collectionTests(ECollection<A> expr, ECollection<A> other, A knownElement){
        execute(testData.collectionMatchingFilters(expr, other, knownElement), true);
        execute(testData.collectionFilters(expr, other, knownElement), false);
        execute(testData.collectionProjections(expr, other, knownElement));
    }
    
    public <A extends Comparable> void dateTests(EDate<A> expr, EDate<A> other, A knownValue){
//        execute(testData.dateMatchingFilters(expr, other, knownValue), true);
//        execute(testData.dateFilters(expr, other, knownValue), false);
//        execute(testData.dateProjections(expr, other, knownValue));
    }
    
    public <A extends Comparable> void timeTests(ETime<A> expr, ETime<A> other, A knownValue){
        // TODO
    }
    
    public <A extends Comparable> void dateTimeTests(EDateTime<A> expr, EDateTime<A> other, A knownValue){
        execute(testData.dateTimeMatchingFilters(expr, other, knownValue), true);
        execute(testData.dateTimeFilters(expr, other, knownValue), false);
        execute(testData.dateTimeProjections(expr, other, knownValue));   
    }
    
    public <A> void listTests(EList<A> expr, EList<A> other, A knownElement){
        execute(testData.listMatchingFilters(expr, other, knownElement), true);
        execute(testData.listFilters(expr, other, knownElement), false);
        execute(testData.listProjections(expr, other, knownElement));
    }
    
    public <K,V> void mapTests(EMap<K,V> expr, EMap<K,V> other, K knownKey, V knownValue) {
        execute(testData.mapMatchingFilters(expr, other, knownKey, knownValue), true);
        execute(testData.mapFilters(expr, other, knownKey, knownValue), false);
        execute(testData.mapProjections(expr, other, knownKey, knownValue));
    }
    
    public <A extends Number & Comparable<A>> void numericCasts(ENumber<A> expr, ENumber<A> other, A knownValue){
        execute(testData.numericCasts(expr, other, knownValue));
    }
    
    public <A extends Number & Comparable<A>> void numericTests(ENumber<A> expr, ENumber<A> other, A knownValue){
        execute(testData.numericMatchingFilters(expr, other, knownValue), true);
        execute(testData.numericFilters(expr, other, knownValue), false);
        execute(testData.numericProjections(expr, other, knownValue));
    }
    
    public void stringTests(EString expr, EString other, String knownValue){
        execute(testData.stringMatchingFilters(expr, other, knownValue), true);
        execute(testData.stringFilters(expr, other, knownValue), false);
        execute(testData.stringProjections(expr, other, knownValue));
    }
    
    private void execute(Collection<EBoolean> filters, boolean matching){
        if (this.runFilters){
            for (EBoolean f : filters){
                total++;
                try{
                    System.err.println(f);
                    int results = executeFilter(f);
                    System.err.println();
                    if (matching && results == 0){
                        failures.add(f + " failed");
                    }    
                }catch(Throwable t){
                    t.printStackTrace();
                    errors.add(f + " failed : " + t.getMessage());
                }            
            }    
        }        
    }
    
    private void execute(Collection<? extends Expr<?>> projections){
        if (this.runProjections){
            for (Expr<?> pr : projections){
                total++;
                try{
                    System.err.println(pr);
                    executeProjection(pr);
                    System.err.println();
                }catch(Throwable t){
                    t.printStackTrace();
                    errors.add(pr + " failed : " + t.getMessage());
                }            
            }    
        }        
    }

    public abstract int executeFilter(EBoolean f);

    public abstract int executeProjection(Expr<?> pr);

    public void report() {
        if (!failures.isEmpty() || !errors.isEmpty()){
            System.err.println(failures.size() + " failures");
            for (String f : failures){
                System.err.println(f);
            }
            System.err.println(errors.size() + " errors");
            for (String e : errors){
                System.err.println(e);
            }
            StringBuffer buffer = new StringBuffer("Failed with ");
            if (!failures.isEmpty()){
                buffer.append(failures.size()).append(" failure(s) ");
                if (!errors.isEmpty()){
                    buffer.append("and ");
                }
            }
            if (!errors.isEmpty()){
                buffer.append(errors.size()).append(" error(s) ");
            }
            buffer.append("of ").append(total).append(" tests");            
            Assert.fail(buffer.toString());
        }else{
            System.out.println("Success with " + total + " tests");
        }        
    }

    public StandardTest noFilters() {
        runFilters = false;        
        return this;
    }       
    
    public StandardTest noProjections() {
        runProjections = false;
        return this;
    }  

}
