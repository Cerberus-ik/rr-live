var map = {
    roles: [],
    registerRole: function (displayName, name, key) {
        var role = new Role(displayName, name);
        map.roles[key] = role;
    },
    init: function () {
        map.registerRole("Toplane", "toplane", "TOP");
        map.registerRole("Jungle", "jungle", "JUNGLE");
        map.registerRole("Midlane", "midlane", "MID");
        map.registerRole("ADC", "adc", "ADC");
        map.registerRole("Support", "support", "SUPPORT");
    },
    ui: {
        elements: {
            map: document.getElementById('map'),
            mapOverlays: document.getElementById('map-overlays')
        },
        refreshRoleRunes: function () {
            Object.keys(map.roles).forEach(function (key) {
                if (data.runes[key][configuration.getCurrentIndex()] == undefined) {
                    map.ui.hideOverlays();

                } else {
                    map.ui.showOverlays();
                    map.roles[key].setRunes(new RuneList(data.runes.runeOrder, data.runes[key][configuration.getCurrentIndex()]));
                }
            });
        },
        showOverlays: function () {
            this.elements.mapOverlays.style.opacity = "1";
        },
        hideOverlays: function () {
            this.elements.mapOverlays.style.opacity = "0";
        },
        movingEngines: {
            start: function () {
                Object.keys(map.roles).forEach(function (key) {
                    map.roles[key].movingEngines.start();
                });
            },
            stop: function () {
                Object.keys(map.roles).forEach(function (key) {
                    map.roles[key].movingEngines.stop();
                });
            }
        }
    }
};

function Role(displayName, name) {
    var image1 = document.getElementById('map-' + name + '-1');
    var movingEngine1 = new MovingEngine(image1);
    var image2 = document.getElementById('map-' + name + '-2');
    var movingEngine2 = new MovingEngine(image2);
    var image3 = document.getElementById('map-' + name + '-3');
    var movingEngine3 = new MovingEngine(image3);
    var overlay = document.getElementById('map-' + name + '-overlay');
    var overlayTitle1 = overlay.getElementsByTagName("span")[1];
    var overlaySubtitle1 = overlay.getElementsByTagName("span")[2];
    var overlayTitle2 = overlay.getElementsByTagName("span")[4];
    var overlaySubtitle2 = overlay.getElementsByTagName("span")[5];
    var overlayTitle3 = overlay.getElementsByTagName("span")[7];
    var overlaySubtitle3 = overlay.getElementsByTagName("span")[8];
    var overlayClose = document.getElementById('map-' + name + '-overlay-close');

    var show = function () {
        Object.keys(map.roles).forEach(function (key) {
            if (map.roles[key].name != name) map.roles[key].hide();
        });
        overlay.style.visibility = "visible";
        overlay.classList.add("shown");
        image1.classList.add("shown");
        image2.classList.add("shown");
        image3.classList.add("shown");
        if (helper.isVisible) {
            helper.ui.hide()
        }
    };
    var hide = function () {
        overlay.classList.remove("shown");
        image1.classList.remove("shown");
        image2.classList.remove("shown");
        image3.classList.remove("shown");
        setTimeout(function () {
            if ((overlay.currentStyle || window.getComputedStyle(overlay)).opacity == "0") overlay.style.visibility = "hidden";
        }, 300);
    };
    image1.addEventListener("mouseover", show);
    overlayClose.addEventListener("click", hide);

    return {
        show: show,
        hide: hide,
        name: name,
        displayName: displayName,
        setRunes: function (runesList) {
            image1.src = "perk/" + runesList.getFirstRune().id + ".png";
            image1.style.transform = "scale(" + (runesList.getFirstRune().percent / 20) + ")";
            overlayTitle1.innerHTML = runesList.getFirstRune().name;
            overlaySubtitle1.innerHTML = runesList.getFirstRune().percent + "%";
            image2.src = "perk/" + runesList.getSecondRune().id + ".png";
            image2.style.transform = "scale(" + (runesList.getSecondRune().percent / 20) + ")";
            var secondMarginTop = (20 + runesList.getFirstRune().percent + runesList.getSecondRune().percent);
            movingEngine2.setMarginTop(secondMarginTop);
            image2.style.marginTop = secondMarginTop + "px";
            overlayTitle2.innerHTML = runesList.getSecondRune().name;
            overlaySubtitle2.innerHTML = runesList.getSecondRune().percent + "%";
            image3.src = "perk/" + runesList.getThirdRune().id + ".png";
            image3.style.transform = "scale(" + (runesList.getThirdRune().percent / 20) + ")";
            var thirdMarginTop = (secondMarginTop + 6 + runesList.getSecondRune().percent + runesList.getThirdRune().percent);
            movingEngine3.setMarginTop(thirdMarginTop);
            image3.style.marginTop = thirdMarginTop + "px";
            overlayTitle3.innerHTML = runesList.getThirdRune().name;
            overlaySubtitle3.innerHTML = runesList.getThirdRune().percent + "%";
        },
        movingEngines: {
            start: function () {
                movingEngine1.start();
                movingEngine2.start();
                movingEngine3.start();
            },
            stop: function () {
                movingEngine1.stop();
                movingEngine2.stop();
                movingEngine3.stop();
            }
        }
    };
}

function MovingEngine(element) {
    var marginTop = Number.parseInt((element.currentStyle || window.getComputedStyle(element)).marginTop);
    var marginLeft = 16;
    var isStarted = false;
    var move = function () {
        if (!isStarted) return;
        element.style.marginTop = randomMarginTop(marginTop) + "px";
        element.style.marginLeft = randomMarginTop(marginLeft) + "px";
        if (isStarted)
            setTimeout(move, randomTimeout(300, 700));
    };
    var reset = function () {
        element.style.marginTop = marginTop + "px";
        element.style.marginLeft = marginLeft + "px";
    };
    var randomMarginTop = function (fix) {
        return randomMargin(7) + fix;
    };
    var randomMarginleft = function (fix) {
        return randomMargin(5) + fix;
    };
    var randomMargin = function (range) {
        return Math.random() * range * 2 - range;
    };
    var randomTimeout = function (min, max) {
        return Math.random() * (max - min) + min;
    };
    return {
        start: function () {
            isStarted = true;
            move();
        },
        stop: function () {
            isStarted = false;
            reset();
        },
        setMarginTop: function (newMarginTop) {
            marginTop = newMarginTop;
        }
    }
}