package net.cerberus.challengeBackend.api;

import net.cerberus.challengeBackend.api.v1.FactApi;
import net.cerberus.challengeBackend.api.v1.PatchApi;
import net.cerberus.challengeBackend.api.v1.RunesApi;
import net.cerberus.challengeBackend.io.logs.LogLevel;
import net.cerberus.challengeBackend.io.logs.LogReason;
import net.cerberus.challengeBackend.io.logs.Logger;
import spark.Request;

import static spark.Spark.init;
import static spark.Spark.port;

public class API {

    public API() {
    }

    public void initialize() {
        port(this.getHerokuAssignedPort());
        init();
        Logger.logMessage("API has started.", LogLevel.INFO, LogReason.API);
    }

    private int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            Logger.logMessage("Running on port: " + processBuilder.environment().get("PORT"), LogLevel.INFO, LogReason.API);
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 80;
    }

    public void startV1() {
        new RunesApi();
        new PatchApi();
        new FactApi();
    }

    protected boolean isConnectionAllowed(Request request) {
        if (!request.host().equals("localhost") && !request.host().equals("vent-projects.de")) {
            /* TODO disable cross site access */
            //return halt(401, "Unauthorized");
            //return false;
            return true;
        }
        return true;
    }
}
