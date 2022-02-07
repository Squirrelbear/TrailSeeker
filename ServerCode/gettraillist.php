<?php
include 'connect.php';

mysql_select_db($mysql_database, $con);

$result = mysql_query("SELECT id,summary FROM traildata");

$count = 0;

echo "<traillist>";
while($row = mysql_fetch_array($result))
  {
  $count++;
  $summary = $row['summary'];
  $uid = $row['id'];
  $modsummary = str_replace("<summary>", "<summary uid='" . $uid . "'>", $summary);
  echo $modsummary;
  }
echo "</traillist>";

include 'closeconnect.php';
?>