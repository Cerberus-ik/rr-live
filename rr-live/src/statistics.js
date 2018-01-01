var statistics = {
	gamesCount: [["Day", "Games analysed"]],

	init: function() {
		try {
			google.charts.load('current', {'packages':['corechart']});
			google.charts.setOnLoadCallback(function() {loading.notifyIsLoaded("googleCharts");});
		} catch (e) {
			features.notifyFeatureFailed("gamesCount");
		}
	},

	initGamesCount: function() {
		if (!features.gamesCount.active) return;
		if (this.gamesCount.length < 1) {
			return;
		}
		for (var i = 1; i <= configuration.maxPossibleID - configuration.minID + 1; i++) {
		 	this.gamesCount[i] = [new Date(configuration.baseTimestamp + configuration.interval * configuration.getIndex(i)), null];
		}
	},
	refreshGamesCount: function(id, amount) {
		if (!features.gamesCount.active) return;
		for (var i = 0; i < amount; i++) {
			this.gamesCount[id - configuration.minID + i + 1][1] = data.getGamesCount(id - configuration.minID + i);
		}
	},

	ui: {
		elements: {
			gamesCountChart: document.getElementById('games-count-chart'),
			gamesCountChartContainer: document.getElementById('games-count-chart-container'),

			mostPickedRoleRunes: {
				container: document.getElementById('most-picked-role-runes'),
				cards: [{
					container: document.getElementById('most-picked-role-runes-first-container'),
					img: document.getElementById('most-picked-role-runes-first-img'),
					title: document.getElementById('most-picked-role-runes-first-title'),
					subtitle: document.getElementById('most-picked-role-runes-first-subtitle')
				},
				{
					container: document.getElementById('most-picked-role-runes-second-container'),
					img: document.getElementById('most-picked-role-runes-second-img'),
					title: document.getElementById('most-picked-role-runes-second-title'),
					subtitle: document.getElementById('most-picked-role-runes-second-subtitle')
				},
				{
					container: document.getElementById('most-picked-role-runes-third-container'),
					img: document.getElementById('most-picked-role-runes-third-img'),
					title: document.getElementById('most-picked-role-runes-third-title'),
					subtitle: document.getElementById('most-picked-role-runes-third-subtitle')
				},
				{
					container: document.getElementById('most-picked-role-runes-fourth-container'),
					img: document.getElementById('most-picked-role-runes-fourth-img'),
					title: document.getElementById('most-picked-role-runes-fourth-title'),
					subtitle: document.getElementById('most-picked-role-runes-fourth-subtitle')
				},
				{
					container: document.getElementById('most-picked-role-runes-fifth-container'),
					img: document.getElementById('most-picked-role-runes-fifth-img'),
					title: document.getElementById('most-picked-role-runes-fifth-title'),
					subtitle: document.getElementById('most-picked-role-runes-fifth-subtitle')
				}]
			}
		},
		refreshGamesCount: function() {
			if (!features.gamesCount.active) return;

			var chartData = [["Day", "Games analysed"]];
			for (var i = 1; i < Math.floor(statistics.gamesCount.length - 1 / 2); i++) {
				if (statistics.gamesCount[2 * i + 1] == undefined || 
					statistics.gamesCount[2 * i + 2] == undefined) continue;
				if (statistics.gamesCount[2 * i + 1][1] == null && 
					statistics.gamesCount[2 * i + 2][1] == null) {
					chartData.push([statistics.gamesCount[2 * i + 1][0], null]);
				} else {
					chartData.push([statistics.gamesCount[2 * i + 1][0], statistics.gamesCount[2 * i + 1][1] + statistics.gamesCount[2 * i + 2][1]]);
				}
			}

			try {
				var data = google.visualization.arrayToDataTable(chartData);
				(new google.visualization.DateFormat({pattern: "dd. MMM yyyy"})).format(data, 0);
				(new google.visualization.NumberFormat({pattern: "#"})).format(data, 1);
			} catch (e) {
				console.log(e);
				features.notifyFeatureFailed("gamesCount");
				return;
			}

			var options = {
				theme: "maximized",
				backgroundColor: "transparent",
				colors: ["#EEEEEE"],
				vAxis: {
					minValue: 0,
					textColor: "#EEEEEE",
				textPosition: "out",
				gridlines: {color: "transparent"}
				},
				hAxis: {
					gridlines: {color: "transparent"},
					textPosition: "none"
				},
				legend: {position: "none"}
			};

			try {
				var chart = new google.visualization.AreaChart(this.elements.gamesCountChart);
			 	chart.draw(data, options);
			} catch (e) {
				console.log(e);
				features.notifyFeatureFailed("gamesCount");
				return;
			}
		},
		hideGamesCount: function() {
			this.elements.gamesCountChartContainer.style.display = "none";
		},

		refreshMostPickedRoleRunes: function() {
			if (!features.mostPickedRoleRunes.active) return;
			if (data.mostPickedRoleRunes == null) {
				features.notifyFeatureFailed("mostPickedRoleRunes");
				return;
			}
			for (var i = 0; i < 5; i++) {
				if (data.mostPickedRoleRunes[i] == undefined) {
					this.hideMostPickedRoleRunesCard(i);
					continue;
				}
				this.elements.mostPickedRoleRunes.cards[i].img.src = "perk/" +	data.mostPickedRoleRunes[i].rune + ".png";
				this.elements.mostPickedRoleRunes.cards[i].title.innerHTML = (i+1) + ". " + data.getRuneName(data.mostPickedRoleRunes[i].rune);
				this.elements.mostPickedRoleRunes.cards[i].subtitle.innerHTML = "<span class='role-name'>" + data.getRoleName(data.mostPickedRoleRunes[i].role) + "</span> &ndash; " + data.mostPickedRoleRunes[i].timesPicked + " times picked &ndash; " + (Math.round(data.mostPickedRoleRunes[i].timesPicked/(data.totalAnalyzedRunes/5)*1000)/10) + "%";
			}
		},
		hideMostPickedRoleRunes: function() {
			this.elements.mostPickedRoleRunes.container.style.display = "none";
		},
		hideMostPickedRoleRunesCard: function(i) {
			this.elements.mostPickedRoleRunes.cards[i].container.style.display = "none";
		}
	}
}