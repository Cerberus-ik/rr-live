<!DOCTYPE html>
<html>
<head>
	<title>Runes Reforged Live</title>

	<!-- Import Googles Material Icons -->
	<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">

	<!-- Import Googles Material Design Light -->
	<link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.grey-indigo.min.css">
	<script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>

	<!-- Import Googles Font 'Roboto' -->
	<link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Roboto:300,400,500,700" type="text/css">

	<!-- Import my own CSS -->
	<link rel="stylesheet" type="text/css" href="index.css">

	<!-- Some important meta data -->
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, user-scalable=yes">
	<meta name="theme-color" content="#212121">
</head>
<body class="mdl-color--grey-800">
	<!-- LOADER -->
	<div id="loader" class="mdl-spinner mdl-spinner--single-color mdl-js-spinner is-active mdl-color-text--grey-200"></div>

	<!-- HEADER -->
	<header id="header" class="mdl-layout__header">
		<div class="mdl-layout__header-row">
			<span class="mdl-layout-title">Runes Reforged Live</span>
			<div class="mdl-layout-spacer"></div>
			<span id="total-analysed-runes-container" class="mdl-color-text--grey-100">Anaylsed runes: <span id="total-analysed-runes"></span></span>
			<span id="platinum-plus" class="mdl-color--grey-700 mdl-color-text--grey-200">Platinum+</span>
		</div>
	</header>

	<!-- MAIN -->
	<main style="opacity: 0;">
		<div id="primary-content">
			<div id="map">
				<div id="map-base"><?php echo file_get_contents("map-min.svg"); ?></div>
				<div id="map-overlays">
					<div id="map-toplane-overlay" class="mdl-card mdl-shadow--2dp map-toplane">
						<ul>
							<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
							</li>
							<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
							</li>
							<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
							</li>
						</ul>
						<div class="mdl-card__menu">
							<button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect" id="map-toplane-overlay-close">
								<i class="material-icons">close</i>
							</button>
						</div>
					</div>
					<img id="map-toplane-1" src="none.jpg" alt="" class="map-toplane map-img-first">
					<img id="map-toplane-2" src="none.jpg" alt="" class="map-toplane map-img-second">
					<img id="map-toplane-3" src="none.jpg" alt="" class="map-toplane map-img-third">
					<div id="map-midlane-overlay" class="mdl-card mdl-shadow--2dp map-midlane">
						<ul>
							<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
							</li>
							<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
							</li>
							<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
							</li>
						</ul>
						<div class="mdl-card__menu">
							<button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect" id="map-midlane-overlay-close">
								<i class="material-icons">close</i>
							</button>
						</div>
					</div>
					<img id="map-midlane-1" src="none.jpg" alt="" class="map-midlane map-img-first">
					<img id="map-midlane-2" src="none.jpg" alt="" class="map-midlane map-img-second">
					<img id="map-midlane-3" src="none.jpg" alt="" class="map-midlane map-img-third">
					<div id="map-jungle-overlay" class="mdl-card mdl-shadow--2dp map-jungle">
						<ul>
							<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
							</li>
							<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
							</li>
							<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
							</li>
						</ul>
						<div class="mdl-card__menu">
							<button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect" id="map-jungle-overlay-close">
								<i class="material-icons">close</i>
							</button>
						</div>
					</div>
					<img id="map-jungle-1" src="none.jpg" alt="" class="map-jungle map-img-first">
					<img id="map-jungle-2" src="none.jpg" alt="" class="map-jungle map-img-second">
					<img id="map-jungle-3" src="none.jpg" alt="" class="map-jungle map-img-third">
					<div id="map-support-overlay" class="mdl-card mdl-shadow--2dp map-support">
						<ul>
							<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
							</li>
							<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
							</li>
							<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
							</li>
						</ul>
						<div class="mdl-card__menu">
							<button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect" id="map-support-overlay-close">
								<i class="material-icons">close</i>
							</button>
						</div>
					</div>
					<img id="map-support-1" src="none.jpg" alt="" class="map-support map-img-first">
					<img id="map-support-2" src="none.jpg" alt="" class="map-support map-img-second">
					<img id="map-support-3" src="none.jpg" alt="" class="map-support map-img-third">
					<div id="map-adc-overlay" class="mdl-card mdl-shadow--2dp map-adc">
						<ul>
							<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
								</li>
								<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
								</li>
								<li class="mdl-list__item mdl-list__item--two-line">
								<span class="mdl-list__item-primary-content">
									<i class="mdl-list__item-avatar"></i>
									<span></span>
									<span class="mdl-list__item-sub-title"></span>
								</span>
							</li>
						</ul>
						<div class="mdl-card__menu">
							<button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect" id="map-adc-overlay-close">
								<i class="material-icons">close</i>
							</button>
						</div>
					</div>
					<img id="map-adc-1" src="none.jpg" alt="" class="map-adc map-img-first">
					<img id="map-adc-2" src="none.jpg" alt="" class="map-adc map-img-second">
					<img id="map-adc-3" src="none.jpg" alt="" class="map-adc map-img-third">
					<div id="please-hover" class="mdl-card map-toplane">
						<div class="mdl-card__supporting-text">Please hover me!</div>
					</div>
				</div>
			</div>
			<div id="configuration" class="mdl-color-text--grey-200">
				<h4 id="current-date"></h4>
				<h6 id="current-daytime"></h6>
				<div id="play-controls">
					<button class="mdl-button mdl-js-button mdl-button--icon" id="control-button-first">
						<i class="material-icons">first_page</i>
					</button>
					<button class="mdl-button mdl-js-button mdl-button--icon" id="control-button-previous">
						<i class="material-icons">navigate_before</i>
					</button>
					<button class="mdl-button mdl-js-button mdl-button--icon" id="control-button-toggle">
						<i class="material-icons" id="control-icon-toggle">play_arrow</i>
					</button>
					<button class="mdl-button mdl-js-button mdl-button--icon" id="control-button-next">
						<i class="material-icons">navigate_next</i>
					</button>
					<button class="mdl-button mdl-js-button mdl-button--icon" id="control-button-last">
						<i class="material-icons">last_page</i>
					</button>
				</div>
				<div id="configuration-progress" class="mdl-progress mdl-js-progress"></div>
				<div class="additional-information">
					<p>Patch <span id="current-patch"></span> &ndash; <span id="current-games-count"></span> Games analysed</p>
				</div>
				<div id="games-count-chart-container">
					<h6>Games analysed</h6>
					<div id="games-count-chart"></div>
				</div>
			</div>
		</div>

		<div id="secondary-content">
			<div id="most-picked-role-runes">
				<h5 class="mdl-color-text--grey-200">All-time most picked runes</h5>
				<div>
					<div id="most-picked-role-runes-first-container" class="mdl-card mdl-color--grey-300">
						<img id="most-picked-role-runes-first-img" src="perk/8005.png" alt="">
						<h2 id="most-picked-role-runes-first-title" class="mdl-card__title-text">1. Press the Attack</h2>
						<h3 id="most-picked-role-runes-first-subtitle" class="mdl-card__subtitle-text">Support &ndash; 3456 Picks &ndash; 50%</h3>
					</div>
					<div id="most-picked-role-runes-second-container" class="mdl-card mdl-color--grey-400">
						<img id="most-picked-role-runes-second-img" src="none.jpg" alt="">
						<h2 id="most-picked-role-runes-second-title" class="mdl-card__title-text"></h2>
						<h3 id="most-picked-role-runes-second-subtitle" class="mdl-card__subtitle-text"></h3>
					</div>
					<div id="most-picked-role-runes-third-container" class="mdl-card mdl-color--grey-500">
						<img id="most-picked-role-runes-third-img" src="none.jpg" alt="">
						<h2 id="most-picked-role-runes-third-title" class="mdl-card__title-text"></h2>
						<h3 id="most-picked-role-runes-third-subtitle" class="mdl-card__subtitle-text"></h3>
					</div>
					<div id="most-picked-role-runes-fourth-container" class="mdl-card mdl-color--grey-600">
						<img id="most-picked-role-runes-fourth-img" src="none.jpg" alt="">
						<h2 id="most-picked-role-runes-fourth-title" class="mdl-card__title-text"></h2>
						<h3 id="most-picked-role-runes-fourth-subtitle" class="mdl-card__subtitle-text"></h3>
					</div>
					<div id="most-picked-role-runes-fifth-container" class="mdl-card mdl-color--grey-600">
						<img id="most-picked-role-runes-fifth-img" src="none.jpg" alt="">
						<h2 id="most-picked-role-runes-fifth-title" class="mdl-card__title-text"></h2>
						<h3 id="most-picked-role-runes-fifth-subtitle" class="mdl-card__subtitle-text"></h3>
					</div>
				</div>
			</div>
		</div>
	</main>

	<!-- FOOTER -->
	<footer class="mdl-mini-footer mdl-color--grey-900">
		<div class="mdl-mini-footer__left-section">
		<div class="mdl-logo">Runes Reforged Live</div>
		<p>This site isn't endorsed by Riot Games and doesn't reflect the views or opinions of Riot Games or anyone officially involved in producing or managing League of Legends. League of Legends and Riot Games are trademarks or registered trademarks of Riot Games, Inc. League of Legends &copy; Riot Games, Inc.</p>
		<ul class="mdl-mini-footer__link-list">
			<li><a href="javascript: footer.ui.showImprint()">Imprint &amp; Contact</a></li>
			<li><a href="https://github.com/Cerberus-ik/rr-live/">Github</a></li>
		</ul>
		</div>
	</footer>

	<!-- IMPRINT -->
	<div id="imprint">
		<div class="mdl-card mdl-shadow--2dp">
			<div class="mdl-card__title">
				<h2 class="mdl-card__title-text">Imprint &amp; Contact</h2>
			</div>
			<div class="mdl-card__supporting-text">
				<h5>Owners</h5>
				<div class="list-horizontal">
					<div>
						<h6>Philipp Hickisch</h6>
						<p>Brombeerweg 6<br>18209 Bad Doberan<br>Germany</p>
						<p>philipphickisch@gmail.com</p>
					</div>
					<div>
						<h6>Gerolf Vent</h6>
						<p>Dorfstra&szlig;e 37<br>18239 Satow<br>Germany</p>
						<p>gerolf@vent-projects.de</p>
					</div>
				</div>
				<h5>Frameworks &amp; Libraries</h5>
				<div class="list-horizontal">
					<div>
						<h6>Material Design Light</h6>
						<p><a href="https://getmdl.io/">Website</a> <a href="https://github.com/google/material-design-lite/blob/mdl-1.x/LICENSE">License</a></p>
					</div>
					<div>
						<h6>Google Charts</h6>
						<p><a href="https://developers.google.com/chart/">Website</a> <a href="https://github.com/GoogleWebComponents/google-chart/blob/master/LICENSE">License</a></p>
					</div>
				</div>
				<div class="list-horizontal">
					<div>
						<h6>Google Fonts</h6>
						<p><a href="https://fonts.google.com/">Website</a> <a href="https://fonts.google.com/attribution">Licenses</a></p>
					</div>
					<div>
						<h6>Material Design Icons</h6>
						<p><a href="https://material.io/icons/">Website</a> <a href="https://github.com/google/material-design-icons/blob/master/LICENSE">License</a></p>
					</div>
				</div>
			</div>
			<div class="mdl-card__menu">
				<button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect" id="imprint-close">
					<i class="material-icons">close</i>
				</button>
			</div>
		</div>
	</div>

	<!-- ERRORS -->
	<div id="error" class="sorry">
		<div class="sorry-text"><h5 id="error-title"></h5><p id="error-message"></p></div>
	</div>
	<div id="error-snackbar" class="mdl-js-snackbar mdl-snackbar">
		<div class="mdl-snackbar__text"></div>
		<button class="mdl-snackbar__action" type="button"></button>
	</div>

	<!-- MOBILE FALLBACK -->
	<div id="mobiles-unsupported" class="sorry">
		<div class="sorry-text"><h5>Sorry,</h5><p>this site doesn't work for mobile devices yet, please use a desktop computer or laptop</p></div>
	</div>

	<!-- JAVASCRIPT FALLBACK -->
	<noscript>
		<div class="sorry shown">
			<div class="sorry-text"><h5>Please activate Javascript</h5><p>Javascript is required for this application</p></div>
		</div>
	</noscript>

	<!-- IMPORT JAVASCRIPT -->
	<script type="text/javascript" src="errors.js"></script>
	<script type="text/javascript" src="features.js"></script>
	<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
	<script type="text/javascript" src="loading.js"></script>
	<script type="text/javascript" src="configuration.js"></script>
	<script type="text/javascript" src="header.js"></script>
	<script type="text/javascript" src="statistics.js"></script>
	<script type="text/javascript" src="data.js"></script>
	<script type="text/javascript" src="map.js"></script>
	<script type="text/javascript" src="runes.js"></script>
	<script type="text/javascript" src="helper.js"></script>
	<script type="text/javascript" src="footer.js"></script>

	<!-- INITIAL & START LOADING -->
	<script type="text/javascript">
		loading.init();
		loading.start();
	</script>
</body>
</html>