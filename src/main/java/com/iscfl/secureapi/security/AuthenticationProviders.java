package com.iscfl.secureapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthenticationProviders {

    private ConcurrentHashMap<String, AuthenticationProvider> providers;

    public AuthenticationProviders(){
        this(new ConcurrentHashMap<>());
    }

    public AuthenticationProviders(ConcurrentHashMap<String, AuthenticationProvider> providers){
        this.providers = providers;
    }

    public List<AuthenticationProvider> getProviders(List<String> names) {
        List<AuthenticationProvider> providerList = new ArrayList<>();
        for(String name: names){
            providerList.add(getProvider(name));
        }
        return providerList;
    }

    public ConcurrentHashMap<String, AuthenticationProvider> getProviders() {
        return providers;
    }

    public void setProviders(ConcurrentHashMap<String, AuthenticationProvider> providers) {
        this.providers = providers;
    }

    public void addProvider(String name, AuthenticationProvider provider){
        getProviders().put(name, provider);
    }

    public AuthenticationProvider getProvider(String name){
        return getProviders().get(name);
    }
}
