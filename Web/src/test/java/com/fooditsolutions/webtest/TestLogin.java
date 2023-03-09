package com.fooditsolutions.webtest;

import com.fooditsolutions.web.HandleLogin;
import javafx.beans.value.ObservableBooleanValue;
import org.junit.Test;
import org.mockito.Mockito;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

//import static com.sun.deploy.security.DeployManifestChecker.verify;
import static javafx.beans.binding.Bindings.when;
import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

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
