var loading = {
	loaded: [], // "runes", "runeDescriptions", "patches", "maxPossibleId," "mostPickedRoleRunes", "totalAnalyzedRunes", "googleCharts"
	loadingFailed: [],
	loadingCallbacks: [],

	init: function() {
		this.addLoadingCallback(
			function() {
				configuration.setID(0);
				setTimeout(loading.ui.hide, 300);
				features.notifyFeatureLoaded("core");
			},
			function() {
				features.notifyFeatureFailed("core");
			},
			"runes", "runeDescriptions", "patches", "maxPossibleID"
		);

		this.addLoadingCallback(
			function() {
				data.apiRequest("patches", 
					function(response) {
						data.patches = response;
						loading.notifyIsLoaded("patches");
					},
					function() {
						loading.notifyLoadingFailed("patches");
					},
					{
						since: configuration.baseTimestamp
					}
				);
			},
			null,
			"runes"
		);

		this.addLoadingCallback(
			function() {
				try {
					statistics.initGamesCount();
					statistics.refreshGamesCount(configuration.minID, data.configuration.runeLoadingAmount);
					statistics.ui.refreshGamesCount();
					features.notifyFeatureLoaded("gamesCount");
				} catch (e) {
					console.log(e);
					features.notifyFeatureFailed("gamesCount");
				}
			},
			function() {
				features.notifyFeatureFailed("gamesCount");
			},
			"runes", "maxPossibleID", "googleCharts"
		);

		this.addLoadingCallback(
			function() {
				statistics.ui.refreshMostPickedRoleRunes();
				features.notifyFeatureLoaded("mostPickedRoleRunes");
			},
			function() {
				features.notifyFeatureFailed("mostPickedRoleRunes");
			},
			"mostPickedRoleRunes", "totalAnalyzedRunes", "runeDescriptions"
		);

		this.addLoadingCallback(
			function() {
				header.ui.refreshTotalAnalyzedRunes();
				features.notifyFeatureLoaded("totalAnalyzedRunes");
			},
			function() {
				features.notifyFeatureFailed("totalAnalyzedRunes");
			},
			"totalAnalyzedRunes"
		);
	},

	start: function() {
		statistics.init();
		map.init();
		configuration.ui.init();
		footer.ui.init();
		data.load();
	},

	notifyIsLoaded(name) {
		this.loaded.push(name);
		this.check();
	},

	notifyLoadingFailed(name) {
		this.loadingFailed.push(name);
		this.check();
	},

	check: function() {
		for (var i = 0; i < this.loadingCallbacks.length; i++) {
			if (this.loadingCallbacks[i].called) continue;
			var loadingsDone = true;
			var someLoadingsFailed = false;
			for (var j = 0; j < this.loadingCallbacks[i].require.length; j++) {
				if (typeof this.loadingCallbacks[i].require[j] == "string") {
					if (this.loadingFailed.indexOf(this.loadingCallbacks[i].require[j]) >= 0) {
						someLoadingsFailed = true;
						break;
					}
					if (this.loaded.indexOf(this.loadingCallbacks[i].require[j]) < 0) {
						loadingsDone = false;
						break;
					}
				} else if (typeof this.loadingCallbacks[i].require[j] == "function") {
					if (!typeof this.loadingCallbacks[i].require[j]) {
						loadingsDone = false;
						break;
					}
				}				
			}
			if (someLoadingsFailed) {
				if (typeof this.loadingCallbacks[i].fallback == "function") {
					this.loadingCallbacks[i].fallback();
				}
				this.loadingCallbacks[i].called = true;
				continue;
			}
			if (loadingsDone) {
				if (typeof this.loadingCallbacks[i].callback == "function") {
					this.loadingCallbacks[i].callback();
				}
				this.loadingCallbacks[i].called = true;
			}
		}
	},

	addLoadingCallback(callback, fallback, ...require) {
		if (require.length == 0) return; 
		this.loadingCallbacks.push({
			called: false,
			callback: callback,
			fallback: fallback,
			require: require
		});
	},

	ui: {
		elements: {
			loader: document.getElementById('loader'),
			main: document.getElementsByTagName('main')[0]
		},

		hide: function() {
			loading.ui.elements.loader.style.opacity = "0";
			setTimeout(function() {loading.ui.elements.loader.style.visibility = "hidden";}, 400);
			loading.ui.elements.main.style.opacity = "1";
			header.ui.show();
		}
	}
}