package org.rabix.engine;

import java.beans.PropertyVetoException;

import org.rabix.engine.db.BackendDB;
import org.rabix.engine.db.DAGNodeDB;
import org.rabix.engine.db.JobBackendService;
import org.rabix.engine.db.JobDB;
import org.rabix.engine.processor.EventProcessor;
import org.rabix.engine.processor.dispatcher.EventDispatcherFactory;
import org.rabix.engine.processor.handler.HandlerFactory;
import org.rabix.engine.processor.handler.impl.ContextStatusEventHandler;
import org.rabix.engine.processor.handler.impl.InitEventHandler;
import org.rabix.engine.processor.handler.impl.InputEventHandler;
import org.rabix.engine.processor.handler.impl.JobStatusEventHandler;
import org.rabix.engine.processor.handler.impl.OutputEventHandler;
import org.rabix.engine.processor.handler.impl.ScatterHandler;
import org.rabix.engine.processor.impl.MultiEventProcessorImpl;
import org.rabix.engine.service.ContextRecordService;
import org.rabix.engine.service.JobRecordService;
import org.rabix.engine.service.LinkRecordService;
import org.rabix.engine.service.VariableRecordService;
import org.rabix.engine.service.cache.generic.CacheService;
import org.rabix.engine.singleton.RepositoriesFactory;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.logging.SLF4JLog;

import com.github.mlk.guice.JdbiModule;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class EngineModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(JobDB.class).in(Scopes.SINGLETON);
    bind(BackendDB.class).in(Scopes.SINGLETON);
    bind(DAGNodeDB.class).in(Scopes.SINGLETON);
    
    bind(JobBackendService.class).in(Scopes.SINGLETON);
    
    bind(JobRecordService.class).in(Scopes.SINGLETON);
    bind(VariableRecordService.class).in(Scopes.SINGLETON);
    bind(LinkRecordService.class).in(Scopes.SINGLETON);
    bind(ContextRecordService.class).in(Scopes.SINGLETON);
    bind(JobBackendService.class).in(Scopes.SINGLETON);

    bind(ScatterHandler.class).in(Scopes.SINGLETON);
    bind(InitEventHandler.class).in(Scopes.SINGLETON);
    bind(InputEventHandler.class).in(Scopes.SINGLETON);
    bind(OutputEventHandler.class).in(Scopes.SINGLETON);
    bind(JobStatusEventHandler.class).in(Scopes.SINGLETON);
    bind(ContextStatusEventHandler.class).in(Scopes.SINGLETON);
    
    bind(HandlerFactory.class).in(Scopes.SINGLETON);
    bind(EventDispatcherFactory.class).in(Scopes.SINGLETON);
    bind(EventProcessor.class).to(MultiEventProcessorImpl.class).in(Scopes.SINGLETON);

    bind(RepositoriesFactory.class).in(Scopes.SINGLETON);
    bind(CacheService.class).in(Scopes.SINGLETON);
    
    install(JdbiModule.builder().scan("org.rabix.engine.dao").build());
//    Jdbc3PoolingDataSource source = new Jdbc3PoolingDataSource();
//    source.setDataSourceName("Data Source");
//    source.setServerName("localhost");
//    source.setDatabaseName("bunny");
//    source.setPortNumber(5433);
//    source.setUser("postgres");
//    source.setPassword("postgres");
//    source.setMaxConnections(20);
    
    ComboPooledDataSource ds = new ComboPooledDataSource();
    try {
      ds.setDriverClass("org.postgresql.Driver");
    } catch (PropertyVetoException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    ds.setUser("postgres");
    ds.setCheckoutTimeout(1000);
    ds.setPassword("postgres");
    ds.setJdbcUrl("jdbc:postgresql://localhost:5433/bunny?profileSQL=true");
   
    // the settings below are optional -- c3p0 can work with defaults
    ds.setMinPoolSize(5);
    ds.setAcquireIncrement(5);
    ds.setMaxPoolSize(20);
    ds.setMaxStatements(180);

    DBI dbi = new DBI(ds);
    dbi.setSQLLog(new SLF4JLog());
    
    bind(DBI.class).toInstance(dbi);
  }

}
