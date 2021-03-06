package org.wso2.sample.unique.claim.user.store.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.user.core.common.User;
import org.wso2.carbon.user.core.jdbc.UniqueIDJDBCUserStoreManager;
import org.wso2.carbon.user.core.profile.ProfileConfigurationManager;

import java.util.List;
import java.util.Map;

public class UniqueClaimUserStore extends UniqueIDJDBCUserStoreManager {

    private static Log log = LogFactory.getLog(UniqueClaimUserStore.class);
    private String UNIQUE_CLAIM = " ";

    public UniqueClaimUserStore() {

    }

    public UniqueClaimUserStore(RealmConfiguration realmConfig, int tenantId) throws UserStoreException {

        super(realmConfig, tenantId);
    }

    public UniqueClaimUserStore(RealmConfiguration realmConfig, Map<String, Object> properties,
                                ClaimManager claimManager, ProfileConfigurationManager profileManager, UserRealm realm, Integer tenantId)
            throws UserStoreException {


        super(realmConfig, properties, claimManager, profileManager, realm, tenantId);
        log.info("CustomUserStoreManager initialized...");
    }

    public UniqueClaimUserStore(RealmConfiguration realmConfig, Map<String, Object> properties,
                                ClaimManager claimManager, ProfileConfigurationManager profileManager, UserRealm realm, Integer tenantId,
                                boolean skipInitData) throws UserStoreException {

        super(realmConfig, properties, claimManager, profileManager, realm, tenantId, skipInitData);
        log.info("CustomUserStoreManager initialized...");
    }

    @Override
    protected void doSetUserClaimValueWithID(String userID, String claimURI, String claimValue, String profileName) throws UserStoreException {

        if (!claimURI.equalsIgnoreCase(UNIQUE_CLAIM)) {
            super.doSetUserClaimValueWithID(userID, claimURI, claimValue, profileName);
        } else {
            if (claimValue != null) {
                String property = null;
                try {
                    property = claimManager.getAttributeName(getMyDomainName(), UNIQUE_CLAIM);
                } catch (org.wso2.carbon.user.api.UserStoreException e) {
                    log.error("User Store Exception when fetching user Claims", e);
                }
                List<String> userIds = this.doGetUserListFromPropertiesWithID(property, claimValue, profileName);
                // Check whether any other users exist or not
                if (userIds.size() > 0) {
                    throw new UserStoreException("Unique constraint violation for " + UNIQUE_CLAIM);
                }
            }
            super.doSetUserClaimValueWithID(userID, claimURI, claimValue, profileName);
        }
    }

    @Override
    public void doSetUserClaimValuesWithID(String userID, Map<String, String> claims, String profileName) throws UserStoreException {

        String claimValue = claims.get(UNIQUE_CLAIM);
        if (claimValue != null) {
            String property = null;
            try {
                property = claimManager.getAttributeName(getMyDomainName(), UNIQUE_CLAIM);
            } catch (org.wso2.carbon.user.api.UserStoreException e) {
                log.error("User Store Exception when fetching user Claims", e);
            }
            List<String> userIds = this.doGetUserListFromPropertiesWithID(property, claimValue, profileName);
            // Check whether any other users exist or not
            if (userIds.size() > 0) {
                throw new UserStoreException("Unique constraint violation for " + UNIQUE_CLAIM);
            }
        }
        super.doSetUserClaimValuesWithID(userID, claims, profileName);
    }

    @Override
    public User doAddUserWithID(String userName, Object credential, String[] roleList, Map<String, String> claims, String profileName, boolean requirePasswordChange) throws UserStoreException {

        String claimValue = claims.get(UNIQUE_CLAIM);
        if (claimValue != null) {
            String property = null;
            try {
                property = claimManager.getAttributeName(getMyDomainName(), UNIQUE_CLAIM);
            } catch (org.wso2.carbon.user.api.UserStoreException e) {
                log.error("User Store Exception when fetching user Claims", e);
            }
            List<String> userIds = this.doGetUserListFromPropertiesWithID(property, claimValue, profileName);
            // Check whether any other users exist or not
            if (userIds.size() > 0) {
                throw new UserStoreException("Unique constraint violation for " + UNIQUE_CLAIM);
            }
        }
        return super.doAddUserWithID(userName, credential, roleList, claims, profileName, requirePasswordChange);
    }
}
