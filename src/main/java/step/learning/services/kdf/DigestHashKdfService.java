package step.learning.services.kdf;

import com.google.inject.Inject;
import step.learning.services.hash.HashService;

import javax.inject.Named;

public class DigestHashKdfService implements KdfService{
    private final HashService hashService;

    @Inject
    public DigestHashKdfService(@Named("Digest-Hash") HashService hashService) {
        this.hashService = hashService;
    }

    @Override
    public String getDerivedKey(String password, String salt) {
        return hashService.hash( salt + password + salt );
    }
}
