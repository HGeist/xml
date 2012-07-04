<?php
	error_reporting(0);
	session_start();
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> 
<html>
	<head>
		<title>POInter</title>
		<link rel="stylesheet" type="text/css" href="css/styles.css">
	</head>
	<body id="index">
		<div id="wrapper">
			<header id="header">
				<div class="inside">
					<h1>POInter</h1>
					<h3>Tracks and Landmarks..</h3>
				</div>
			</header>
			<div id="container">
				<section id="main">
					<div class="inside">
						<form action="search.php" method="POST">
						<p class="search">Search: <br/>
							<input type="text" name="searchInput" />
						</p> 
						
						<input class="submit" type="submit" value="Los!"/><br/>
						
						<p class="optInf">Orte in der N&auml;he:</p>
						
						<div class="options">
							<input type="checkbox" name="festival" value="festival">&nbsp;Festival<br>
							<input type="checkbox" name="gebetsstaette" value="gebetsstaette">&nbsp;Gebetsst&auml;tte<br>
							<input type="checkbox" name="monuments" value="monuments">&nbsp;Monumente<br>
							<input type="checkbox" name="museum" value="museum">&nbsp;Museum<br>
							<input type="checkbox" name="parks" value="parks">&nbsp;Park<br>
							<input type="checkbox" name="ruinen" value="ruinen">&nbsp;Ruinen<br>
							<input type="checkbox" name="see" value="see">&nbsp;See<br>
							<input type="checkbox" name="sehenswuerdig" value="sehenswuerdig">&nbsp;Sehensw&uuml;rdigkeit<br>
							<input type="checkbox" name="sportVenue" value="sportVenue">&nbsp;Sportst&auml;tte<br>
							<input type="checkbox" name="theatreOpera" value="theatreOpera">&nbsp;Theater / Oper<br>
							<input type="checkbox" name="towers" value="towers">&nbsp;T&uuml;rme<br>
						</div>

						</form>
					</div>
				</section>
			</div>
			<footer id="footer">
				<div class="inside">
					<div><a href="">Kontakt</a> | <a href="">Impressum</a> | <a href="">Hilfe</a> | <a href="">Datenschutz</a> | <a href="">Copyright &copy; Hints &amp; Pointers SE 2012. Alle Rechte vorbehalten.</a></div>
				</div>
			</footer>
		</div>
	</body>
</html>
