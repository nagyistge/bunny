package org.rabix.engine.rest.service.impl;

import org.rabix.engine.dao.Repositories;
import org.rabix.engine.dao.Repositories.TransactionException;
import org.rabix.engine.db.BackendDB;
import org.rabix.engine.rest.backend.BackendDispatcher;
import org.rabix.engine.rest.backend.stub.BackendStub;
import org.rabix.engine.rest.backend.stub.BackendStubFactory;
import org.rabix.engine.rest.service.BackendService;
import org.rabix.engine.rest.service.JobService;
import org.rabix.engine.singleton.RepositoriesFactory;
import org.rabix.transport.backend.Backend;
import org.rabix.transport.backend.BackendPopulator;
import org.rabix.transport.mechanism.TransportPluginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class BackendServiceImpl implements BackendService {

  private final static Logger logger = LoggerFactory.getLogger(BackendServiceImpl.class);
  
  private final BackendDB backendDB;
  private final JobService jobService;
  private final BackendPopulator backendPopulator;
  private final BackendDispatcher backendDispatcher;
  private final BackendStubFactory backendStubFactory;
  private final RepositoriesFactory repositoriesFactory;
  
  @Inject
  public BackendServiceImpl(JobService jobService, BackendPopulator backendPopulator, BackendStubFactory backendStubFactory, BackendDB backendDB, BackendDispatcher backendDispatcher, RepositoriesFactory repositoriesFactory) {
    this.backendDB = backendDB;
    this.jobService = jobService;
    this.backendPopulator = backendPopulator;
    this.backendDispatcher = backendDispatcher;
    this.backendStubFactory = backendStubFactory;
    this.repositoriesFactory = repositoriesFactory;
  }
  
  @Override
  public <T extends Backend> T create(T backendP) throws TransportPluginException {
    try {
      return (T) repositoriesFactory.getRepositories().<Backend>doInTransaction(new Repositories.TransactionCallback<Backend>() {
        @Override
        public Backend call() throws TransactionException {
          Backend backend = backendPopulator.populate(backendP);
          backendDB.add(backend);
          
          BackendStub<?, ?, ?> backendStub = null;
          try {
            backendStub = backendStubFactory.create(jobService, backend);
          } catch (TransportPluginException e) {
            e.printStackTrace();
            System.exit(-1);
          }
          backendDispatcher.addBackendStub(backendStub);
          
          logger.info("Backend {} registered.", backend.getId());
          return backend;
        }
      });
    } catch (TransactionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
//    backend = backendPopulator.populate(backend);
//    backendDB.add(backend);
//    
//    BackendStub<?, ?, ?> backendStub = backendStubFactory.create(jobService, backend);
//    backendDispatcher.addBackendStub(backendStub);
//    
//    logger.info("Backend {} registered.", backend.getId());
//    return backend;
  }
  
}
