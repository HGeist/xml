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

session_start();

// if (isset($_POST["searchInput"])) $searchIn =  $_POST["searchInput"];
if (isset($_GET["trackId"])) $trackId = $_GET["trackId"];

foreach($_POST as $name => $val) {
	// print_r("post: " . $name . ", " . $val);
	if ($name === "searchInput") {
		$searchIn = $val;
	}
	else {
		$_SESSION["OPTS"][$name] = $val;
	}
}


// if(isset($trackId)) {
	// print("<pre>");
// print_r($_SESSION);
// print("</pre>");
// die("session");
// }

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
		$session->execute("DROP DB db-crawl");
		$session->execute("CREATE DB db-crawl gpsies_pois_part.xml");
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
	print('<!DOCTYPE html>
	<html>
		<head>
		<title>Tracks</title>			
		</head>
		<body">
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
			switch($node->nodeName) {
				case "fileId":
					$fileId = $node->nodeValue;
					// print("id: ".$fileId."<br>");
					break;
				case "kmlLink":
					$link = $node->nodeValue;
					// print("link: ".$link."<br>");
					break;
			}
		}
		
		//save relevant attribute node infos
		foreach($track->attributes as $attrName => $attrNode) {
			switch($attrName) {
				case "trackName":
					$title = $attrNode->nodeValue;
					// print("title: ".$title."<br>");
					break;
				case "createTimestamp":
					$created = $attrNode->nodeValue;
					// print("created: ".$created."<br>");
					break;
			}
		}
		
		//print a table row
		print ('<tr>
			<td><a href="search.php?trackId='.$fileId.'" title="'.$title.'">'.$title.'</a></td>');
		print ('<td>'.$created.'</td></tr>');

	}
	
	print ('				</tbody>
			</table>
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
	
	$pois = array();
	
	foreach($track->item(0)->childNodes as $node) {
		switch($node->nodeName) {
			case "kmlLink":
				$link = $node->nodeValue;
				break;
			case "points":
				foreach($node->childNodes as $cnode) {
					switch($cnode->nodeName) {
						case "poi":
							
							if (array_key_exists($cnode->getAttribute("category"), $_SESSION["OPTS"])) {
								
								$pois[] = $cnode;
							}
					}
				}
		}
	}
	
	//print out the html document
	print('<!DOCTYPE html><html>
		<head>
			<title>Track</title>
			<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
			<style type="text/css">
				html { height: 100% }
				body { height: 100%; margin: 0px; padding: 0px }
				#map_canvas { height: 100% }
			</style>
			<script type="text/javascript" src="https://maps.google.com/maps/api/js?sensor=false"></script>
			<script type="text/javascript">
			var markers = new Array();
			var map;
			
			function initialize() {
				var latlng = new google.maps.LatLng(49.97823380, 11.69609640);
				var myOptions = {
					zoom: 8,
					center: latlng,
					mapTypeId: google.maps.MapTypeId.ROADMAP
				};
				map = new google.maps.Map(document.getElementById("map_canvas"),
				myOptions);
				
				var track = new google.maps.KmlLayer("'.htmlspecialchars($link).'");
				track.setMap(map);
				
				poiLocs = [
				');
				
	foreach($pois as $poi) {
		echo "new google.maps.LatLng(".$poi->getAttribute("lat").",".$poi->getAttribute("lon")."),\n";
	}
	
	echo "];\n";
	
	$i = 0;
	
	foreach($pois as $poi) {
		echo "markers[".$i."] = new google.maps.Marker({position: poiLocs[".$i."], map: map, title: \"".$poi->getAttribute("title")."\"});\n";
		$i++;
	}
				
	print ('
			}
			</script>			
		</head>
		<body onload="initialize()">
			<div id="map_canvas" style="width:100%; height:100%"></div>
		</body></html>');
}


?>