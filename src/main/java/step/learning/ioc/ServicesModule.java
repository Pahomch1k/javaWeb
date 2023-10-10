package step.learning.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import step.learning.services.hash.HashService;
import step.learning.services.hash.Md5HashService;
import step.learning.services.hash.Sha256HashService;
import step.learning.services.random.RandomService;
import step.learning.services.random.RandomServiceV1;

public class ServicesModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HashService.class).annotatedWith(Names.named("Digest-Hash")).to(Md5HashService.class);
        bind(HashService.class).annotatedWith(Names.named("DSA_HAsh")).to(Sha256HashService.class);
    }

    private RandomService randomService;
    @Provides
    private RandomService InjectRandomService(){
        if (randomService == null){
            randomService = new RandomServiceV1();
            randomService.seed("0");
        }
        return randomService;
    }
}
