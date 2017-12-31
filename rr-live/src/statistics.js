var statistics = {
	gamesCount: [["Day", "Games analysed"]],

	init: function () {
		google.charts.load('current', {'packages': ['corechart']});
		google.charts.setOnLoadCallback(function () {
			loading.notifyIsLoaded("googleCharts");
		});
	},

	initGamesCount: function () {
		if (!features.gamesCount.active) return;
		if (this.gamesCount.length < 1) {
			return;
		}
		for (var i = 1; i <= configuration.maxPossibleID - configuration.minID + 1; i++) {
			this.gamesCount[i] = [new Date(configuration.baseTimestamp + configuration.interval * configuration.getIndex(i)), null];
		}
	},
	refreshGamesCount: function (id, amount) {
		if (!features.gamesCount.active) return;
		for (var i = 0; i < amount; i++) {
			this.gamesCount[id - configuration.minID + i + 1][1] = data.getGamesCount(id - configuration.minID + i);
		}
	},

	ui: {
		elements: {
			gamesCountChart: document.getElementById('games-count-chart'),
			gamesCountChartContainer: document.getElementById('games-count-chart-container')
		},
		refreshGamesCount: function () {
			if (!features.gamesCount.active) return;
			try {
				var data = google.visualization.arrayToDataTable(statistics.gamesCount);
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
					gridlines: {color: "transparent"},
					format: "none"
				},
				hAxis: {
					gridlines: {color: "transparent"},
					textPosition: "none",
					format: "d MMM y - h"
				},
				legend: {position: "none"}
			};

			try {
				var chart = new google.visualization.AreaChart(this.elements.gamesCountChart);
				chart.draw(data, options);
			} catch (e) {
				console.log(e);
				features.notifyFeatureFailed("gamesCount");

			}
		},
		hideGamesCount: function () {
			this.elements.gamesCountChartContainer.style.display = "none";
		}
	}
};