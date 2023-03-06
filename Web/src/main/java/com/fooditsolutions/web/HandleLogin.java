package com.fooditsolutions.web;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class takes the credentials from index.xhtml and passes them to CentralServer2023API
 */
public class HandleLogin {
    String email;
    String password;
    String responseString;
    int errorCount=0;
    int timeOut=5;
    boolean isTimedOut=false;

    /**
    *This function takes the credentials and puts them in a json string. That string gets send to CentralServer2023API, and after that PassCredentials recieves the session key provided the
     * credentials given match that of a registered user.
     */
    public void PassCredentials() throws IOException {
        if (!isTimedOut){
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

            if (email.trim().equals("") || password.trim().equals("")){
                responseString="Please provide username/email and password";
                return;
            }

            String POST_PARAMS = String.format("{\"email\": \"%s\",\"password\": \"%s\"}", email, password);

            URL url=new URL("http://localhost:8080/CentralServer2023API-1.0-SNAPSHOT/api/crud/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            System.out.println("POST Response Code :  " + responseCode);
            System.out.println("POST Response Message : " + connection.getResponseMessage());


            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Authentication successful, retrieve the session key from the API response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String apiResponse = in.readLine();
                in.close();

                if (!apiResponse.contains("Error")){
                    String name="Login";
                    //System.out.println(response);
                    Cookie cookie=new Cookie(name, apiResponse);
                    cookie.setDomain("localhost");
                    cookie.setMaxAge(60*60);
                    System.out.println("Cookie " + cookie);
                    response.addCookie(cookie);
                    responseString=connection.getResponseMessage();
                }else{
                    errorCount++;
                    responseString=apiResponse;
                    if (errorCount==3){
                        responseString="Too many failed attempts, login will be blocked for 1 minute";
                        TimeOut(1);
                    } else if (errorCount>3) {
                        responseString="Too many failed attempts, login will be blocked for " + timeOut + " minutes";
                        TimeOut(timeOut);
                        timeOut*=2;
                    }
                }

            } else {
                responseString=connection.getResponseMessage();
            }
        }
    }

    public void TimeOut(int minutes){
        isTimedOut=true;
        System.out.println("Timed out for " + minutes + " minutes.");
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                isTimedOut=false;
                System.out.println("Time out over.");
            }
        };
        timer.schedule(task,(long) minutes *60*1000);
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public boolean isTimedOut() {
        return isTimedOut;
    }

    public void setTimedOut(boolean timedOut) {
        isTimedOut = timedOut;
    }
}
