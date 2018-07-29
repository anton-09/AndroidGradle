package ru.home.serial.entity;

public class AuthCredentials
{
    private String apikey;
    private String username;
    private String userkey;

    public AuthCredentials(String apikey, String username, String userkey)
    {
        this.apikey = apikey;
        this.username = username;
        this.userkey = userkey;
    }

    public String getApikey()
    {
        return apikey;
    }

    public void setApikey(String apikey)
    {
        this.apikey = apikey;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUserkey()
    {
        return userkey;
    }

    public void setUserkey(String userkey)
    {
        this.userkey = userkey;
    }
}