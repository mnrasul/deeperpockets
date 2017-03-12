package ca.rasul.config;

import ca.rasul.api.Networth;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(Networth.class);
    }

}