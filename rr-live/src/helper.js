var helper = {
	isVisible: false,
	init: function() {
		if (window.localStorage === false || !localStorage.getItem("visited")) {
			helper.isVisible = true;
			helper.ui.show();
		}
	},
	hide: function() {
		helper.isVisible = false;
		localStorage.setItem("visited", true);
		helper.ui.hide();
	},
	ui: {
		elements: {
			helper: document.getElementById('please-hover')
		},
		show: function() {
			this.elements.helper.style.visibility = "visible";
			this.elements.helper.classList.add("shown");
		},
		hide: function() {
			this.elements.classList.remove("shown");
			setTimeout(function() {helper.ui.elements.style.visibility = "hidden";}, 300);
		}
	}
}