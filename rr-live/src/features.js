var features = {
    core: {
        active: true,
        loaded: false,
        failingCallback: function () {
            errors.throw("Core features failed", "Some terrible errors destroyed this site, maybe you can fix it by reloading the page", errors.FATAL);
        }
    },
    gamesCount: {
        active: true,
        loaded: false,
        failingCallback: function () {
            statistics.ui.hideGamesCount();
            errors.throw("Games count statistic failed");
        }
    },

    notifyFeatureFailed: function (name) {
        if (features[name] != undefined) {
            features[name].active = false;
            features[name].failingCallback();
        }
    },

    notifyFeatureLoaded: function (name) {
        if (features[name] != undefined) {
            features[name].loaded = true;
        }
    }
};