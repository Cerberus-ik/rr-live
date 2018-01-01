var header = {
	ui: {
		elements: {
			header: document.getElementById('header'),
			totalAnalyzedRunes: document.getElementById('total-analysed-runes'),
			totalAnalyzedRunesContainer: document.getElementById('total-analysed-runes-container')
		},
		show: function() {
			this.elements.header.style.opacity = "1";
		},
		refreshTotalAnalyzedRunes: function() {
			if (!features.totalAnalyzedRunes.active) return;
			this.elements.totalAnalyzedRunes.innerHTML = data.totalAnalyzedRunes;
		},
		hideTotalAnalyzedRunes: function() {
			this.elements.totalAnalyzedRunesContainer.style.display = "none";
		}
	}
}