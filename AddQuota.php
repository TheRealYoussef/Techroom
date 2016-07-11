<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"SchoolName" : "Name of school", "Country" : "School's Country"};
	//return
	//[{"Status" : 409, "Message" : "error"}] OR [{"Status" : 200}]
	$json = file_get_contents('php://input');
	$decodedJSON = json_decode($json);
	$conn = mysqli_connect("localhost", "root", "TechroomPass1234", "Techroom");
	if (!$conn) {
   	 	echo "[{'Status' : 409, 'Message' : 'Failed to connect to database'}]";
   	 	exit;
	}
	$query = "SELECT ID FROM Country WHERE 
					Name = '$decodedJSON->Country'";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		echo "[{'Status' : 409, 'Message' : 'Country $decodedJSON->Country does not exist'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$countryID = $obj->ID;
	$query = "SELECT Quota FROM School WHERE Name = '$decodedJSON->SchoolName' AND Country = $countryID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		echo "[{'Status' : 409, 'Message' : 'No school $decodedJSON->SchoolName exists in the country $decodedJSON->Country'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$quota  = $obj->Quota;
	$quota = $quota + 20;
	$query = "UPDATE School set Quota = $quota WHERE Name = '$decodedJSON->SchoolName' AND Country = $countryID";
	mysqli_query($conn, $query);
	mysqli_close($conn);
	echo "[{'Status' : 200}]";
	exit;
?>