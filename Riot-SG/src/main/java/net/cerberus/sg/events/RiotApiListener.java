package net.cerberus.sg.events;

import net.cerberus.riotApi.events.EventAdapter;
import net.cerberus.riotApi.events.eventClasses.ApiCallExceptionEvent;
import net.cerberus.sg.logger.LogLevel;
import net.cerberus.sg.logger.LogReason;
import net.cerberus.sg.logger.Logger;

public class RiotApiListener extends EventAdapter {

    @Override
    public void onApiCallException(ApiCallExceptionEvent event) {
        Logger.logMessage("A riot api exception occurred: " + event.getResponseCode() + "(" + event.getRequestURL() + ")", LogLevel.WARNING, LogReason.RIOT_API_EXCEPTION_HANDLER);
    }
}
