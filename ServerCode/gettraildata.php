<?php
include 'connect.php';

$id = $_GET['id'];

mysql_select_db($mysql_database, $con);

$result = mysql_query("SELECT id, fulldata FROM traildata WHERE id = " . $id);

$count = 0;
while($row = mysql_fetch_array($result))
  {
  $count++;
  echo preg_replace("/summary/", "summary uid=\"". $row['id'] . "\"", $row['fulldata'], 1);
  }

  if($count == 0)
  {
  	echo "ERROR";
  }

include 'closeconnect.php';
?>