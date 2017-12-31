var data = {
    runes: null,
    runeDescriptions: null,
    patches: null,
    mostPickedRoleRunes: null,
    totalAnalyzedRunes: null,

    getRuneName: function (id) {
        var category = Math.floor(id / 100) * 100;
        if (this.runeDescriptions != null) {
            for (var i = 0; i < this.runeDescriptions.length; i++) {
                if (this.runeDescriptions[i].id == category) {
                    for (var j = 0; j < this.runeDescriptions[i].slots.length; j++) {
                        for (var k = 0; k < this.runeDescriptions[i].slots[j].runes.length; k++) {
                            if (this.runeDescriptions[i].slots[j].runes[k].id == id)
                                return this.runeDescriptions[i].slots[j].runes[k].name;
                        }
                    }
                }
            }
        }
        return "Unknown";
    },

    getRoleName: function(name) {
        switch (name) {
            case "ADC": return "ADC";
            case "TOP": return "Toplane";
            case "MID": return "Midlane";
            case "SUPPORT": return "Support";
            case "JUNGLE": return "Jungle";
        }
        return "Unknown";
    },

    getPatch: function(timestamp) {
        var x;
        for (var i = 0; i < data.patches.length; i++) {
            if (data.patches[i].start <= timestamp) {
                x = i;
            }
        }
        return data.patches[x].patch;
    },

    getMonthName: function(month) {
        switch (month) {
            case 0: return "January";
            case 1: return "February";
            case 2: return "March";
            case 3: return "April";
            case 4: return "May";
            case 5: return "June";
            case 6: return "July";
            case 7: return "August";
            case 8: return "September"; 
            case 9: return "October";
            case 10: return "November";
            case 11: return "December";
            default: return "Unknown";
        }
    },

    getDaytime: function(hour) {
        if (hour > 22 || hour < 6) {
            return "Night";
        } else if (hour >= 6 && hour < 11) {
            return "Morning";
        } else if (hour >= 10 && hour < 14) {
            return "Midday";
        } else if (hour >= 14 && hour < 17) {
            return "Afternoon";
        } else {
            return "Evening";
        }
    },

    getGamesCount: function(id) {
        if (id > configuration.maxID || id < configuration.minID) return;
        var count = 0;
        Object.keys(map.roles).forEach(function(name) {
            for (var i = 0; i < data.runes[name][configuration.getIndex(id)].length; i++) {
                count += data.runes[name][configuration.getIndex(id)][i];
            }
        });
        return Math.floor(count/10);
    },

    configuration: {
        autoLoad: true,
        runeLoadingAmount: 10
    },

    apiRequest: function(api, resonseCallback, errorCallback, parameters) {
        var ajax = new XMLHttpRequest();
        if (typeof errorCallback == "function") {
            ajax.addEventListener("error", errorCallback);
            ajax.addEventListener("abort", errorCallback);
        }
        ajax.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                try {
                    var json = JSON.parse(this.responseText);
                    resonseCallback(json);
                } catch (e) {
                    console.log(e);
                    if (typeof errorCallback == "function") {
                        errorCallback();
                    }
                }               
            }
            if (this.status == 500) {
                if (typeof errorCallback == "function") {
                    errorCallback();
                }
            }
        };
        var query = "";
        if (parameters != undefined && 
            parameters != null && 
            Object.keys(parameters).length > 0) {
            Object.keys(parameters).forEach(function(key) {
                query += "&" + key + "=" + parameters[key];
            });
        }
        ajax.open("GET", "api.php?api="+api+query, true);
        ajax.send();
    },

    load: function() {
        if (!(new XMLHttpRequest())) {
            throwError("No Ajax support", "The Application uses Ajax (also known as XMLHttpRequest), but it's required. Please use a other Browser.");
            return;
        }

        data.apiRequest("maxpossibleid", 
            function(response) {
                configuration.maxPossibleID = response.latestStepId;
                loading.notifyIsLoaded("maxPossibleID");
            },
            function() {
                loading.notifyLoadingFailed("maxPossibleID");
            }
        );

        data.apiRequest("runes", 
            function(response) {
                data.runes = response;
                configuration.baseTimestamp = response.firstGame;
                configuration.interval = response.stepSize; 
                configuration.maxID = configuration.minID + data.configuration.runeLoadingAmount - 1;
                loading.notifyIsLoaded("runes");
                if (data.configuration.autoLoad) {
                    data.loadNext();
                }
            },
            function() {
                loading.notifyLoadingFailed("runes");
            },
            {
                id: configuration.minID, 
                amount: data.configuration.runeLoadingAmount
            }
        );

        data.apiRequest("descriptions", 
            function(response) {
                data.runeDescriptions = response;
                loading.notifyIsLoaded("runeDescriptions");
            }, 
            function() {
                loading.notifyLoadingFailed("runeDescriptions");
            }
        );

        data.apiRequest("analysedrunes",
            function(response) {
                if (typeof response.totalAnalyzedRunes != "number") {
                    loading.notifyLoadingFailed("totalAnalyzedRunes");
                    return;
                }
                data.totalAnalyzedRunes = response.totalAnalyzedRunes;
                loading.notifyIsLoaded("totalAnalyzedRunes");
            },
            function() {
                loading.notifyLoadingFailed("totalAnalyzedRunes");
            }
        );

        data.apiRequest("mostpickedrolerunes", 
            function(response) {
                response.sort(function(a, b) {
                    return b.timesPicked - a.timesPicked;
                });
                data.mostPickedRoleRunes = response;
                loading.notifyIsLoaded("mostPickedRoleRunes");
            }, 
            function() {
                loading.notifyLoadingFailed("mostPickedRoleRunes");
            }
        );
    },

    loadNext: function() {
        if (configuration.maxID >= configuration.maxPossibleID) return;
        var id = configuration.maxID + 1;
        configuration.maxLoadingID = configuration.maxID - configuration.minID + data.configuration.runeLoadingAmount;
        configuration.ui.refreshProgressBar();
        data.apiRequest("runes", 
            function(response) {
                var lastID = 0;
                Object.keys(map.roles).forEach(function(name) {
                    for (var i = 0; i < data.configuration.runeLoadingAmount; i++) {
                        if (response[name][i] != undefined) {
                            data.runes[name][id - configuration.minID + i] = response[name][i];
                            lastID = id + i;
                        }
                    }
                });
                configuration.maxID = lastID;
                configuration.maxLoadingID = null;
                configuration.ui.refreshProgressBar();
                statistics.refreshGamesCount(id, lastID - id + 1);
                statistics.ui.refreshGamesCount();

                if (data.configuration.autoLoad) {
                    data.loadNext();
                }
            },
            function() {
                errors.throw("Failed to load Data", null, errors.SORRY);
            },
            {
                id: id, 
                amount: data.configuration.runeLoadingAmount
            });
    }
};