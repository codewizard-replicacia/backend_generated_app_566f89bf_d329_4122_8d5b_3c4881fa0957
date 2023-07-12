package com.mycompany.group234.function;

import com.mycompany.group234.model.FrontendScreen;
import com.mycompany.group234.model.FrontendApp;
import com.sap.olingo.jpa.metadata.core.edm.annotation.EdmAction;
import com.sap.olingo.jpa.metadata.core.edm.annotation.EdmParameter;
import com.sap.olingo.jpa.metadata.core.edm.mapper.extension.ODataAction;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;

@Component
public class JavaActions implements ODataAction {
    private final EntityManager entityManager;

    public JavaActions(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

	@EdmAction(name="LinkFrontendAppWithFrontendScreen", isBound = false)
    public boolean linkFrontendAppWithFrontendScreen(@EdmParameter(name = "AppId")final long appId, @EdmParameter(name = "FeScreenId") int feScreenId) {
        try {
            // Find frontendApp object by appId
            FrontendApp frontendApp = entityManager.find(FrontendApp.class, appId);
            if (frontendApp == null) {
                LOGGER.warn("FrontendApp with AppId not found.");
                return false;
            }
            // Get frontendScreens by feScreenId
            List<FrontendScreen> frontendScreens = frontendApp.getFrontendScreens();
            FrontendScreen frontendScreen = entityManager.find(FrontendScreen.class, feScreenId);
            if (frontendScreen == null) {
                LOGGER.warn("FrontendScreen with feScreenId not found.");
                return false;
            }
            //Associate frontendApp with frontendScreen
            frontendScreens.add(frontendScreen);
            frontendApp.setFrontendScreens(frontendScreens);
            entityManager.getTransaction().begin();
            entityManager.merge(frontendApp);
            entityManager.getTransaction().commit();
            return true;
        } catch (Exception e) {
            LOGGER.error("Error in linkFrontendAppWithFrontendScreen", e);
            return false;
        }
    }
	
}
  