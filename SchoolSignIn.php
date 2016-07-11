<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"SchoolName" : "School Name", "Country" : "country of school", "Password" : "password entered"}
	//Returns JSON of the form
	//[{"Status" : 200}]
	//OR
	//[{"Status" : 409, "Message" : "error"}]
	$json = file_get_contents('php://input');
	$decodedJSON = json_decode($json);
	//Connect to database
	$conn = mysqli_connect("localhost", "root", "TechroomPass1234", "Techroom");
	if (!$conn) {
		//Failed to connect to database (error)
		echo "[{'Status' : 409, 'Message' : 'Failed to connect to database'}]";
		exit;
	}
	$query = "SELECT ID FROM Country WHERE Name = '$decodedJSON->Country'";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		echo "[{'Status' : 409, 'Message' : 'Country $decodedJSON->Country does not exist'}]";
		exit;
	}
	$result = mysqli_query($conn, $query);
	$obj = mysqli_fetch_object($result);
	$query = "SELECT ID FROM School WHERE
				Country = $obj->ID AND
				Password = '$decodedJSON->Password' AND
				Name = '$decodedJSON->SchoolName'";
				
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		echo "[{'Status' : 409, 'Message' : 'Incorrect school name, password, or country'}]";
	}
	mysqli_close($conn);
	echo "[{'Status' : 200}]";
	exit;
?>