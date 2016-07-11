<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"Username" : "username", "Password" : "password", "SchoolName" : "School name", "Country" : "Country name"}
	//return [{'Status' : 200}] or [{'Status' : 409, 'Message' : 'message'}]
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
		mysqli_close($conn);
		echo "[{'Status' : 409, 'Message' : 'Country $decodedJSON->Country does not exist'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$countryID = $obj->ID;

	$query = "SELECT ID FROM School WHERE Name = '$decodedJSON->SchoolName' AND Country = $countryID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		mysqli_close($conn);
		echo "[{'Status' : 409, 'Message' : 'No school $decodedJSON->SchoolName exists in the country $decodedJSON->Country'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$schoolID = $obj->ID;
	
	$query = "SELECT * FROM Student WHERE Username = '$decodedJSON->Username' AND Password = '$decodedJSON->Password' AND School = $schoolID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		$query = "SELECT * FROM Teacher WHERE Username = '$decodedJSON->Username' AND Password = '$decodedJSON->Password' AND School = $schoolID";
		$result = mysqli_query($conn, $query);
		if (mysqli_num_rows($result) == 0) {
			mysqli_close($conn);
			echo "[{'Status' : 409, 'Message' : 'Incorrect username, password, school, or country'}]";
			exit;
		}
	}
	
	mysqli_close($conn);
	echo "[{'Status' : 200}]";
	exit;
	
?>