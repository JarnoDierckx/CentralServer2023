package com.fooditsolutions.webtest;

import com.fooditsolutions.web.bean.HandleLogin;
import org.junit.Test;
import org.mockito.Mockito;

import javax.faces.context.FacesContext;
import java.io.IOException;

//import static com.sun.deploy.security.DeployManifestChecker.verify;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TestLogin {

    @Test
    public void testPassCredentialsFailed() throws IOException {
        // Arrange
        HandleLogin instance = new HandleLogin();
        instance.setEmail("invalidEmail");
        instance.setPassword("invalidPassword");

        FacesContext mockfaces = Mockito.mock(FacesContext.class);
        Mockito.when(mockfaces);



        // Act
        instance.PassCredentials();

        // Assert
        assertEquals("Error: Username/Email and Password are incorrect!", instance.getResponseString());
    }

}
