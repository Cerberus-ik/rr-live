function Rune(id, usage, total) {
	this.id = id;
	this.usage = usage;
	this.percent = Math.round(usage / total * 1000) / 10;
	this.name = data.getRuneName(id);
}

function RuneList(order, usage) {
	var list = [];
	var total = 0;
	for (var i = 0; i < usage.length; i++) {
		total += usage[i];
	}
	for (var i = 0; i < order.length; i++) {
		list.push(new Rune(order[i], usage[i], total));
	}
	list.sort(function (a, b) {
		return ((a.usage < b.usage) ? 1 : ((a.usage == b.usage) ? 0 : -1));
	});

	return {
		getAllRunes: function () {
			return list;
		},
		getFirstRune: function () {
			return list[0];
		},
		getSecondRune: function () {
			return list[1];
		},
		getThirdRune: function () {
			return list[2];
		}
	}
}