var configuration = {
	currentID: null,
	getCurrentIndex: function() {
		return this.getIndex(this.currentID);
	},
	getIndex: function(id) {
		return id - configuration.minID;
	},
	setID: function(id) {
		if (!this.isValid()) {
			throw "Configuration not valid";
			return;
		}
		if (id == this.currentID) return;
		if (id <= this.minID) {
			this.currentID = this.minID;
		} else if (id >= this.maxID) {
			this.currentID = this.maxID;
			this.stop();
		} else {
			this.currentID = id;
		}
		this.ui.refreshInformations();
		this.ui.refreshProgressBar();
		map.ui.refreshRoleRunes();
		if (this.currentID >= (this.maxID - 5) && !this.maxLoadingID) {
			data.loadNext();
		}
	},
	nextID: function(dontStop) {
		if (!dontStop) this.stop();
		if (this.currentID < this.maxID) this.setID(this.currentID+1);
	},
	previousID: function(dontStop) {
		if (!dontStop) this.stop();
		if (this.currentID > this.minID) this.setID(this.currentID-1);
	},
	lastID: function(dontStop) {
		if (!dontStop) this.stop();
		this.setID(this.maxID);
	},
	firstID: function(dontStop) {
		if (!dontStop) this.stop();
		this.setID(this.minID);
	},
	minID: 0,
	maxID: null,
	maxLoadingID: null,
	maxPossibleID: null,

	baseTimestamp: null,
	interval: null,

	playInterval: null,
	isPlaying: false,
	play: function() {
		if (this.currentID < this.maxID) {
			this.playInterval = setInterval(function() {configuration.nextID(true);}, 1000);
			this.isPlaying = true;
			this.ui.refreshToggleButton();
			map.ui.movingEngines.start();
		}
	},
	stop: function() {
		if (this.playInterval) {
			clearInterval(this.playInterval);
			this.playInterval = null;
		}
		map.ui.movingEngines.stop();
		this.isPlaying = false;
		this.ui.refreshToggleButton();
	},
	toggle: function() {
		if (!this.playInterval) {
			this.play();
		} else {
			this.stop();
		}
	},

	isValid: function() {
		return this.minID != null && this.maxID != null && this.baseTimestamp != null && this.interval != null;
	},

	ui: {
		elements: {
			currentDate: document.getElementById('current-date'),
			currentDaytime: document.getElementById('current-daytime'),
			currentPatch: document.getElementById('current-patch'),
			currentGamesCount: document.getElementById('current-games-count'),
			buttonFirst: document.getElementById('control-button-first'),
			buttonPrevious: document.getElementById('control-button-previous'),
			buttonToggle: document.getElementById('control-button-toggle'),
			iconToggle: document.getElementById('control-icon-toggle'),
			buttonNext: document.getElementById('control-button-next'),
			buttonLast: document.getElementById('control-button-last'),
			progressBar: document.getElementById('configuration-progress')
		},
		refreshToggleButton: function() {
			if (configuration.isPlaying) {
				this.elements.iconToggle.innerHTML = "pause";
			} else {
				this.elements.iconToggle.innerHTML = "play_arrow";
			}
		},
		refreshInformations: function() {
			var timestamp = configuration.baseTimestamp + configuration.interval * configuration.getIndex(configuration.currentID);
			var date = new Date(timestamp);
			this.elements.currentDate.innerHTML = date.getDate() + ". " + data.getMonthName(date.getMonth()) + " " + date.getFullYear();
			this.elements.currentDaytime.innerHTML = data.getDaytime(date.getHours());
			this.elements.currentPatch.innerHTML = data.getPatch(timestamp);
			this.elements.currentGamesCount.innerHTML = data.getGamesCount(configuration.currentID);
		},
		refreshProgressBar: function() {
			if (configuration.currentID == configuration.minID) {
				this.elements.buttonFirst.setAttribute("disabled", "");
				this.elements.buttonPrevious.setAttribute("disabled", "");
				this.elements.buttonNext.removeAttribute("disabled");
				this.elements.buttonLast.removeAttribute("disabled");
			} else if (configuration.currentID == configuration.maxID) {
				this.elements.buttonFirst.removeAttribute("disabled");
				this.elements.buttonPrevious.removeAttribute("disabled");
				this.elements.buttonNext.setAttribute("disabled", "");
				this.elements.buttonLast.setAttribute("disabled", "");
			} else {
				this.elements.buttonFirst.removeAttribute("disabled");
				this.elements.buttonPrevious.removeAttribute("disabled");
				this.elements.buttonNext.removeAttribute("disabled");
				this.elements.buttonLast.removeAttribute("disabled");
			}
			if (this.elements.progressBar.MaterialProgress == undefined) {
				componentHandler.upgradeElements(this.elements.progressBar);
			}
			this.elements.progressBar.MaterialProgress.setBuffer((configuration.maxID - configuration.minID) / (configuration.maxPossibleID - configuration.minID) * 100);
			this.elements.progressBar.MaterialProgress.setProgress((configuration.currentID - configuration.minID) / (configuration.maxPossibleID - configuration.minID) * 100);
		},
		refreshGamesCountChart: function() {

		},
		init: function() {
			window.addEventListener("keydown", function(event) {event = event || window.event; if (event.keyCode == 37) {configuration.previousID()} else if (event.keyCode == 39) {configuration.nextID()}}, false);
			this.elements.buttonFirst.addEventListener("click", function() {configuration.firstID();});
			this.elements.buttonPrevious.addEventListener("click", function() {configuration.previousID();});
			this.elements.buttonNext.addEventListener("click", function() {configuration.nextID();});
			this.elements.buttonLast.addEventListener("click", function() {configuration.lastID();});
			this.elements.buttonToggle.addEventListener("click", function() {configuration.toggle();});
		}
	}
}