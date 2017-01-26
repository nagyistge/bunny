package org.rabix.engine.singleton;

import org.rabix.engine.dao.Repositories;

import com.google.inject.Inject;

public class RepositoriesFactory {

  private Repositories repositories;
  
  @Inject
  public RepositoriesFactory(Repositories repositories) {
    this.repositories = repositories;
  }
  
  public Repositories getRepositories() {
    return repositories;
  }
}
