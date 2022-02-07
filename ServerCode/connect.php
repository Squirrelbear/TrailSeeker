<?php

$mysql_host = "mysql13.000webhost.com";
$mysql_database = "a5716545_trail";
$mysql_user = "a5716545_admin";
$mysql_password = "abc123";

$con = mysql_connect($mysql_host,$mysql_user,$mysql_password);
if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }
?>