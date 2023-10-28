package step.learning.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import step.learning.dao.UserDao;
import step.learning.services.db.DbProvider;
import step.learning.services.db.PlanetDbProvider;
import step.learning.services.formparse.FormParseService;
import step.learning.services.formparse.MixedFormParseService;
import step.learning.services.hash.HashService;
import step.learning.services.hash.Md5HashService;
import step.learning.services.hash.Sha256HashService;
import step.learning.services.kdf.DigestHashKdfService;
import step.learning.services.kdf.KdfService;
import step.learning.services.random.RandomService;
import step.learning.services.random.RandomServiceV1;

import java.util.Date;

public class ServicesModule extends AbstractModule {



    @Override
    protected void configure() {
        bind( HashService.class ).annotatedWith(Names.named( "Digest-Hash" )).to( Md5HashService.class );
        bind( HashService.class ).annotatedWith(Names.named( "DSA_HAsh" )).to( Sha256HashService.class );
        bind( FormParseService.class ).to( MixedFormParseService.class );
        bind( DbProvider.class ).to( PlanetDbProvider.class );
        bind( KdfService.class ).to( DigestHashKdfService.class );

        bind( String.class ).annotatedWith(Names.named( "db-prefix" )).toInstance( "java201_" );
    }

    private RandomService randomService;
    @Provides
    private RandomService InjectRandomService(){
        if (randomService == null){
            randomService = new RandomServiceV1();
            randomService.seed(String.valueOf( new Date().getTime() ));
        }
        return randomService;
    }
}
