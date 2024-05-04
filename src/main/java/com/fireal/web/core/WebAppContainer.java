package com.fireal.web.core;

import fireal.core.DataAccessContainer;

public class WebAppContainer extends DataAccessContainer{
    public WebAppContainer() {
        super();
    }

    public WebAppContainer(Class<?> configClass, boolean autoStart) {
        super(configClass, autoStart);
    }

    public WebAppContainer(Class<?> configClass) {
        super(configClass);
    }

}
