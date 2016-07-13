<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"LessonID" : lesson id}
	//return 
	//[
	//	{
	//		"Status" : 200, 
	//		"Code" : "code"
	//	}
	//]
	//OR
	//[{'Status' : 409, 'Message' : 'error message'}]
	$json = file_get_contents('php://input');
	$decodedJSON = json_decode($json);
	$conn = mysqli_connect("localhost", "root", "TechroomPass1234", "Techroom");
	if (!$conn) {
   	 	echo "[{'Status' : 409, 'Message' : 'Failed to connect to database'}]";
   	 	exit;
	}
	
	$ret = array();
	$mainObj = array("Status" => 200);
	$discussions = array();
	
	$query = "SELECT Code FROM Lesson WHERE ID = $decodedJSON->LessonID";
	$result = mysqli_query($conn, $query);
	$obj = mysqli_fetch_object($result);
	$code = $obj->Code;
	
	echo "[{'Status' : 200, 'Code' : '$code'}]";
	exit;
?>