// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles;

import java.util.Iterator;
import java.util.List;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Activation;

public class ProfilesConversionUtils
{
    private ProfilesConversionUtils() {
    }
    
    public static org.apache.maven.model.Profile convertFromProfileXmlProfile(final Profile profileXmlProfile) {
        final org.apache.maven.model.Profile profile = new org.apache.maven.model.Profile();
        profile.setId(profileXmlProfile.getId());
        profile.setSource("profiles.xml");
        final org.apache.maven.profiles.Activation profileActivation = profileXmlProfile.getActivation();
        if (profileActivation != null) {
            final Activation activation = new Activation();
            activation.setActiveByDefault(profileActivation.isActiveByDefault());
            activation.setJdk(profileActivation.getJdk());
            final org.apache.maven.profiles.ActivationProperty profileProp = profileActivation.getProperty();
            if (profileProp != null) {
                final ActivationProperty prop = new ActivationProperty();
                prop.setName(profileProp.getName());
                prop.setValue(profileProp.getValue());
                activation.setProperty(prop);
            }
            final org.apache.maven.profiles.ActivationOS profileOs = profileActivation.getOs();
            if (profileOs != null) {
                final ActivationOS os = new ActivationOS();
                os.setArch(profileOs.getArch());
                os.setFamily(profileOs.getFamily());
                os.setName(profileOs.getName());
                os.setVersion(profileOs.getVersion());
                activation.setOs(os);
            }
            final org.apache.maven.profiles.ActivationFile profileFile = profileActivation.getFile();
            if (profileFile != null) {
                final ActivationFile file = new ActivationFile();
                file.setExists(profileFile.getExists());
                file.setMissing(profileFile.getMissing());
                activation.setFile(file);
            }
            profile.setActivation(activation);
        }
        profile.setProperties(profileXmlProfile.getProperties());
        final List repos = profileXmlProfile.getRepositories();
        if (repos != null) {
            final Iterator it = repos.iterator();
            while (it.hasNext()) {
                profile.addRepository(convertFromProfileXmlRepository(it.next()));
            }
        }
        final List pluginRepos = profileXmlProfile.getPluginRepositories();
        if (pluginRepos != null) {
            final Iterator it2 = pluginRepos.iterator();
            while (it2.hasNext()) {
                profile.addPluginRepository(convertFromProfileXmlRepository(it2.next()));
            }
        }
        return profile;
    }
    
    private static org.apache.maven.model.Repository convertFromProfileXmlRepository(final Repository profileXmlRepo) {
        final org.apache.maven.model.Repository repo = new org.apache.maven.model.Repository();
        repo.setId(profileXmlRepo.getId());
        repo.setLayout(profileXmlRepo.getLayout());
        repo.setName(profileXmlRepo.getName());
        repo.setUrl(profileXmlRepo.getUrl());
        if (profileXmlRepo.getSnapshots() != null) {
            repo.setSnapshots(convertRepositoryPolicy(profileXmlRepo.getSnapshots()));
        }
        if (profileXmlRepo.getReleases() != null) {
            repo.setReleases(convertRepositoryPolicy(profileXmlRepo.getReleases()));
        }
        return repo;
    }
    
    private static org.apache.maven.model.RepositoryPolicy convertRepositoryPolicy(final RepositoryPolicy profileXmlRepo) {
        final org.apache.maven.model.RepositoryPolicy policy = new org.apache.maven.model.RepositoryPolicy();
        policy.setEnabled(profileXmlRepo.isEnabled());
        policy.setUpdatePolicy(profileXmlRepo.getUpdatePolicy());
        policy.setChecksumPolicy(profileXmlRepo.getChecksumPolicy());
        return policy;
    }
}
