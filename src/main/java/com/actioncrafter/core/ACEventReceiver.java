package com.actioncrafter.core;

import java.util.List;

public interface ACEventReceiver
{

    public void handleEvents(List<ACEvent> events);

}
