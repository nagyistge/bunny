package org.rabix.engine.dao;

import org.skife.jdbi.v2.sqlobject.Transaction;

import com.google.inject.Singleton;

@Singleton
public class TransactionHelper {

  @Transaction
  public <R> R call(TransactionCallback<R> callback) throws TransactionException {
    return callback.call();
  }
  
  public static interface TransactionCallback<R> {
    R call() throws TransactionException;
  }
  
  public static class TransactionException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -2785803795422959954L;
    
    public TransactionException(Throwable t) {
      super(t);
    }
    
  }
  
}
