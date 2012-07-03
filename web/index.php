<?php
	error_reporting(0);
?>
<html>
	<head>
		<title>POInter</title>
		<link rel="stylesheet" type="text/css" href="css/styles.css">
	</head>
	<body>
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
						<p>Search: <br/>
							<input type="text" name="searchInput" />
						</p> 
						
						<input type="checkbox" name="check[]" value="jogging">&nbsp;Jogging<br>
						
						<input type="checkbox" name="check[]" value="biking">&nbsp;Biking<br>

						<input type="submit" /> <input type="reset" />

						</form>
					</div>
				</section>
			</div>
			<footer id="footer">
				<div class="inside">
				
				</div>
			</footer>
		</div>
	</body>
</html>
