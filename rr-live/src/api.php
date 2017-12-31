<?php
$riotBackendApi = "https://riot-backend-api.herokuapp.com/api/v1";
if ($_GET["api"] == "runes") {
	echo file_get_contents($riotBackendApi."/runes/getRunes?id=".$_GET["id"]."&amount=".$_GET["amount"]);
} else if ($_GET["api"] == "descriptions") {
	echo file_get_contents("http://ddragon.leagueoflegends.com/cdn/7.24.1/data/en_US/runesReforged.json");
} else if ($_GET["api"] == "patches") {
	echo file_get_contents($riotBackendApi."/patch/getPatches?since=".$_GET["since"]);
} else if ($_GET["api"] == "maxpossibleid") {
	echo file_get_contents($riotBackendApi."/runes/getLatestStepId");
} else if ($_GET["api"] == "analysedrunes") {
	echo file_get_contents($riotBackendApi."/fact/getAnalyzedRunes");
} else if ($_GET["api"] == "mostpickedrolerunes") {
	echo file_get_contents($riotBackendApi."/fact/getMostPickedRunes");
}
?>