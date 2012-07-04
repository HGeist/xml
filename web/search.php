<?php
/*
* This example shows how database commands can be executed
* and the result of a query can be processed in a dom document.
*
* Documentation: http://docs.basex.org/wiki/Clients
*
* (C) BaseX Team 2005-12, BSD License
*/
include("BaseXClient.php");

if (isset($_POST["searchInput"])) $searchIn =  $_POST["searchInput"];
if (isset($_GET["trackId"])) $trackId = $_GET["trackId"];
//$trackType =  $_POST["check"];

error_reporting(0);

if ($searchIn != '') doSearch($searchIn);
else if ($trackId != '') showTrack($trackId);

function doSearch($searchIn) {

	try {
		// initialize timer
		$start = microtime(true);

		// var from HTML

		// create session
		$session = new Session("localhost", 1984, "admin", "admin");

		// open test.xml
		// $session->execute("DROP DB db-crawl");
		// $session->execute("CREATE DB db-crawl gpsies_piece.xml");
		$session->execute("OPEN db-crawl");
		
		$doc = new DOMDocument();
		$doc->loadXML("<root/>");

		$f = $doc->createDocumentFragment();
		$f->appendXML($session->execute("xquery //track[@trackName[contains(lower-case(.), lower-case('".$searchIn."'))]]"));

		$doc->documentElement->appendChild($f);
		
		$xpath = new DOMXpath($doc);
		
		showResults($xpath->query("//track"));
	  
	  // close session
	  $session->close();
	  
	  // print time needed
	  $time = (microtime(true) - $start) * 1000;
	  print "<br/>$time ms\n";


	} catch (Exception $e) {
		// print exception
		print $e->getMessage();
	}
}

function showResults(DOMNodeList $tracks) {

	//print out the html document
	print('
	<!doctype html>
	<html>
		<head>
			<title>Tracks</title>			
			<link rel="stylesheet" type="text/css" href="css/styles.css">
		</head>
		<body>
			<div id="wrapper">
				<header id="header">
					<div class="inside">
						<h1>POInter</h1>
						<h3>Search results</h3>
					</div>
				</header>
				<div id="container">
					<section id="main">
						<div class="inside">
							<table border="1">
								<thead>
									<tr>
										<th>Track Title</th> 
										<th>CreateDate</th>	
									</tr>
								</thead>
								<tbody>');
				
	// die("tracklaenge: ".$tracks->length);

	//loop through tracks and put them out row per row
	foreach($tracks as $track) {
		//save relevant child node infos
		foreach($track->childNodes as $node) {
			if($node->nodeName == "fileId") {
				$fileId = $node->nodeValue;
			} else if ($node->nodeName == "kmlLink") {
				$link = $node->nodeValue;
			}
		}
		
		//save relevant attribute node infos
		foreach($track->attributes as $attrName => $attrNode) {
			if($attrName == "trackName") {
				$title = $attrNode->nodeValue;
			} else if($attrName == "createTimestamp") {
				$created = $attrNode->nodeValue;
			}
		}
		
		//print a table row
		print ('
			<tr>
				<td>
					<a href="search.php?trackId='.$fileId.'" title="'.$title.'">'.$title.'</a>
				</td>
				<td>'.$created.'</td>
			</tr>');

	}
	
	print ('
						</tbody>
					</table>
				</div>
			</div>
		</body>
	</html>');
}

function showTrack($trackId) {

	// create session
	$session = new Session("localhost", 1984, "admin", "admin");

	$session->execute("OPEN db-crawl");

	$doc = new DOMDocument();
	$doc->loadXML("<root/>");
	$f = $doc->createDocumentFragment();

	$f->appendXML($session->execute("xquery //track[fileId[text() = '".$trackId."']]"));
	$doc->documentElement->appendChild($f);
	
	$xpath = new DOMXpath($doc);

	$track = $xpath->query("//track");
	
	// close session
	$session->close();
	
	foreach($track->item(0)->childNodes as $node) {
		if($node->nodeName == "kmlLink") {
			$link = $node->nodeValue;
		}
	}
	
	//print out the html document
	print('
	<!doctype html>
	<html>
		<head>
			<title>Track</title>
			<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
			<style type="text/css">
				html { height: 100% }
				body { height: 100%; margin: 0px; padding: 0px }
				#map_canvas { width: 100%; height: 100% }
			</style>
			<script src="https://maps.google.com/maps/api/js?sensor=false"></script>
			<script>
			function initialize() {
				var latlng = new google.maps.LatLng(49.97823380, 11.69609640);
				var myOptions = {
					zoom: 8,
					center: latlng,
					mapTypeId: google.maps.MapTypeId.ROADMAP
				};
				var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
				var track = new google.maps.KmlLayer("'.htmlspecialchars($link).'");
				track.setMap(map);
			}
			</script>
			<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
			<script src="js/jquery.twitter.search.js"></script>
		</head>
		<body onload="initialize()">
			<div id="map_canvas"></div>
			<div id="poitable"></div>
		</body>
	</html>');
}

?>
