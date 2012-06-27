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

$searchIn =  $_POST["searchInput"];
$trackId = $_GET["trackId"];
//$trackType =  $_POST["check"];

if ($searchIn != '') doSearch($searchIn);
else if ($trackId != '') getTrackInfo($trackId);

function getTrackInfo($trackId) {
	echo $trackId;
	
	try {
		$session = new Session("localhost", 1984, "admin", "admin");
		
		$session->execute("OPEN db-crawl");
		
		$query = $session->query('
			let $my-doc := doc("db-crawl.xml")
			return
			'
		);
		print $query->execute()."<br/>";
		$query->close();
		
	} catch (Exception $e) {
	  // print exception
	  print $e->getMessage();
	}
}

function doSearch($searchIn) {
	try {
	  // initialize timer
	  $start = microtime(true);

	  // var from HTML
	  
	  
	  // create session
	  $session = new Session("localhost", 1984, "admin", "admin");
		
	  // dom document
	  //$dom = new DOMDocument();


	  // open test.xml
	  //$session->execute("CREATE DB db-crawl db-crawl.xml");
	  $session->execute("OPEN db-crawl");
	  // query
		$input = 'declare variable $search external;
		let $my-doc := doc("db-crawl.xml")
		return
		<html>
			<head>
				<title>Tracks</title>
				<!--<script src="test.js" type="text/javascript" />-->
			</head>
			<body>
			<table border="1">
			<thead>
			  <tr>
				  <th>Track Title</th> 
				  <th>CreateDate</th>
				  <th>TrackType</th>
			  </tr>
			</thead>
			<tbody>{
				
			   for $term at $count in 
				 for $item in $my-doc//tracks/track
					 let $track-name := $item/title/text()'.
					 //let $track-type := $item/trackTypes/trackType/*
					 'where contains(upper-case($track-name), upper-case($search))'. //and contains($track-type, "biking")
					 
					'order by upper-case($track-name)
				 return $item
			   return
				 <tr> {if ($count mod 2) then (attribute bgcolor {"Lightblue"}) else ()}
				   <td><a href="search.php?trackId={$term/fileId/text()}" title="{$term/title/text()}">{$term/title/text()}</a></td>
				   <td>{$term/createdDate/text()}</td>
				   <td>{$term/trackTypes/node()}</td>
				 </tr>
				 
			   }		   
			   </tbody>
			 </table>
		   </body>
		</html>';
	  $query = $session->query($input);
	  $query->bind("search",$searchIn);
	  print $query->execute()."<br/>";
	  $query->close();
	  
	  //$session->execute("DROP DB db-crawl");
	  
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


?>