<?php require 'basex.php' ?>
<?php
	$session = new Session("localhost", 1984, "admin", "admin");
	$dom = new DOMDocument();
	
	$session->execute("create db database <root><user>
<username>Username1</username><password>Password1</password>
</user><user><username>Username2</username><password>Password2</password></user></root>");
	
	// perform command and print returned string
  $dom->loadXML($session->execute("xquery ."));
  // print nodes info
  getNodesInfo($dom->firstChild);

  // close session
  $session->close();
	
	// print info of node and subnodes
function getNodesInfo($node)
{
  if ($node->hasChildNodes())
  {
  $subNodes = $node->childNodes;
  foreach ($subNodes as $subNode)
    {
if (($subNode->nodeType != 3)&&($subNode->nodeType != 8)) {
      print "Node name: ".trim($subNode->nodeName)."<br />";
print "Node value: ".trim($subNode->nodeValue)."<br />";
}
getNodesInfo($subNode);
}
  }
}
?>
<?php echo "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>
<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en'>"; ?>
 <head>
   <title>Beispiel</title>
 </head>
 <body>
test
 </body>
</html>