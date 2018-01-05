package quote.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** JAX-RS Application that is used to find all resources */
public class QuoteApplication extends RestApplication {

  private final Set<Class<?>> classes;

  public QuoteApplication() {
    HashSet<Class<?>> c = new HashSet<>();
    c.add(QuoteApi.class);
    c.add(WebApplicationExceptionMapper.class);
    classes = Collections.unmodifiableSet(c);
  }

  @Override
  public Set<Class<?>> getClasses() {
    return classes;
  }
}
