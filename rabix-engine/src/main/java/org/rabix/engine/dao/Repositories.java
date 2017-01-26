package org.rabix.engine.dao;

import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.Transaction;

public abstract class Repositories {

  @CreateSqlObject
  public abstract ApplicationRepository applicationRepository();
  
  @CreateSqlObject
  public abstract BackendRepository backendRepository();
  
  @CreateSqlObject
  public abstract DAGRepository dagRepository();
  
  @CreateSqlObject
  public abstract JobRepository jobRepository();
  
  @CreateSqlObject
  public abstract JobBackendRepository jobBackendRepository();
  
  @CreateSqlObject
  public abstract JobRecordRepository jobRecordRepository();
  
  @CreateSqlObject
  public abstract LinkRecordRepository linkRecordRepository();
  
  @CreateSqlObject
  public abstract VariableRecordRepository variableRecordRepository();
  
  @CreateSqlObject
  public abstract ContextRecordRepository contextRecordRepository();
  
  @Transaction
  public <T> T doInTransaction(TransactionCallback<T> callback) throws TransactionException {
    return callback.call();
  }
  
  public static interface TransactionCallback<T> {
    T call() throws TransactionException;
  }
  
  public static class TransactionException extends Exception {
    
    /**
     * 
     */
    private static final long serialVersionUID = 4463416980242373365L;

    public TransactionException(Throwable t) {
      super(t);
    }
    
  }
  
  
}
