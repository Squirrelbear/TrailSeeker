<?php
include 'connect.php';

$summary = $_POST['summary'];
$fulldata = $_POST['fulldata'];

mysql_select_db($mysql_database, $con);

$insertquery = "INSERT INTO traildata (summary, fulldata)"
				. " VALUES ('" . $summary . "', '" . $fulldata . "')";

$result = mysql_query($insertquery);

if(!$result)
{
	echo "ERROR";
}
else
{
	echo "SUCCESS";
	/*while($row = mysql_fetch_array($result))
	  {
	  echo "SUCCESS " . $row['id'];
	  break;
	  }*/
}

include 'closeconnect.php';
?>