var features = {
	core: {
		active: true,
		loaded: false,
		failingCallback: function() {
			errors.throw("Core features failed", "Some terrible errors destroyed this site, maybe you can fix it by reloading the page", errors.FATAL);
		}
	},

	gamesCount: {
		active: true,
		loaded: false,
		failingCallback: function() {
			statistics.ui.hideGamesCount();
			errors.throw("Games count statistic failed");
		}
	},

	totalAnalyzedRunes: {
		active: true,
		loaded: false,
		failingCallback: function() {
			header.ui.hideTotalAnalyzedRunes();
			errors.throw("Cannot show count of analysed runes");
		}
	},

	mostPickedRoleRunes: {
		active: true,
		loaded: false,
		failingCallback: function() {
			statistics.ui.hideMostPickedRoleRunes();
			errors.throw("Cannot show all-time most picked runes");
		}
	},

	notifyFeatureFailed: function(name) {
		if (features[name] != undefined) {
			features[name].active = false;
			features[name].failingCallback();
		}
	},

	notifyFeatureLoaded: function(name) {
		if (features[name] != undefined) {
			features[name].loaded = true;
		}
	}
}