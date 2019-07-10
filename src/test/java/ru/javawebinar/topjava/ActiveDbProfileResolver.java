package ru.javawebinar.topjava;

import org.springframework.test.context.ActiveProfilesResolver;

//http://stackoverflow.com/questions/23871255/spring-profiles-simple-example-of-activeprofilesresolver
public class ActiveDbProfileResolver implements ActiveProfilesResolver {

    @Override
    public String[] resolve(Class<?> aClass) {
        String className = aClass.getSimpleName();
        String repoProfile = null;
        if (className.endsWith("DataJpaTest")) {
            repoProfile = "datajpa";
        } else if (className.endsWith("JpaTest")) {
            repoProfile = "jpa";
        } else if (className.endsWith("JdbcTest")) {
            repoProfile = "jdbc";
        }
        String dbProfile = Profiles.getActiveDbProfile();

        if (dbProfile != null && repoProfile != null) {
            return new String[]{dbProfile, repoProfile};
        } else {
            if (dbProfile == null && repoProfile == null) {
                return new String[0];
            } else {
                return new String[]{dbProfile == null ? repoProfile : dbProfile};
            }
        }
    }
}