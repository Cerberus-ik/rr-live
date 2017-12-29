console.log("Server started.");

var express = require("express");

var app = express();
var server = app.listen(process.env.PORT || 80);

/* Handles web browser requests. */
app.use(express.static("public"));

/* Handles 404 requests. */
app.use(function (req, res, next) {
    res.status(404).send("Uuuups 404, my bad.");
});
