<?php
	header('Content-Type: application/json');
	$json = file_get_contents('php://input');
	$decodedJSON = json_decode($json);
	//Connect to database
	$conn = mysqli_connect("localhost", "root", "TechroomPass1234", "Techroom");

	if (!$conn) {
		echo "[{'Status' : 409, 'Message' : 'Failed to connect to database'}]";
		exit;
	}

	foreach ($decodedJSON as $value) {
		$sql= "SELECT * FROM Country WHERE Name = ('$value->Name')";
		$result = mysqli_query($conn, $sql);
		if (mysqli_num_rows($result) != 1) {
			echo "[{'Status' : 409, 'Message' : 'SQL query error'}]";
			mysqli_close($conn);
			exit;
		}
	}
	
	mysqli_close($conn);
	echo "[{'Status' : 200}]";
	exit;
?>