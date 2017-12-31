var footer = {
	ui: {
		elements: {
			imprint: document.getElementById('imprint'),
			imprintClose: document.getElementById('imprint-close')
		},
		init: function() {
			this.elements.imprintClose.addEventListener("click", function() {footer.ui.hideImprint();});
		},
		showImprint: function() {
			this.elements.imprint.style.visibility = "visible";
			this.elements.imprint.style.opacity = "1";
		},
		hideImprint: function() {
			this.elements.imprint.style.opacity = "0";
			setTimeout(function() {footer.ui.elements.imprint.style.visibility = "hidden"}, 500); 
		}
	}
}