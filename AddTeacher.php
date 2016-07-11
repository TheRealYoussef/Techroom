<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"Username" : "username", "Password" : "Password", "SchoolName": "school", "Country": "country"}
	//return
	//[{"Status" : 409, "Message" : "Error message"}] OR [{"Status" : 200}]
	$json = file_get_contents('php://input');
	$decodedJSON = json_decode($json);
	
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
	$obj = mysqli_fetch_object($result);
	$countryID = $obj->ID;
	
	$query = "SELECT ID, Quota FROM School WHERE Name = '$decodedJSON->SchoolName' AND Country = $countryID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		mysqli_close($conn);
		echo "[{'Status' : 409, 'Message' : 'No school $decodedJSON->SchoolName exists in the country $decodedJSON->Country'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$schoolID  = $obj->ID;
	$quota = $obj->Quota;
	
	$query = "SELECT * FROM Student WHERE Username = '$decodedJSON->Username' AND School = $schoolID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) != 0) {
		mysqli_close($conn);
		echo "[{'Status' : 409,'Message':'Username already exists'}]";
		exit;
	}
	
	$query = "SELECT * FROM Teacher WHERE Username = '$decodedJSON->Username' AND School = $schoolID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) != 0) {
		mysqli_close($conn);
		echo "[{'Status' : 409, 'Message':'Username already exists'}]";
		exit;
	}
	
	$query = "INSERT INTO Teacher (Username, Password, Name, School, Image, Orientation) VALUES ('$decodedJSON->Username','$decodedJSON->Password','$decodedJSON->Username', $schoolID, 0, 1)";
	mysqli_query($conn, $query);
	
	$quota = $quota - 1;
	$query = "UPDATE School set Quota = $quota WHERE ID = $schoolID";
	mysqli_query($conn, $query);
	
	mysqli_close($conn);
	echo "[{'Status' : 200}]";
	exit;
?>