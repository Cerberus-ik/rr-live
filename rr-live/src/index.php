<!DOCTYPE html>
<html>
<head>
	<title>Runes Reforged Live</title>
	<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
	<link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.grey-indigo.min.css">
	<link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Roboto:300,400,500,700" type="text/css">
	<script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
	<link rel="stylesheet" type="text/css" href="index.css">
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, user-scalable=yes">
	<meta name="theme-color" content="#212121">
</head>
<body class="mdl-color--grey-800">
<div id="loader" class="mdl-spinner mdl-spinner--single-color mdl-js-spinner is-active mdl-color-text--grey-200"></div>
<div class="mdl-layout mdl-js-layout">
  <header class="mdl-layout__header">
    <div class="mdl-layout__header-row">
      <span class="mdl-layout-title">Runes Reforged Live</span>
      <div class="mdl-layout-spacer"></div>
    </div>
  </header>
</div>
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
						<button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect">
							<i class="material-icons">more_horiz</i>
						</button>
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
						<button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect">
							<i class="material-icons">more_horiz</i>
						</button>
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
						<button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect">
							<i class="material-icons">more_horiz</i>
						</button>
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
						<button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect">
							<i class="material-icons">more_horiz</i>
						</button>
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
						<button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect">
							<i class="material-icons">more_horiz</i>
						</button>
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
</main>
<noscript>
	<div class="sorry shown">
		<div class="sorry-text"><h5>Please activate Javascript</h5><p>Javascript is required for this application</p></div>
	</div>
</noscript>
<div class="sorry">
	<div class="sorry-text"><h5>Sorry,</h5><p>this site doesn't work for mobile devices yet, please use a desktop computer or laptop</p></div>
</div>
<div id="error" class="sorry">
	<div class="sorry-text"><h5 id="error-title"></h5><p id="error-message"></p></div>
</div>
<div id="error-snackbar" class="mdl-js-snackbar mdl-snackbar">
  <div class="mdl-snackbar__text"></div>
  <button class="mdl-snackbar__action" type="button"></button>
</div>
<script type="text/javascript" src="errors.js"></script>
<script type="text/javascript" src="features.js"></script>
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script type="text/javascript" src="loading.js"></script>
<script type="text/javascript" src="configuration.js"></script>
<script type="text/javascript" src="statistics.js"></script>
<script type="text/javascript" src="data.js"></script>
<script type="text/javascript" src="map.js"></script>
<script type="text/javascript" src="runes.js"></script>
<script type="text/javascript" src="helper.js"></script>
<script type="text/javascript">
	loading.init();
	loading.start();
</script>
<footer class="mdl-mini-footer mdl-color--grey-900">
  <div class="mdl-mini-footer__left-section">
    <div class="mdl-logo">Runes Reforged Live</div>
    <p>This site isn't endorsed by Riot Games and doesn't reflect the views or opinions of Riot Games or anyone officially involved in producing or managing League of Legends. League of Legends and Riot Games are trademarks or registered trademarks of Riot Games, Inc. League of Legends &copy; Riot Games, Inc.</p>
    <ul class="mdl-mini-footer__link-list">
      <li><a href="#">Imprint</a></li>
      <li><a href="#">Contact</a></li>
    </ul>
  </div>
</footer>
</body>
</html>