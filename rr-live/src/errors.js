var errors = {
    FATAL: 0,
    SORRY: 1,

    throw: function (title, message, level, debugMessage) {
        if (debugMessage != undefined) {
            console.log(debugMessage);
        }

        if (level == this.FATAL) {
            this.ui.showPersistent(title, message);
        } else {
            this.ui.showSnackbar(title + (typeof message == "string" ? ": " + message : ""));
        }
    },

    ui: {
        elements: {
            persistent: {
                container: document.getElementById('error'),
                title: document.getElementById('error-title'),
                message: document.getElementById('error-message')
            },
            snackbar: document.getElementById('error-snackbar')
        },

        showPersistent: function (title, message) {
            this.elements.persistent.title.innerHTML = title;
            this.elements.persistent.message.innerHTML = message;
            this.elements.persistent.container.classList.add("shown");
        },

        showSnackbar: function (message) {
            'use strict';
            var data = {
                message: message
            };
            if (this.elements.snackbar.MaterialSnackbar == undefined) {
                componentHandler.upgradeElements(this.elements.snackbar);
            }
            this.elements.snackbar.MaterialSnackbar.showSnackbar(data);
        }
    }
};

window.onerror = function (message, source, lineno, colno, error) {
    if ((/syntax/gi).test(error)) {
        errors.throw("Site is broken", "Some of the code is not valid &ndash; sorry for that &ndash;, but we will fix it!", errors.FATAL);
    }
};